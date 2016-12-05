package udp_parser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.omg.CORBA.ORB;

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

public abstract class UdpParserBase implements Runnable {
	private final int BUFFER_SIZE = 50000;
	private final int THREAD_POOL_SIZE = Integer.MAX_VALUE;
	private final ExecutorService threadPool;
	private ILogger logger;

	protected final ORB orb;
	protected final int port;

	public UdpParserBase(ORB orb, int port) {
		super();
		this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		this.orb = orb;
		this.port = port;
		this.logger = new CustomLogger(new TextFileLog());
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
		this.logger.log("UDP_PARSER", "SWITCH_STATEMENT", packet.toString());
		switch (replicaOperation) {
		case BOOK_FLIGHT:
			BookFlightOperation bookFlightOperation = (BookFlightOperation) operationParameters;
			BookFlightReply bookFlightResult = bookFlight(bookFlightOperation);
			return new Packet(Operation.BOOK_FLIGHT, bookFlightResult);
		case BOOKED_FLIGHTCOUNT:
			GetBookedFlightCountOperation getBookedFlightCountOperation = (GetBookedFlightCountOperation) operationParameters;
			GetBookedFlightCountReply bookedFlightCountResult = getBookedFlightCount(getBookedFlightCountOperation);
			return new Packet(Operation.BOOKED_FLIGHTCOUNT, bookedFlightCountResult);
		case EDIT_FLIGHT:
			EditFlightRecordOperation editFlightRecordOperation = (EditFlightRecordOperation) operationParameters;
			EditFlightRecordReply editFlightRecordOperationResult = editFlightRecord(editFlightRecordOperation);
			return new Packet(Operation.EDIT_FLIGHT, editFlightRecordOperationResult);
		case TRANSFER_RESERVATION:
			TransferReservationOperation transferReservationOperation = (TransferReservationOperation) operationParameters;
			TransferReservationReply transferReservationOperationResult = transferReservation(transferReservationOperation);
			return new Packet(Operation.TRANSFER_RESERVATION, transferReservationOperationResult);
		case REPLICA_ALIVE:
			ReplicaAliveOperation replicaAliveOperation = (ReplicaAliveOperation) operationParameters;
			ReplicaAliveReply replicaAliveReply = replicaAlive(replicaAliveOperation);
			return new Packet(Operation.REPLICA_ALIVE, replicaAliveReply);
		case EXECUTE_OPERATION_LOG:
			this.logger.log("UDP_PARSER", "EXECUTE_OPERATION_LOG", packet.toString());
			ExecuteOperationLogOperation executeOperationLogOperation = (ExecuteOperationLogOperation) operationParameters;
			ExecuteOperationLogReply executeOperationLogReply = executeOperationLog(executeOperationLogOperation);
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
	
	private ExecuteOperationLogReply executeOperationLog(ExecuteOperationLogOperation executeOperationLogOperation) {
		ArrayList<Packet> operationLog = executeOperationLogOperation.getOperationLog();
		for(Packet packet : operationLog){
			processPacket(packet);
		}
		return new ExecuteOperationLogReply();
	}
	
	private void serveRequests() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(port);
			while (true) {
				byte[] buffer = new byte[BUFFER_SIZE];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				this.logger.log("UDP_PARSER", "SERVE_REQUESTS", packet.toString());
				threadPool.execute(new UdpParserPacketDispatcher(this, packet));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}
}
