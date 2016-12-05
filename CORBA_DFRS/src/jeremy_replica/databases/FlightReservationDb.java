package jeremy_replica.databases;

import java.util.HashMap;

import jeremy_replica.enums.FlightClass;
import jeremy_replica.models.FlightRecord;
import jeremy_replica.models.FlightReservation;
import jeremy_replica.models.PassengerRecord;

public interface FlightReservationDb {
	public FlightReservation getFlightReservation(FlightClass flightClass, Character c, Integer id);
	public FlightReservation getFlightReservation(Integer id);
	public HashMap<Integer, FlightReservation> getFlightReservations(FlightClass flightClass, Character c);
	public FlightReservation removeFlightReservation(FlightClass flightClass, Character c, Integer id);
	public FlightReservation[] removeFlightReservations(int flightRecordId);
	public FlightReservation[] removeFlightReservations(FlightClass flightClass, int count);
	public FlightReservation[] getFlightReservations();
	public FlightReservation addFlightReservation(FlightClass flightClass, PassengerRecord passengerRecord, FlightRecord flightRecord);
	public FlightReservation addFlightReservation(FlightReservation flightReservation);
	int getFlightReservationCount(FlightClass flightClass);
}
