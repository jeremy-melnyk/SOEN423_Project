package front_end;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import friendly_end.FlightReservationServer;
import friendly_end.FlightReservationServerHelper;
import friendly_end.FlightReservationServerPOA;
import front_end.failure_tracker.FailureTracker;
import json.JSONReader;
import packet.BookFlightOperation;
import packet.EditFlightRecordOperation;
import packet.GetBookedFlightCountOperation;
import packet.Operation;
import packet.Packet;
import packet.ReplicaAliveOperation;
import packet.ReplicaAliveReply;
import packet.TransferReservationOperation;
import udp.UdpHelper;

public class FrontEnd extends FlightReservationServerPOA{
	private final int sequencer;
	private FailureTracker failureTracker;
	private HashMap<Integer, Integer> replicaTracker;
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
		replicaTracker = new HashMap<Integer, Integer>();
		JSONReader jsonReader = new JSONReader();
		jsonReader.initialize();
		
		//RMs = "localhost:3333\nlocalhost:4444\nlocalhost:5555".split("\n");
		for(int RM : jsonReader.getAllRMPorts())
			replicaTracker.put(RM, 0);
		// Get sequencer's address from config
		sequencer = jsonReader.getSequencerPort();
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
		return null;
	}

	@Override
	public String[] getReservations() {
		return null;
	}
	
	private String send(Packet packet){
		// Create socket
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		// Bind FE Address
		packet.setSenderAddress(socket.getInetAddress());
		packet.setSenderPort(socket.getPort());
		// Get Active Replica addresses from RMs 
		List<Integer> group = getActiveReplicas(socket);
		// SEQUENCER
		FrontEndTransfer transfer =  new FrontEndTransfer(socket, packet, group, sequencer, failureTracker, replicaTracker);
		transfer.start();
		// Wait while there's no correct Response
		while(!transfer.hasCorrectReply());
		return transfer.getCorrectReply();
	}
	
	private List<Integer> getActiveReplicas(DatagramSocket socket) {
		List<Integer> group = new ArrayList<Integer>();
		HashMap<Integer, DatagramPacket> transmitionTracker = new HashMap<Integer, DatagramPacket>();
		try{
			// Multicast 
			for(int RM : replicaTracker.keySet()){
				InetAddress host = InetAddress.getLocalHost();
				ReplicaAliveOperation aliveRequest = new ReplicaAliveOperation();
				Packet packet = new Packet(Operation.REPLICA_ALIVE, aliveRequest);
				byte[] packetBytes = UdpHelper.getByteArray(packet);
				DatagramPacket seq = new DatagramPacket(packetBytes, packetBytes.length, host, RM);
				socket.send(seq);
				transmitionTracker.put(seq.getPort(), seq);
			}
			int counter = replicaTracker.size();
			socket.setSoTimeout(2000);
			while(counter > 0){
				try{
					byte buffer[] = new byte[100];
					DatagramPacket p = new DatagramPacket(buffer, buffer.length);
					socket.receive(p);
					if (transmitionTracker.containsKey(p.getPort()))
						transmitionTracker.remove(p.getPort());	// Remove from tracker
					else
						continue;	// If repeated
					ReplicaAliveReply reply = (ReplicaAliveReply) UdpHelper.getObjectFromByteArray(p.getData());
					if(reply.isAlive()){
						group.add(reply.getReplicaPort());
						replicaTracker.put(p.getPort(), reply.getReplicaPort());
					}
					counter--;
				}catch (SocketTimeoutException e){
					for(DatagramPacket packet : transmitionTracker.values())		// Get all packets not received yet
						socket.send(packet);							// Retransmit
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return group;
	}

}
