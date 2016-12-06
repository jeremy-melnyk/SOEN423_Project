package udp_parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.omg.CORBA.ORB;

import global.Constants;
import jeremy_replica.log.ILogger;
import jeremy_replica.log.CustomLogger;
import jeremy_replica.log.TextFileLog;
import packet.BookFlightOperation;
import packet.BookFlightReply;
import packet.EditFlightRecordOperation;
import packet.EditFlightRecordReply;
import packet.ExecuteOperationLogOperation;
import packet.ExecuteOperationLogReply;
import packet.GetBookedFlightCountOperation;
import packet.GetBookedFlightCountReply;
import packet.OperationParameters;
import packet.Packet;
import packet.ReplicaAliveOperation;
import packet.ReplicaAliveReply;
import packet.Operation;
import packet.TransferReservationOperation;
import packet.TransferReservationReply;
import udp.UdpHelper;


public abstract class UdpParserBase implements Runnable {
	private final int BUFFER_SIZE = 50000;
	private final int THREAD_POOL_SIZE = Integer.MAX_VALUE;
	private final ExecutorService threadPool;
	private ILogger logger;
	private final String tag;
	protected final ORB orb;
	protected final int port;
	private int lastreceivednumber = 0;
	private Set<Integer> setofreceivednumbers = new HashSet<Integer>();
	private PriorityQueue<DatagramPacket> holdbackqueue = new PriorityQueue<DatagramPacket>(new PQSort());
	private final int groupportnumber = 9876;
	private Thread sequencerLogThread;
	
	public UdpParserBase(ORB orb, int port) {
		super();
		this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		this.orb = orb;
		this.port = port;
		this.tag = "UDP_PARSER_"  + port;
		this.logger = new CustomLogger(new TextFileLog());
		this.sequencerLogThread = initSequencerLogThread();
		this.sequencerLogThread.start();
	}

	public int getPort() {
		return port;
	}

	public ORB getOrb() {
		return orb;
	}
	
	@Override
	public void run() {
		serveRequests();
	}
	
	public Packet processPacket(Packet packet) {
		Operation replicaOperation = packet.getReplicaOperation();
		OperationParameters operationParameters = packet.getOperationParameters();
		this.logger.log("UDP_PARSER", "PACKET_RECEIVED", replicaOperation.toString());
		switch (replicaOperation) {
		case BOOK_FLIGHT:
			BookFlightOperation bookFlightOperation = (BookFlightOperation) operationParameters;
			BookFlightReply bookFlightResult = bookFlight(bookFlightOperation);
			this.logger.log(tag, "BOOK_FLIGHT_REPLY", bookFlightResult.toString());
			return new Packet(Operation.BOOK_FLIGHT, bookFlightResult);
		case BOOKED_FLIGHTCOUNT:
			GetBookedFlightCountOperation getBookedFlightCountOperation = (GetBookedFlightCountOperation) operationParameters;
			GetBookedFlightCountReply bookedFlightCountResult = getBookedFlightCount(getBookedFlightCountOperation);
			this.logger.log(tag, "GET_BOOKED_FLIGHT_COUNT_REPLY", bookedFlightCountResult.toString());
			return new Packet(Operation.BOOKED_FLIGHTCOUNT, bookedFlightCountResult);
		case EDIT_FLIGHT:
			EditFlightRecordOperation editFlightRecordOperation = (EditFlightRecordOperation) operationParameters;
			EditFlightRecordReply editFlightRecordOperationResult = editFlightRecord(editFlightRecordOperation);
			this.logger.log(tag, "EDIT_FLIGHT_RECORD_REPLY", editFlightRecordOperationResult.toString());
			return new Packet(Operation.EDIT_FLIGHT, editFlightRecordOperationResult);
		case TRANSFER_RESERVATION:
			TransferReservationOperation transferReservationOperation = (TransferReservationOperation) operationParameters;
			TransferReservationReply transferReservationOperationResult = transferReservation(transferReservationOperation);
			this.logger.log(tag, "TRANSFER_RESERVATION_REPLY", transferReservationOperationResult.toString());
			return new Packet(Operation.TRANSFER_RESERVATION, transferReservationOperationResult);
		case REPLICA_ALIVE:
			ReplicaAliveOperation replicaAliveOperation = (ReplicaAliveOperation) operationParameters;
			ReplicaAliveReply replicaAliveReply = replicaAlive(replicaAliveOperation);
			this.logger.log(tag, "REPLICA_ALIVE_REPLY", replicaAliveReply.toString());
			return new Packet(Operation.REPLICA_ALIVE, replicaAliveReply);
		case EXECUTE_OPERATION_LOG:
			ExecuteOperationLogOperation executeOperationLogOperation = (ExecuteOperationLogOperation) operationParameters;
			ExecuteOperationLogReply executeOperationLogReply = executeOperationLog(executeOperationLogOperation);
			this.logger.log(tag, "EXECUTE_OPERATION_LOG_REPLY", executeOperationLogReply.toString());
			return new Packet(Operation.EXECUTE_OPERATION_LOG, executeOperationLogReply);
		default:
			break;
		}
		return null;
	}

