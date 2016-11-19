package front_end;

import friendly_end.FlightReservationServerPOA;

public class FrontEnd extends FlightReservationServerPOA{

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public String bookFlight(String firstName, String lastName, String address, String phoneNumber, String destination,
			String date, String flightClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBookedFlightCount(String recordType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String editFlightRecord(String recordId, String fieldName, String newValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String transferReservation(String passengerId, String currentCity, String otherCity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getFlights() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getReservations() {
		// TODO Auto-generated method stub
		return null;
	}

}
