package client;

import org.omg.CORBA.ORB;

import enums.City;
import friendly_end.FlightReservationServer;
import global.Constants;

public class ManagerClient extends CorbaClient {
	private Integer id;

	public ManagerClient(City city, String lastName, String firstName, Integer id, ORB orb) {
		super(city, lastName, firstName, orb);
		this.id = id;
	}

	public String getId() {
		return city + id.toString();
	}
	
	public void getBookedFlightCount(String flightClass){
		FlightReservationServer flightServer = getFlightServer();
		if (flightServer == null){
			System.out.println("FlightServer was null for " + city);
		}
		String request = getId() + Constants.DELIMITER + flightClass;
		String result = flightServer.getBookedFlightCount(request);
		System.out.println("COUNT: " + result);
	}
	
	public void editFlightRecord(int flightRecordId, String fieldName, String newValue){
		FlightReservationServer flightServer = getFlightServer();
		if (flightServer == null){
			System.out.println("FlightServer was null for " + city);
		}
		String recordId = getId() + Constants.DELIMITER + flightRecordId;
		String result = flightServer.editFlightRecord(recordId, fieldName, newValue);
		System.out.println(fieldName + ": " + result);
	}
	
	public void transferReservation(int flightRecordId, City otherCity){
		FlightReservationServer flightServer = getFlightServer();
		if (flightServer == null){
			System.out.println("FlightServer was null for " + city);
		}
		String request = getId() + Constants.DELIMITER + flightRecordId;
		String result = flightServer.transferReservation(request, city.toString(), otherCity.toString());
		System.out.println("TRANSFER: " + result);
	}
}
