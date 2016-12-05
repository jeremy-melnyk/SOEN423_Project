package jeremy_replica.databases;

import java.util.Date;
import java.util.HashMap;

import jeremy_replica.db_models.AddFlightRecord;
import jeremy_replica.enums.City;
import jeremy_replica.enums.FlightClass;
import jeremy_replica.models.FlightRecord;
import jeremy_replica.models.FlightSeats;

public interface FlightRecordDb {
	public FlightRecord getFlightRecord(Date date, Integer id);
	public FlightRecord getFlightRecord(Integer id);
	public FlightRecord[] getFlightRecords(Date date, FlightClass flightClass, City destination);
	public FlightRecord[] getFlightRecords();
	public FlightRecord removeFlightRecord(Date date, Integer id);
	public FlightRecord removeFlightRecord(Integer id);
	public FlightRecord addFlightRecord(City origin, City destination, Date date, HashMap<FlightClass, FlightSeats> flightClasses);
	public FlightRecord addFlightRecord(AddFlightRecord addFlightRecord);
	public FlightRecord addFlightRecord(FlightRecord flightRecord);
}
