package client;

import org.omg.CORBA.ORB;

import enums.City;
<<<<<<< refs/remotes/origin/master
import global.Constants;
=======
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
import replica_friendly_end.FlightReservationServer;

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
<<<<<<< refs/remotes/origin/master
		String request = getId() + Constants.DELIMITER + flightClass;
=======
		String request = getId() + DELIMITER + flightClass;
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
		String result = flightServer.getBookedFlightCount(request);
		System.out.println(result);
	}
	
	public void editFlightRecord(String editParameters, String fieldToEdit, String newValue){
		FlightReservationServer flightServer = getFlightServer();
		if (flightServer == null){
			System.out.println("FlightServer was null for " + city);
		}
<<<<<<< refs/remotes/origin/master
		String request = getId() + Constants.DELIMITER + editParameters;
=======
		String request = getId() + DELIMITER + editParameters;
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
		String result = flightServer.editFlightRecord(request, fieldToEdit, newValue);
		String operation = editParameters.split("\\|")[0];
		System.out.println(operation + " : " + result);
	}
	
	public void transferReservation(int flightRecordId, City otherCity){
		FlightReservationServer flightServer = getFlightServer();
		if (flightServer == null){
			System.out.println("FlightServer was null for " + city);
		}
<<<<<<< refs/remotes/origin/master
		String request = getId() + Constants.DELIMITER + flightRecordId;
=======
		String request = getId() + DELIMITER + flightRecordId;
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
		String result = flightServer.transferReservation(request, city.toString(), otherCity.toString());
		System.out.println(result);
	}
	
	protected String[] getManagerRecords(){
		FlightReservationServer flightServer = getFlightServer();
		if (flightServer == null){
			System.out.println("FlightServer was null for " + city);
			return new String[0];
		}
		return flightServer.getManagerRecords();
	}
}
