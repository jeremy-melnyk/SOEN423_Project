package db_models;

<<<<<<< refs/remotes/origin/master
import java.text.ParseException;
import java.text.SimpleDateFormat;
=======
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
import java.util.Date;
import java.util.HashMap;

import enums.City;
import enums.FlightClass;
import models.FlightSeats;
<<<<<<< refs/remotes/origin/master
import global.Constants;

public class AddFlightRecord {
=======

public class AddFlightRecord {
	private final String DELIMITER = "|";
	private final String DELIMITER_ESCAPE = "\\" + DELIMITER;
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
	private City origin;
	private City destination;
	private Date date;
	private HashMap<FlightClass, FlightSeats> flightClasses;
	
<<<<<<< refs/remotes/origin/master
	public AddFlightRecord(String flightRecord) {
		super();
		String[] tokens = flightRecord.split(Constants.DELIMITER_ESCAPE);
		this.origin = City.valueOf(tokens[0].toUpperCase());
		this.destination = City.valueOf(tokens[1].toUpperCase());
		this.date = new Date();
		try {
			date = new SimpleDateFormat(Constants.DATE_FORMAT).parse(tokens[2]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
=======
	@SuppressWarnings("deprecation")
	public AddFlightRecord(String flightRecord) {
		super();
		String[] tokens = flightRecord.split(DELIMITER_ESCAPE);
		this.origin = City.valueOf(tokens[0].toUpperCase());
		this.destination = City.valueOf(tokens[1].toUpperCase());
		this.date = new Date(tokens[2]);
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
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
