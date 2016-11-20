package models;

import java.io.Serializable;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FlightSeats implements Serializable {
	private static final long serialVersionUID = 1L;
	private int seats;
	private int availableSeats;
	private ReadWriteLock lock;
	private final String DELIMITER = "|";

	public FlightSeats(int seats) {
		super();
		this.seats = seats;
		this.availableSeats = seats;
		this.lock = new ReentrantReadWriteLock(true);
	}

	public int getSeats() {
		this.lock.readLock().lock();
		try {
			return seats;
		} finally {
			this.lock.readLock().unlock();
		}
	}

	public int setSeats(int seats) {
		if (seats < 0) {
			seats = 0;
		}
		lock.writeLock().lock();
		try {
			if (seats == this.seats) {
				return 0;
			}
			if (seats > this.seats) {
				int numOfNewSeats = seats - this.seats;
				availableSeats += numOfNewSeats;
				this.seats = seats;
				return 0;
			} else {
				int numOfReducedSeats = this.seats - seats;
				availableSeats -= numOfReducedSeats;
				this.seats = seats;
				if (availableSeats < 0){
					int seatsToRemove = -availableSeats;
					availableSeats = 0;
					return seatsToRemove;	
				} else {
					return 0;
				}
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	public int getAvailableSeats() {
		this.lock.readLock().lock();
		try {
			return availableSeats;
		} finally {
			this.lock.readLock().unlock();
		}
	}

	public boolean acquireSeat() {
		lock.writeLock().lock();
		try {
			if (this.availableSeats > 0) {
				--this.availableSeats;
				return true;
			}
			return false;
		} finally {
			lock.writeLock().unlock();
		}
	}

	public boolean releaseSeat() {
		lock.writeLock().lock();
		try {
			if (this.availableSeats < this.seats) {
				++this.availableSeats;
				return true;
			}
			return false;
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public String toString() {
		return "S" + seats + DELIMITER +  "A" + availableSeats;
	}
}
