package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import databases.DatabaseRepository;
import databases.FlightReservationDb;
import enums.FlightClass;

public class BookedFlightCountHandler extends RequestHandler {

	public BookedFlightCountHandler(InetAddress address, int port, Request request, DatagramSocket socket,
			DatabaseRepository databaseRepository) {
		super(address, port, request, socket, databaseRepository);
	}

	@Override
	public void execute() {
		try {
			BookedFlightCountRequest bookedFlightCountRequest = (BookedFlightCountRequest) request;
			FlightClass flightClass = bookedFlightCountRequest.getFlightClass();
			FlightReservationDb flightReservationDb = databaseRepository.getFlightReservationDb();
			int bookedFlightCount = flightReservationDb.getFlightReservationCount(flightClass);
	        byte[] message = UdpHelper.intToByteArray(bookedFlightCount);
			DatagramPacket reply = new DatagramPacket(message, message.length, address, port);
			socket.send(reply);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}