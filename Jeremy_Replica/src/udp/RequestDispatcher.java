package udp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import databases.DatabaseRepository;
import enums.UdpRequestType;

public class RequestDispatcher implements Runnable {
	private final DatagramSocket socket;
	private final DatagramPacket packet;
	private final DatabaseRepository databaseRepository;

	public RequestDispatcher(DatagramSocket socket, DatagramPacket packet, DatabaseRepository databaseRepository) {
		super();
		this.socket = socket;
		this.packet = packet;
		this.databaseRepository = databaseRepository;
	}

	@Override
	public void run() {
		handleRequest();
	}

	private void handleRequest() {
		Request request = null;
		try {
			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(packet.getData());
			ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
			request = (Request)objectInputStream.readObject();
			objectInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}
		UdpRequestType requestType = request.getRequestType();
		switch (requestType) {
		case BOOKED_FLIGHTCOUNT:
			new BookedFlightCountHandler(packet.getAddress(), packet.getPort(), request, socket, databaseRepository).execute();
			break;
		case TRANSFER_RESERVATION:
			new TransferReservationHandler(packet.getAddress(), packet.getPort(), request, socket, databaseRepository).execute();
			break;
		}
	}
}
