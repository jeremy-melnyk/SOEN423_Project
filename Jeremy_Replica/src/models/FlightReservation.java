package models;

import java.io.Serializable;
import java.util.Date;

import enums.FlightClass;
<<<<<<< refs/remotes/origin/master
import global.Constants;
=======
>>>>>>> Added CORBA replica implementation to Jeremy_Replica

public class FlightReservation implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private PassengerRecord passengerRecord;
	private FlightRecord flightRecord;
	private FlightClass flightClass;
	private Date bookingDate;
<<<<<<< refs/remotes/origin/master
=======
	private final String DELIMITER = "|";
>>>>>>> Added CORBA replica implementation to Jeremy_Replica

	public FlightReservation(Integer id, PassengerRecord passengerRecord, FlightRecord flightRecord, FlightClass flightClass,
			Date bookingDate) {
		super();
		this.id = id;
		this.passengerRecord = passengerRecord;
		this.flightRecord = flightRecord;
		this.flightClass = flightClass;
		this.bookingDate = bookingDate;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public PassengerRecord getPassengerRecord() {
		return passengerRecord;
	}
	public void setPassengerRecord(PassengerRecord passengerRecord) {
		this.passengerRecord = passengerRecord;
	}
	public FlightRecord getFlightRecord() {
		return flightRecord;
	}
	public void setFlightRecord(FlightRecord flightRecord) {
		this.flightRecord = flightRecord;
	}
	public FlightClass getFlightClass() {
		return flightClass;
	}
	public void setFlightClass(FlightClass flightClass) {
		this.flightClass = flightClass;
	}
	public Date getBookingDate() {
		return bookingDate;
	}
	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}
	
	@Override
	public String toString() {
<<<<<<< refs/remotes/origin/master
		return "FlightReservation" + Constants.DELIMITER + id + Constants.DELIMITER + flightClass + Constants.DELIMITER + passengerRecord + Constants.DELIMITER
				+ flightRecord + Constants.DELIMITER + "BookingDate" + Constants.DELIMITER + bookingDate;
=======
		return "FlightReservation" + DELIMITER + id + DELIMITER + flightClass + DELIMITER + passengerRecord + DELIMITER
				+ flightRecord + DELIMITER + "BookingDate" + DELIMITER + bookingDate;
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
	}
}
