package db_models;

import java.util.Date;
import java.util.HashMap;

import enums.City;
import enums.FlightClass;
import models.FlightSeats;

public class AddFlightRecord {
	private final String DELIMITER = "|";
	private final String DELIMITER_ESCAPE = "\\" + DELIMITER;
	private City origin;
	private City destination;
	private Date date;
	private HashMap<FlightClass, FlightSeats> flightClasses;
	
	@SuppressWarnings("deprecation")
	public AddFlightRecord(String flightRecord) {
		super();
		String[] tokens = flightRecord.split(DELIMITER_ESCAPE);
		this.origin = City.valueOf(tokens[0].toUpperCase());
		this.destination = City.valueOf(tokens[1].toUpperCase());
		this.date = new Date(tokens[2]);
		this.flightClasses = new HashMap<FlightClass, FlightSeats>();
		flightClasses.put(FlightClass.FIRST, new FlightSeats(Integer.parseInt(tokens[3])));
		flightClasses.put(FlightClass.BUSINESS, new FlightSeats(Integer.parseInt(tokens[4])));
		flightClasses.put(FlightClass.ECONOMY, new FlightSeats(Integer.parseInt(tokens[5])));
	}

	public City getOrigin() {
		return origin;
	}

	public void setOrigin(City origin) {
		this.origin = origin;
	}

	public City getDestination() {
		return destination;
	}

	public void setDestination(City destination) {
		this.destination = destination;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public HashMap<FlightClass, FlightSeats> getFlightClasses() {
		return flightClasses;
	}

	public void setFlightClasses(HashMap<FlightClass, FlightSeats> flightClasses) {
		this.flightClasses = flightClasses;
	}
}
