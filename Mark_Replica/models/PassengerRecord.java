package models;

import java.io.Serializable;

public class PassengerRecord implements Serializable {

	public static int recordCount = 0;

	int recordID;
	
	int flightID;

	String firstName, lastName, address, phone, destination, date, seating;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSeating() {
		return seating;
	}

	public void setSeating(String seating) {
		this.seating = seating;
	}

	public int getID() {
		return recordID;
	}

	public static void setRecordCount(int n) {
		recordCount = n;
	}

	public String toString() {
		String s = recordID + ", " + firstName + ", " + lastName + ", " + address + ", " + phone + ", " + destination
				+ ", " + date + ", " + seating;

		return s;
	}

	public PassengerRecord(String firstName, String lastName, String address, String phone, String destination,
			String date, String seating) {

		recordID = recordCount++;

		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.phone = phone;
		this.destination = destination;
		this.date = date;
		this.seating = seating;
	}

}
