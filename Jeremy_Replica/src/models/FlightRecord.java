package models;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import enums.City;
import enums.FlightClass;

public class FlightRecord implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private City origin;
	private City destination;
	private Date flightDate;
	private HashMap<FlightClass, FlightSeats> flightClasses;
	private ReadWriteLock originLock;
	private ReadWriteLock destinationLock;
	private ReadWriteLock flightDateLock;
	private final String DELIMITER = "|";
	
	public FlightRecord(Integer id, City origin, City destination, Date flightDate,
			HashMap<FlightClass, FlightSeats> flightClasses) {
		super();
		this.id = id;
		this.origin = origin;
		this.destination = destination;
		this.flightDate = flightDate;
		this.flightClasses = flightClasses;
		this.originLock = new ReentrantReadWriteLock(true);
		this.destinationLock = new ReentrantReadWriteLock(true);
		this.flightDateLock = new ReentrantReadWriteLock(true);
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public City getOrigin() {
		this.originLock.readLock().lock();
		try {
			return origin;
		} finally {
			this.originLock.readLock().unlock();
		}
	}

	public void setOrigin(City origin) {
		this.originLock.writeLock().lock();
		try {
			this.origin = origin;
		} finally {
			this.originLock.writeLock().unlock();
		}
	}

	public City getDestination() {
		this.destinationLock.readLock().lock();
		try {
			return destination;
		} finally {
			this.destinationLock.readLock().unlock();
		}
	}

	public void setDestination(City destination) {
		this.destinationLock.writeLock().lock();
		try {
			this.destination = destination;
		} finally {
			this.destinationLock.writeLock().unlock();
		}
	}

	public Date getFlightDate() {
		this.flightDateLock.readLock().lock();
		try {
			return flightDate;
		} finally {
			this.flightDateLock.readLock().unlock();
		}
	}

	public void setFlightDate(Date flightDate) {
		this.flightDateLock.writeLock().lock();
		try {
			this.flightDate = flightDate;
		} finally {
			this.flightDateLock.writeLock().unlock();
		}
	}

	public HashMap<FlightClass, FlightSeats> getFlightClasses() {
		return flightClasses;
	}

	public void setFlightClasses(HashMap<FlightClass, FlightSeats> flightClasses) {
		this.flightClasses = flightClasses;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("FlightRecord" + DELIMITER + id + DELIMITER + origin + DELIMITER + destination + DELIMITER
				+ flightDate);
		sb.append(DELIMITER + FlightClass.FIRST + DELIMITER + flightClasses.get(FlightClass.FIRST));
		sb.append(DELIMITER + FlightClass.BUSINESS + DELIMITER + flightClasses.get(FlightClass.BUSINESS));
		sb.append(DELIMITER + FlightClass.ECONOMY + DELIMITER + flightClasses.get(FlightClass.ECONOMY));
		return sb.toString();
	}
}
