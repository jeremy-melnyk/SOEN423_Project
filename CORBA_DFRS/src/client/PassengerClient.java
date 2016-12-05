package client;

import org.omg.CORBA.ORB;

import enums.City;
import friendly_end.FlightReservationServer;
import global.Constants;
import models.Address;

public class PassengerClient extends CorbaClient {
	private Address address;
	private String phoneNumber;
		
	public PassengerClient(City city, String lastName, String firstName, String address, String phoneNumber, ORB orb) {
		super(city, lastName, firstName, orb);
		this.address = new Address(address);
		this.phoneNumber = phoneNumber;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public void bookFlight(String destination, String date, String flightClass){
		FlightReservationServer flightServer = getFlightServer();
		if (flightServer == null){
			System.out.println("FlightServer was null for " + city);
			return;
		}
		String originAndDestination = this.city.toString() + Constants.DELIMITER + destination.toUpperCase();
		String result = flightServer.bookFlight(firstName, lastName, address.toString(), phoneNumber, originAndDestination, date, flightClass);
		if(result.equals("No flight seat available.")){
			System.out.println("BOOKFLIGHT_FAIL: " + result);
		} else {
			System.out.println("BOOKFLIGHT_SUCCESS: " + result);
		}
	}
}
