package jeremy_replica.tests;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import jeremy_replica.udp.UdpHelper;
import packet.BookFlightOperation;
import packet.EditFlightRecordOperation;
import packet.GetBookedFlightCountOperation;
import packet.Operation;
import packet.OperationParameters;
import packet.Packet;

public class UdpParserTests {
	public static int BUFFER_SIZE = 50000;
	private static int portForParser;

	public static void main(String[] args) {		
		portForParser = 2000;
		
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
		Packet packet = new Packet(Operation.BOOK_FLIGHT, bookFlightOperation);

		// Process the packet
		OperationParameters result = processOperationPacket(packet, portForParser);;
		System.out.println(result);
	}

	private static void testGetBookedFlightCount() {
		GetBookedFlightCountOperation getBookedFlightCountOperation = new GetBookedFlightCountOperation(
				"MTL1111|FIRST");

		Packet packet = new Packet(Operation.BOOKED_FLIGHTCOUNT, getBookedFlightCountOperation);

		OperationParameters result = processOperationPacket(packet, portForParser);
		System.out.println(result);
	}

	private static void testEditFlightRecordOperation() {
		EditFlightRecordOperation editFlightRecordOperation = new EditFlightRecordOperation.BuilderImpl("MTL1111|0")
				.fieldName("EDIT|DESTINATION").newValue("NDL").build();
		
		Packet packet = new Packet(Operation.EDIT_FLIGHT, editFlightRecordOperation);

		OperationParameters result = processOperationPacket(packet, portForParser);
		System.out.println(result);
	}
	
	private static OperationParameters processOperationPacket(Packet packet, int port) {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			byte[] message = UdpHelper.getByteArray(packet);
			InetAddress host = InetAddress.getByName("localhost");
			int serverPort = port;
			DatagramPacket requestPacket = new DatagramPacket(message, message.length, host, serverPort);
			socket.send(requestPacket);
			byte[] buffer = new byte[BUFFER_SIZE];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			socket.receive(reply);
			OperationParameters result = (OperationParameters) UdpHelper.getObjectFromByteArray(reply.getData());
			return result;
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			socket.close();
		}
		return null;
	}
}
