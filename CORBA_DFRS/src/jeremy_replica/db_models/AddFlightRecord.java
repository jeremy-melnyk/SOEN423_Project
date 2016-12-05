package jeremy_replica.db_models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import jeremy_replica.enums.City;
import jeremy_replica.enums.FlightClass;
import jeremy_replica.global.Constants;
import jeremy_replica.models.FlightSeats;

public class AddFlightRecord {
	private City origin;
	private City destination;
	private Date date;
	private HashMap<FlightClass, FlightSeats> flightClasses;
	
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
