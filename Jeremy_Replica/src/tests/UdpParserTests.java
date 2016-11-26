package tests;

import org.omg.CORBA.ORB;

import log.CustomLogger;
import log.ILogger;
import log.TextFileLog;
import packet.BookFlightOperation;
import packet.EditFlightRecordOperation;
import packet.GetBookedFlightCountOperation;
import packet.Packet;
import packet.ReplicaOperation;
import udp_parser.UdpParser;

public class UdpParserTests {
	private static UdpParser udpParser;

	public static void main(String[] args) {
		// Initialize UdpParser with an orb for the replica (it performs the
		// role of the CORBA client)
		ORB orb = ORB.init(args, null);
		ILogger logger = new CustomLogger(new TextFileLog());
		udpParser = new UdpParser(orb, logger);

		testBookFlightOperation();
		testGetBookedFlightCount();
		testEditFlightRecordOperation();
	}

	private static void testBookFlightOperation() {
		// Build the action for the packet
		BookFlightOperation bookFlightOperation = new BookFlightOperation.BuilderImpl("John").lastName("Doe")
				.address("Address").phoneNumber("PhoneNumber").destination("MTL|NDL").date("06/05/2016")
				.flightClass("FIRST").build();

		// Create a packet with the operation
		Packet packet = new Packet(ReplicaOperation.BOOK_FLIGHT, bookFlightOperation);

		// Process the packet
		String result = udpParser.processPacket(packet);
		System.out.println(result);
	}

	private static void testGetBookedFlightCount() {
		GetBookedFlightCountOperation getBookedFlightCountOperation = new GetBookedFlightCountOperation(
				"MTL1111|FIRST");

		Packet packet = new Packet(ReplicaOperation.BOOKED_FLIGHTCOUNT, getBookedFlightCountOperation);

		String result = udpParser.processPacket(packet);
		System.out.println(result);
	}

	private static void testEditFlightRecordOperation() {
		EditFlightRecordOperation editFlightRecordOperation = new EditFlightRecordOperation.BuilderImpl("MTL1111|0")
				.fieldName("EDIT|DESTINATION").newValue("NDL").build();
		
		Packet packet = new Packet(ReplicaOperation.EDIT_FLIGHT, editFlightRecordOperation);

		String result = udpParser.processPacket(packet);
		System.out.println(result);
	}
}
