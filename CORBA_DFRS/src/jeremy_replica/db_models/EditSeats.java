package jeremy_replica.db_models;

import jeremy_replica.enums.FlightClass;
import jeremy_replica.global.Constants;

public class EditSeats {
	private FlightClass flightClass;
	private int seats;
	
	public EditSeats(String editFlightClassSeats) {
		super();
		String[] tokens = editFlightClassSeats.split(Constants.DELIMITER_ESCAPE);
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
