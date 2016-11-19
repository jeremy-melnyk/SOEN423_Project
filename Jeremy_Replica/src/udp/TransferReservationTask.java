package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

import databases.FlightReservationDb;
import enums.FlightClass;
import enums.UdpRequestType;
import models.FlightReservation;
import models.FlightServerAddress;

public class TransferReservationTask extends Task<String> {
	private final FlightClass flightClass;
	private final String lastName;
	private final int flightReservationId;
	private final FlightReservationDb flightReservationDb;

	public TransferReservationTask(FlightServerAddress flightServerAddress, FlightClass flightClass, String lastName,
			int flightReservationId, FlightReservationDb flightReservationDb) {
		super(flightServerAddress);
		this.flightClass = flightClass;
		this.lastName = lastName;
		this.flightReservationId = flightReservationId;
		this.flightReservationDb = flightReservationDb;
	}

	@Override
	public String call() {
		return transferReservation();
	}

	private String transferReservation() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();

			// Find reservation
			Character c = lastName.charAt(0);
			HashMap<Integer, FlightReservation> flightReservations = flightReservationDb
					.getFlightReservations(flightClass, c);

			// Sync flight reservations
			FlightReservation flightReservation = null;
			synchronized (flightReservations) {
				flightReservation = flightReservations.get(flightReservationId);
				if (flightReservation == null) {
					return "Error";
				}

				// Send transfer request with data
				TransferReservationRequest transferReservationRequest = new TransferReservationRequest(UdpRequestType.TRANSFER_RESERVATION, flightReservation);
				byte[] message = UdpHelper.getByteArray(transferReservationRequest);
				InetAddress host = InetAddress.getByName(flightServerAddress.getHost());
				int serverPort = flightServerAddress.getPort();
				DatagramPacket request = new DatagramPacket(message, message.length, host, serverPort);
				socket.send(request);

				// Receive acknowledge
				byte[] ackBuffer = new byte[BUFFER_SIZE];
				DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
				socket.receive(ackPacket);
				boolean ack = UdpHelper.byteArrayToBoolean(ackPacket.getData());
				if (!ack) {
					// Cancel transaction
					return "Cancelled";
				}

				// Send confirmation
				byte[] confirmationMessage = UdpHelper.booleanToByteArray(true);
				DatagramPacket confirmationPacket = new DatagramPacket(confirmationMessage, confirmationMessage.length,
						ackPacket.getAddress(), ackPacket.getPort());
				socket.send(confirmationPacket);

				// Receive result
				byte[] resultBuffer = new byte[BUFFER_SIZE];
				DatagramPacket resultPacket = new DatagramPacket(resultBuffer, resultBuffer.length);
				socket.receive(resultPacket);
				boolean result = UdpHelper.byteArrayToBoolean(resultPacket.getData());
				if (!result) {
					// Cancel transaction
					return "Cancelled";
				}

				// Complete transaction
				FlightReservation removedFlightReservation = flightReservationDb.removeFlightReservation(flightClass, c,
						flightReservationId);
				// Release a seat
				removedFlightReservation.getFlightRecord().getFlightClasses().get(flightClass).releaseSeat();
				return removedFlightReservation.toString();
			}
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