package front_end;

import friendly_end.FlightReservationServerPOA;
import packet.BookFlightOperation;
import packet.Packet;
import packet.ReplicaOperation;

public class FrontEnd extends FlightReservationServerPOA{

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
		String correctreply =  null;
		// TODO 
		// Create socket
		// Create Packet with FE Address and port
		BookFlightOperation bookFlightOperation = new BookFlightOperation.BuilderImpl(firstName).lastName(lastName)
				.address(address).phoneNumber(phoneNumber).destination(destination).date(date).flightClass(flightClass).build();
		Packet packet = new Packet(ReplicaOperation.BOOK_FLIGHT, bookFlightOperation);
		String[] s = null;
		FrontEndTransfer transfer =  new FrontEndTransfer(packet, s, "");
		transfer.start();
		// Async send msg to sequencer
		// --- Wait for sequencer ACK
		// ------ Wait for replica ACKS
		// ------ As soon as 2 identical replies from replicas: Assign correct Response
		// ------ Wait for all replies, If 2x time from before: Communicate with RM and retransmit if necessary
		// ------ If incorrect reply from replica: increase replica counter. If 3, tell RM
		// Wait while there's no correct Response
		do{
			correctreply = transfer.getCorrectReply();
		}while(correctreply == null);
		// Deliver correct reply
		return correctreply;
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

}
