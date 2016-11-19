package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import databases.DatabaseRepository;
import databases.FlightRecordDb;
import databases.FlightReservationDb;
import databases.PassengerRecordDb;
import models.FlightRecord;
import models.FlightReservation;
import models.PassengerRecord;

public class TransferReservationHandler extends RequestHandler {

	public TransferReservationHandler(InetAddress address, int port, Request request, DatagramSocket socket,
			DatabaseRepository databaseRepository) {
		super(address, port, request, socket, databaseRepository);
	}

	@Override
	public void execute() {
		DatagramSocket newSocket = null;
		try {
			// Receive request
			TransferReservationRequest transferReservationRequest = (TransferReservationRequest) request;
			FlightReservation flightReservation = transferReservationRequest.getFlightReservation();
			
			// Send acknowledge
			newSocket = new DatagramSocket();
	        byte[] ackMessage = UdpHelper.booleanToByteArray(true);        
			DatagramPacket ack = new DatagramPacket(ackMessage, ackMessage.length, address, port);
			newSocket.send(ack);
			
			// Receive confirmation
			byte[] buffer = new byte[BUFFER_SIZE];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			newSocket.receive(packet);
			boolean confirmation = UdpHelper.byteArrayToBoolean(packet.getData());
			if (!confirmation){
				// Client cancelled request
				return;
			}
			
			// TODO : Fix Transfer Reservation Logic
			// Flight reservation can only be transferred if
			// A. Flight to same destination is available
			// B. The flight could be booked normally
			// Must link to flight record, and transfer passenger record if it does not exist
			/*
			PassengerRecordDb passengerRecordDb = databaseRepository.getPassengerRecordDb();
			FlightRecordDb flightRecordDb = databaseRepository.getFlightRecordDb();
			FlightRecord newFlightRecord = flightRecordDb.addFlightRecord(flightReservation.getFlightRecord());
			PassengerRecord newPassengerRecord = passengerRecordDb.addPassengerRecord(flightReservation.getPassengerRecord());
			flightReservation.setFlightRecord(newFlightRecord);
			flightReservation.setPassengerRecord(newPassengerRecord);
			*/
			
			// Transfer flight reservation
			FlightReservationDb flightReservationDb = databaseRepository.getFlightReservationDb();		
			flightReservationDb.addFlightReservation(flightReservation);
			
			boolean transferResult = flightReservation != null;
			
			// Send result
			byte[] result = UdpHelper.booleanToByteArray(transferResult);
			DatagramPacket resultMessage = new DatagramPacket(result, result.length, packet.getAddress(), packet.getPort());
			newSocket.send(resultMessage);
			newSocket.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (newSocket != null){
				newSocket.close();
			}
		}
	}
}