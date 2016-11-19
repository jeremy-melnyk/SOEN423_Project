package models;

import java.io.Serializable;
import java.util.Date;

import enums.FlightClass;

public class FlightReservation implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private PassengerRecord passengerRecord;
	private FlightRecord flightRecord;
	private FlightClass flightClass;
	private Date bookingDate;
	private final String DELIMITER = "|";

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
		return "FlightReservation" + DELIMITER + id + DELIMITER + flightClass + DELIMITER + passengerRecord + DELIMITER
				+ flightRecord + DELIMITER + "BookingDate" + DELIMITER + bookingDate;
	}
}
