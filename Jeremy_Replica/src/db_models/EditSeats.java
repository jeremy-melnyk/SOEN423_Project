package db_models;

import enums.FlightClass;
<<<<<<< refs/remotes/origin/master
import global.Constants;

public class EditSeats {
=======

public class EditSeats {
	private final String DELIMITER = "|";
	private final String DELIMITER_ESCAPE = "\\" + DELIMITER;
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
	private FlightClass flightClass;
	private int seats;
	
	public EditSeats(String editFlightClassSeats) {
		super();
<<<<<<< refs/remotes/origin/master
		String[] tokens = editFlightClassSeats.split(Constants.DELIMITER_ESCAPE);
=======
		String[] tokens = editFlightClassSeats.split(DELIMITER_ESCAPE);
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
		this.flightClass = FlightClass.valueOf(tokens[0].toUpperCase());
		this.seats = Integer.parseInt(tokens[1]);
	}

	public FlightClass getFlightClass() {
		return flightClass;
	}

	public void setFlightClass(FlightClass flightClass) {
		this.flightClass = flightClass;
	}

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}
}
