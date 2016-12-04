package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FlightRecord implements Serializable {

	public static int recordCount = 0;

	int recordID;

	int economySeats, businessSeats, firstSeats;

	String origin, destination;

	Calendar date;

	int time;

	// Used to keep track of passengers on the flight, separated by seating;
	// holds their recordID
	ArrayList<Integer> economy = new ArrayList<Integer>(), business = new ArrayList<Integer>(),
			first = new ArrayList<Integer>();

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getDateString() {
		int month = date.get(Calendar.MONTH);
		String temp = "";
		if (month < 10) {
			temp = "0";
		}
		return Integer.toString(date.get(Calendar.DAY_OF_MONTH)) + "." + temp
				+ Integer.toString(date.get(Calendar.MONTH)) + "." + Integer.toString(date.get(Calendar.YEAR));
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		if (time >= 0 && time < 24) {
			this.time = time;
		}
	}

	public int getSeats() {
		return economySeats + businessSeats + firstSeats;
	}

	public int getID() {
		return recordID;
	}

	public static void setRecordCount(int n) {
		recordCount = n;
	}

	public int getEconomySeats() {
		return economySeats;
	}

	public ArrayList<Integer> setEconomySeats(int economySeats) {
		this.economySeats = economySeats;

		// Removing excess passengers
		ArrayList<Integer> temp = new ArrayList<Integer>();

		if (economySeats < economy.size()) {
			for (int i = economySeats; i < economy.size(); i++) {
				temp.add(economy.get(i));
				economy.remove(i);
			}
		}
		return temp;
	}

	public int getBusinessSeats() {
		return businessSeats;
	}

	public ArrayList<Integer> setBusinessSeats(int businessSeats) {
		this.businessSeats = businessSeats;

		// Removing excess passengers
		ArrayList<Integer> temp = new ArrayList<Integer>();

		if (businessSeats < business.size()) {
			for (int i = businessSeats; i < business.size(); i++) {
				temp.add(business.get(i));
				business.remove(i);
			}
		}
		return temp;
	}

	public int getFirstSeats() {
		return firstSeats;
	}

	public ArrayList<Integer> setFirstSeats(int firstSeats) {
		this.firstSeats = firstSeats;

		// Removing excess passengers
		ArrayList<Integer> temp = new ArrayList<Integer>();

		if (firstSeats < first.size()) {
			for (int i = firstSeats; i < first.size(); i++) {
				temp.add(first.get(i));
				first.remove(i);
			}
		}
		return temp;
	}

	// Used to check for remaining seats
	public boolean checkSeats(String seatingType) {
		if (seatingType.equals("ECON")) {
			if (economySeats == 0) {
				return false;
			} else if (economy.size() < economySeats) {
				return true;
			}
		} else if (seatingType.equals("BUS")) {
			if (businessSeats == 0) {
				return false;
			} else if (business.size() < businessSeats) {
				return true;
			}
		} else if (seatingType.equals("FIRST")) {
			if (firstSeats == 0) {
				return false;
			} else if (first.size() < firstSeats) {
				return true;
			}
		}

		return false;
	}

	public ArrayList<Integer> getPassengersOnFlight() {
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for (int i = 0; i < economy.size(); i++) {
			temp.add(economy.get(i));
		}
		for (int i = 0; i < business.size(); i++) {
			temp.add(business.get(i));
		}
		for (int i = 0; i < first.size(); i++) {
			temp.add(first.get(i));
		}
		return temp;
	}

	public synchronized void addToFlight(int passengerID, String seatingType) {
		if (seatingType.equals("ECON")) {
			if (economy.size() < economySeats) {
				economy.add(passengerID);
			}
		} else if (seatingType.equals("BUS")) {
			if (business.size() < businessSeats) {
				business.add(passengerID);
			}
		} else if (seatingType.equals("FIRST")) {
			if (first.size() < firstSeats) {
				first.add(passengerID);
			}
		}
	}

	public String toString() {
		String s = "ID: " + recordID + ", " + origin + " to " + destination + ", " + date.get(Calendar.DAY_OF_MONTH)
				+ "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.YEAR) + ", Time: " + time + ":00, Economy: "
				+ economySeats + ", Business: " + businessSeats + ", First Class: " + firstSeats;

		return s;
	}

	public FlightRecord(String origin, String destination, Calendar date, int time, int economySeats, int businessSeats,
			int firstSeats) {

		recordID = recordCount++;

		this.origin = origin;
		this.destination = destination;
		this.date = date;
		this.time = time;
		this.economySeats = economySeats;
		this.businessSeats = businessSeats;
		this.firstSeats = firstSeats;
	}

}
