package front_end;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import failure_tracker.FailureTracker;
import friendly_end.FlightReservationServer;
import friendly_end.FlightReservationServerHelper;
import friendly_end.FlightReservationServerPOA;
import packet.BookFlightOperation;
import packet.EditFlightRecordOperation;
import packet.GetBookedFlightCountOperation;
import packet.Packet;
import packet.Operation;
import packet.TransferReservationOperation;
import replica_manager_packet.ReplicaAliveOperation;
import replica_manager_packet.ReplicaAliveReply;
import replica_manager_packet.ReplicaManagerOperation;
import replica_manager_packet.ReplicaManagerPacket;
import udp.UdpHelper;

public class FrontEnd extends FlightReservationServerPOA{
	private final String[] RMs;
	private final String sequencer;
	private FailureTracker failureTracker;
	private ORB orb;

	public static void main(String[] args) {
		
		// Register CORBA Server
		try {
			ORB orb = ORB.init(args, null); 
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();
			FrontEnd FE = new FrontEnd();
			FE.setORB(orb);
			org.omg.CORBA.Object ref;
			
				ref = rootpoa.servant_to_reference(FE);
			
			FlightReservationServer href = FlightReservationServerHelper.narrow(ref);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			NameComponent path[] = ncRef.to_name("DFRS");
			ncRef.rebind(path, href);
			System.out.println("FrontEnd Running");
			for (;;){
				orb.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public FrontEnd(){
		failureTracker = new FailureTracker();
		// GET RMs' address from config
		RMs = "localhost:3333\nlocalhost:4444\nlocalhost:5555".split("\n");
		// Get sequencer's address from config
		sequencer = "localhost:1234";
	}
	
	public void setORB(ORB orb){
		this.orb = orb;
	}

	@Override
	public String bookFlight(String firstName, String lastName, String address, String phoneNumber, String destination,
			String date, String flightClass) {
		BookFlightOperation bookFlightOperation = new BookFlightOperation.BuilderImpl(firstName).lastName(lastName)
				.address(address)
				.phoneNumber(phoneNumber)
				.destination(destination)
				.date(date)
				.flightClass(flightClass)
				.build();
		Packet packet = new Packet(Operation.BOOK_FLIGHT, bookFlightOperation);
		return send(packet);
	}

	@Override
	public String getBookedFlightCount(String recordType) {
		// TODO Auto-generated method stub
		GetBookedFlightCountOperation getBookFlightCountOperation = new GetBookedFlightCountOperation(recordType);
		Packet packet = new Packet(Operation.BOOKED_FLIGHTCOUNT, getBookFlightCountOperation);
		return send(packet);
	}

	@Override
	public String editFlightRecord(String recordId, String fieldName, String newValue) {
		EditFlightRecordOperation editFlightRecordOperation = new EditFlightRecordOperation
				.BuilderImpl(recordId)
				.fieldName(fieldName)
				.newValue(newValue)
				.build();
		Packet packet = new Packet(Operation.EDIT_FLIGHT, editFlightRecordOperation);
		return send(packet);
	}

	@Override
	public String transferReservation(String passengerId, String currentCity, String otherCity) {
		TransferReservationOperation transferReservationOperation = new TransferReservationOperation
				.BuilderImpl(passengerId)
				.currentCity(currentCity)
				.otherCity(otherCity)
				.build();
		Packet packet = new Packet(Operation.TRANSFER_RESERVATION, transferReservationOperation);
		return send(packet);
	}

	@Override
	public String[] getFlights() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getReservations() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String send(Packet packet){
		String correctreply =  null;
		// Create socket
		// Create Packet with FE Address and port
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		packet.setSenderAddress(socket.getInetAddress());
		packet.setSenderPort(socket.getPort());
		// TODO Get Active Replica addresses from RMs 
		List<Integer> group = getActiveReplicas(socket);
		// SEQUENCER
		FrontEndTransfer transfer =  new FrontEndTransfer(socket, packet, group, sequencer, failureTracker);
		transfer.start();
		// Wait while there's no correct Response
		do{
			correctreply = transfer.getCorrectReply();
		}while(correctreply == null);
		return correctreply;
	}
	
	private List<Integer> getActiveReplicas(DatagramSocket socket) {
		List<Integer> group = new ArrayList<Integer>();
		try{
			for(int i = 0; i < RMs.length; i++){
				URL url = new URL(RMs[i]);
				InetAddress host = InetAddress.getByName(url.getHost());
				ReplicaAliveOperation aliveRequest = new ReplicaAliveOperation(2222 /*?*/);
				ReplicaManagerPacket packet = new ReplicaManagerPacket(ReplicaManagerOperation.REPLICA_ALIVE, aliveRequest);
				byte[] packetBytes = UdpHelper.getByteArray(packet);
				DatagramPacket seq = new DatagramPacket(packetBytes, packetBytes.length, host, url.getPort());
				socket.send(seq);
			}
			int counter = RMs.length;
			while(counter > 0){
				try{
					socket.setSoTimeout(2000);
					
					byte buffer[] = new byte[100];
					DatagramPacket p = new DatagramPacket(buffer, buffer.length);
					socket.receive(p);
					
					ReplicaAliveReply reply = (ReplicaAliveReply) UdpHelper.getObjectFromByteArray(p.getData());
					if(reply.isAlive())
						group.add(reply.getReplicaPort());
					counter--;
				}catch (SocketTimeoutException e){
					// TODO Resend
						//  Find missing replies
						// Recreate packet or get stored packet
						// retransmit
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return group;
	}

}