	protected abstract BookFlightReply bookFlight(BookFlightOperation bookFlightOperation);

	protected abstract GetBookedFlightCountReply getBookedFlightCount(GetBookedFlightCountOperation getBookedFlightCountOperation);

	protected abstract EditFlightRecordReply editFlightRecord(EditFlightRecordOperation editFlightRecordOperation);

	protected abstract TransferReservationReply transferReservation(TransferReservationOperation transferReservation);

	private ReplicaAliveReply replicaAlive(ReplicaAliveOperation replicaAliveOperation) {
		return new ReplicaAliveReply(true, port);
	}
	
	private Thread initSequencerLogThread() {
		return new Thread(() -> {
			//requestSequencerLog();
			replicaManagerRequests();
		});
	}
	
	private void replicaManagerRequests() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(port);
			while (true) {
				byte[] buffer = new byte[BUFFER_SIZE];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				threadPool.execute(new UdpParserReplicaManagerPacketDispatcher(this, packet));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null){
				socket.close();
			}
		}
	}
	
	private ExecuteOperationLogReply executeOperationLog(ExecuteOperationLogOperation executeOperationLogOperation) {
		ArrayList<Packet> operationLog = executeOperationLogOperation.getOperationLog();
		for(Packet packet : operationLog){
			processPacket(packet);
		}
		return new ExecuteOperationLogReply();
	}
	
	private void serveRequests() {	
		MulticastSocket mSocket = null;
		try {
			mSocket = new MulticastSocket(groupportnumber);
			InetAddress aGroup = InetAddress.getByName(Constants.MULTICAST_ADDRESS);
			mSocket.joinGroup(aGroup);

			while (true) {
				byte[] buffer = new byte[BUFFER_SIZE];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				mSocket.receive(request);

				// delivers packet
				this.deliverMulticast(request);
			}
		} catch (Exception e) {
			this.logger.log("UDP_PARSER", "EXCEPTION", e.getMessage());
		}
	}

	public int getLastreceivednumber() {
		return lastreceivednumber;
	}

	public synchronized void setLastreceivednumber(int lastreceivednumber) {
		this.lastreceivednumber = lastreceivednumber;
	}
	
	public void multicastToGroup(Packet packet) {

		MulticastSocket aSocket = null;
		try {

			// creates MulticastSocket with InetAddress and ServerPort
			aSocket = new MulticastSocket();
			InetAddress aGroup = InetAddress.getByName(Constants.MULTICAST_ADDRESS);

			// join group
			aSocket.joinGroup(aGroup);

			// converts packet to send to group
			byte[] m = UdpHelper.getByteArray(packet);
			DatagramPacket request = new DatagramPacket(m, UdpHelper.getByteArray(packet).length, aGroup,
					groupportnumber);

			// sends packet
			aSocket.send(request);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}

	public void deliverMulticast(DatagramPacket receivedpacket) {
		// check if this is a duplicate packet
		Packet convertedpacket = (Packet) UdpHelper.getObjectFromByteArray(receivedpacket.getData());
		int receivedseqnumber = convertedpacket.getSequencernumber();

		if (this.isDuplicate(receivedseqnumber) == false) {
			// delivers to the rest of the group to ensure multicast reliability
			this.multicastToGroup(convertedpacket);

			// adds to holdbackqueue
			holdbackqueue.offer(receivedpacket);

			// continuously delivers until holdback queue is empty or there is a
			// missing packet
			while (this.checkHoldBackQueue() == true) {
				// pops the latest datagrampacket from the queue and increments
				// the last
				// received number
				DatagramPacket poppacket = holdbackqueue.poll();
				Packet deliveredpacket = (Packet) UdpHelper.getObjectFromByteArray(poppacket.getData());
				this.setLastreceivednumber(deliveredpacket.getSequencernumber());

				this.logger.log("UDP_PARSER", "SERVE_REQUEST", poppacket.toString());
				threadPool.execute(new UdpParserPacketDispatcher(this, poppacket));
			}
		}
	}

	public boolean isDuplicate(int seqnumber) {
		if (setofreceivednumbers.contains(seqnumber)) {
			return true;
		} else {
			setofreceivednumbers.add(seqnumber);
			return false;
		}
	}

	// checks if the next correct packet is within the holdbackqueue
	public boolean checkHoldBackQueue() {
		Packet convertedpacket=(Packet)UdpHelper.getObjectFromByteArray(holdbackqueue.peek().getData());
		int receivedseqnumber = convertedpacket.getSequencernumber();
		int expectedSequenceNumber = this.getLastreceivednumber() + 1;
		if (receivedseqnumber == expectedSequenceNumber) {
			return true;
		} else {
			return false;
		}
	}

	// for comparing packets in the priority queue
	static class PQSort implements Comparator<DatagramPacket> {

		public int compare(DatagramPacket one, DatagramPacket two) {
			Packet onepacket=(Packet)UdpHelper.getObjectFromByteArray(one.getData());
			Packet twopacket=(Packet)UdpHelper.getObjectFromByteArray(two.getData());
			return onepacket.getSequencernumber() - twopacket.getSequencernumber();
		}
	}

	// receives
	@SuppressWarnings("unchecked")
	public void requestSequencerLog() {
		DatagramSocket aSocket = null;

		try {
			// creates socket to receive sequencer log of packets
			aSocket = new DatagramSocket(port);
			byte[] buffer = new byte[BUFFER_SIZE];
			DatagramPacket request = new DatagramPacket(buffer, buffer.length);

			// receive sequencer log of packets
			aSocket.receive(request);

			// converts to List<packet> from byte array
			List<Packet> receivedseqlog = null;

			ByteArrayInputStream bis = new ByteArrayInputStream(request.getData());
			ObjectInput in = null;
			try {
				in = new ObjectInputStream(bis);
				receivedseqlog = (List<Packet>) in.readObject();
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException ex) {
					// ignore close exception
				}
			}

			// adds list<packet> to priority holdback queue
			for (int i = 0; i < receivedseqlog.size(); i++) {
				
				request.setData(UdpHelper.getByteArray(receivedseqlog.get(i)));
				DatagramPacket storeddatagram=new DatagramPacket(request.getData(),request.getLength(),request.getAddress(),request.getPort());
				
				holdbackqueue.offer(storeddatagram);
			}

			// sends replicamanager an ACK that it has received the sequencer
			// logs and has successfully added them to the holdback queue
			request.setData("ACK".getBytes());

			DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(),
					request.getPort());
			aSocket.send(reply);

		} catch (Exception e) {

		}

	}
	
}
