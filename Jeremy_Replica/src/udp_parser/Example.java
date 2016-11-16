package udp_parser;

import packet.BookFlightOperation;
import packet.Packet;
import packet.ReplicaOperation;
import udp_parser.UdpParser;

public class Example {
	public static void main(String[] args) {
		// Build the action for the packet using the builder for BookFlightOperation
		BookFlightOperation bookFlightOperation = new BookFlightOperation.BuilderImpl("John").lastName("Doe")
				.address("Address").phoneNumber("PhoneNumber").date("MTL").flightClass("ECONOMY").build();
		
		// Create a packet with the operation
		Packet packet = new Packet(ReplicaOperation.BOOK_FLIGHT, bookFlightOperation);
		
		// Initialize UdpParser with an orb for the replica (it performs the role of the CORBA client)
		// Using mock orb so that the code runs
		MockOrb orb = new MockOrb();
		UdpParser udpParser = new UdpParser(orb);
		
		// Process the packet (in which case the bookFlightOperation is called)
		udpParser.processPacket(packet);
		
		System.out.println("Test");
	}
}
