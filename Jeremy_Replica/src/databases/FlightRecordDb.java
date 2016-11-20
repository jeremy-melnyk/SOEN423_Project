package databases;

import java.util.Date;
import java.util.HashMap;

import db_models.AddFlightRecord;
import enums.City;
import enums.FlightClass;
import models.FlightRecord;
import models.FlightSeats;

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
