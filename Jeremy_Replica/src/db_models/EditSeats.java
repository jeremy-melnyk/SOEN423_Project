package db_models;

import enums.FlightClass;

public class EditSeats {
	private final String DELIMITER = "|";
	private final String DELIMITER_ESCAPE = "\\" + DELIMITER;
	private FlightClass flightClass;
	private int seats;
	
	public EditSeats(String editFlightClassSeats) {
		super();
		String[] tokens = editFlightClassSeats.split(DELIMITER_ESCAPE);
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
