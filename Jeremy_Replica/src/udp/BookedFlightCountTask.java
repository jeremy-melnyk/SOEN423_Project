package udp;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import enums.FlightClass;
import enums.UdpRequestType;
import models.FlightServerAddress;

public class BookedFlightCountTask extends Task<String> {
	private final FlightClass flightClass;

	public BookedFlightCountTask(FlightServerAddress flightServerAddress, FlightClass flightClass) {
		super(flightServerAddress);
		this.flightClass = flightClass;
	}

	@Override
	public String call() {
		return getBookedFlightCount();
	}

	private String getBookedFlightCount() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			Request request = new BookedFlightCountRequest(UdpRequestType.BOOKED_FLIGHTCOUNT, flightClass);
			byte[] message = UdpHelper.getByteArray(request);
			InetAddress host = InetAddress.getByName(flightServerAddress.getHost());
			int serverPort = flightServerAddress.getPort();
			DatagramPacket requestPacket = new DatagramPacket(message, message.length, host, serverPort);
			socket.send(requestPacket);
			byte[] buffer = new byte[BUFFER_SIZE];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			socket.receive(reply);
			int flightCount;
			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(reply.getData());
			DataInputStream dataInputStream = new DataInputStream(byteInputStream);
			flightCount = dataInputStream.readInt();
			dataInputStream.close();
			return flightServerAddress.getCity() + " " + flightCount;
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			socket.close();
		}
		return "Error";
	}
}