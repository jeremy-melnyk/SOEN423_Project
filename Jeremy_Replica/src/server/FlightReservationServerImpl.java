package server;

import replica_friendly_end.FlightReservationServerPOA;

public class FlightReservationServerImpl extends FlightReservationServerPOA {

	@Override
	public String bookFlight(String firstName, String lastName, String address, String phoneNumber, String destination,
			String date, String flightClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBookedFlightCount(String bookedFlightCountRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String editFlightRecord(String editFlightRecordRequest, String fieldToEdit, String newValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String transferReservation(String transferReservationRequest, String currentCity, String otherCity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getFlightRecords() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getFlightReservations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getPassengerRecords() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getManagerRecords() {
		// TODO Auto-generated method stub
		return null;
	}
}
