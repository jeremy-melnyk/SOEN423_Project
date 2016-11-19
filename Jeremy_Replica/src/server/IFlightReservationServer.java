package server;

public interface IFlightReservationServer extends Runnable {
	public String bookFlight(String firstName, String lastName, String address, String phoneNumber, String destination, String date, String flightClass);
	public String getBookedFlightCount(String bookedFlightCountRequest);
	public String editFlightRecord(String editFlightRecordRequest, String fieldToEdit, String newValue);
	public String transferReservation(String transferReservationRequest, String currentCity, String otherCity);
	
	public String[] getFlightRecords();
	public String[] getFlightReservations();
	public String[] getPassengerRecords();
	public String[] getManagerRecords();
}
