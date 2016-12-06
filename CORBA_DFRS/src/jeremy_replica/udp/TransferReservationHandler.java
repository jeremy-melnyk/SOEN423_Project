package jeremy_replica.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;

import jeremy_replica.databases.DatabaseRepository;
import jeremy_replica.databases.FlightRecordDb;
import jeremy_replica.databases.FlightReservationDb;
import jeremy_replica.databases.PassengerRecordDb;
import jeremy_replica.enums.City;
import jeremy_replica.enums.FlightClass;
import jeremy_replica.enums.UdpRequestType;
import jeremy_replica.models.FlightRecord;
import jeremy_replica.models.FlightReservation;
import jeremy_replica.models.PassengerRecord;

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
			
			// Flight reservation can only be transferred if
			// A. Flight to same destination is available
			// B. The flight could be booked normally
			// Must link to flight record, and transfer passenger record if it does not exist
			
			boolean transferResult = false;
			
			City destination = flightReservation.getFlightRecord().getDestination();
			Date date = flightReservation.getFlightRecord().getFlightDate();
			FlightClass flightClass = flightReservation.getFlightClass();
			
			PassengerRecordDb passengerRecordDb = databaseRepository.getPassengerRecordDb();
			FlightRecordDb flightRecordDb = databaseRepository.getFlightRecordDb();
			FlightReservationDb flightReservationDb = databaseRepository.getFlightReservationDb();
			
			// Transfer flight reservation, if valid flight record exists for transfer, and seat is available
			FlightRecord existingFlightRecord = flightRecordDb.getFlightRecord(date, destination);
			if(existingFlightRecord != null){
				boolean acquiredSeat = existingFlightRecord.getFlightClasses().get(flightClass).acquireSeat();
				if(acquiredSeat){
					PassengerRecord newPassengerRecord = passengerRecordDb.addPassengerRecord(flightReservation.getPassengerRecord());
					flightReservation.setFlightRecord(existingFlightRecord);
					flightReservation.setPassengerRecord(newPassengerRecord);
					flightReservationDb.addFlightReservation(flightReservation);
					transferResult = true;	
				}
			}
			
			// Send result
			UdpRequestType header = UdpRequestType.TRANSFER_RESERVATION_FAIL;
			if(transferResult){
				header = UdpRequestType.TRANSFER_RESERVATION;
			}
			TransferReservationRequest transferReservationRequestResult = new TransferReservationRequest(header, flightReservation);	
			byte[] result = UdpHelper.getByteArray(transferReservationRequestResult);
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