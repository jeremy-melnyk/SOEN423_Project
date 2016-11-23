package front_end;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.locks.Lock;

import friendly_end.FlightReservationServerPOA;
import packet.BookFlightOperation;
import packet.Packet;
import packet.ReplicaOperation;

public class FrontEnd extends FlightReservationServerPOA{
	private static int portNumber = 2288;
	private static Lock udpPortLock;

	public static void main(String[] args) {
		// TODO
		// Create FE
		// Get Replica's Addresses
		// Get Sequencer's Address
		
		// Register CORBA Server

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
		Packet packet = new Packet(ReplicaOperation.BOOK_FLIGHT, bookFlightOperation);
		return send(packet);
	}

	@Override
	public String getBookedFlightCount(String recordType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String editFlightRecord(String recordId, String fieldName, String newValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String transferReservation(String passengerId, String currentCity, String otherCity) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO 
		// Create socket
		// Create Packet with FE Address and port
		DatagramSocket socket = null;
		synchronized (udpPortLock){
			try {
				socket = new DatagramSocket(portNumber++);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		packet.setSenderAddress(socket.getInetAddress());
		packet.setSenderPort(socket.getPort());
		// GROUP
		String[] group = null;
		// SEQUENCER
		int sequencer = 1234;
		FrontEndTransfer transfer =  new FrontEndTransfer(socket, packet, group, sequencer);
		transfer.start();
		// Wait while there's no correct Response
		do{
			correctreply = transfer.getCorrectReply();
		}while(correctreply == null);
		return correctreply;
	}

}
