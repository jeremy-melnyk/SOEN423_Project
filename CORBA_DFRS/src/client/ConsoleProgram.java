package client;

import java.util.Scanner;

import org.omg.CORBA.ORB;

import enums.City;
import enums.FlightClass;
import enums.FlightRecordField;
import enums.FlightRecordOperation;
import global.Constants;

public class ConsoleProgram {
	public final static int exitCommand = -1;

	public static void main(String[] args) {
		// Initialize ORB
		ORB orb = ORB.init(args, null);
		
		Scanner keyboard = new Scanner(System.in);
		System.out.println("To exit anytime, enter " + exitCommand + " at a prompt.");
		System.out.println("Welcome to DFRS!");
		clientSelect(keyboard, orb);
	}

	public static void clientSelect(Scanner keyboard, ORB orb) {
		while (true) {
			System.out.println("What kind of client are you?");
			System.out.println("1 : Passenger");
			System.out.println("2 : Manager");
			int choice = keyboard.nextInt();
			switch (choice) {
			case 1:
				passengerSelect(keyboard, orb);
				break;
			case 2:
				managerSelect(keyboard, orb);
				break;
			}
			if (choice == exitCommand) {
				break;
			}
		}
	}

	public static void passengerSelect(Scanner keyboard, ORB orb) {
		City city = citySelect(keyboard, "Which city?");
		if (isNull(city)) {
			return;
		}
		String firstName = stringSelect(keyboard, "first name");
		String lastName = stringSelect(keyboard, "last name");
		// Default address & phoneNumber
		String address = "1000 Somewhere Land|Montreal|QC|H1B 5U0|Canada";
		String phoneNumber = "514-123-4567";
		PassengerClient client = new PassengerClient(city, lastName, firstName, address, phoneNumber, orb);
		flightSelect(keyboard, client);
	}

	public static void managerSelect(Scanner keyboard, ORB orb) {
		City city = citySelect(keyboard, "Which city?");
		if (isNull(city)) {
			return;
		}
		String firstName = "Robo";
		String lastName = "Cop";
		System.out.println("Please enter your manager ID:");
		int id = keyboard.nextInt();
		if (id == exitCommand){
			return;
		}
		ManagerClient managerClient = new ManagerClient(city, lastName, firstName, id, orb);
		managerOperationSelect(keyboard, managerClient);
	}

	public static void managerOperationSelect(Scanner keyboard, ManagerClient managerClient) {
		while (true) {
			System.out.println("What would you like to do?");
			System.out.println("1 : BOOKED FLIGHT COUNT");
			System.out.println("2 : MODIFY FLIGHT RECORDS");
			System.out.println("3 : TRANSFER RESERVATION");
			int choice = keyboard.nextInt();
			switch (choice) {
			case 1:
				bookedFlightCount(keyboard, managerClient);
				break;
			case 2:
				modifyFlightRecords(keyboard, managerClient);
				break;
			case 3:
				transferReservation(keyboard, managerClient);
				break;
			}
			if (choice == exitCommand) {
				break;
			}
		}
	}

	public static void modifyFlightRecords(Scanner keyboard, ManagerClient managerClient) {
		while(true){
			System.out.println("What kind of edit?");
			System.out.println("1 : ADD FLIGHT RECORD");
			System.out.println("2 : REMOVE FLIGHT RECORD");
			System.out.println("3 : EDIT FLIGHT RECORD");
			int choice = keyboard.nextInt();
			switch (choice) {
			case 1:
				addFlightRecord(keyboard, managerClient);
				break;
			case 2:
				removeFlightRecord(keyboard, managerClient);
				break;
			case 3:
				editFlightRecord(keyboard, managerClient);
				break;
			}
			if (choice == exitCommand){
				break;
			}
		}
	}
	
	public static void transferReservation(Scanner keyboard, ManagerClient managerClient) {
		while(true){
			String[] flightRecords = managerClient.getFlightReservations();
			System.out.println("Existing flight reservations:");
			display(flightRecords);
			if(flightRecords.length == 0){
				return;
			}
			System.out.println("Choose a flight reservation record ID to transfer: ");
			int recordId = keyboard.nextInt();
			if(recordId == exitCommand){
				return;
			}
			City city = citySelect(keyboard, "To which city?");
			if (isNull(city)) {
				return;
			}
			if(recordId == exitCommand){
				break;
			}
			managerClient.transferReservation(recordId, city);
		}	
	}
	
	public static void addFlightRecord(Scanner keyboard, ManagerClient managerClient) {
		City[] cities = destinationCities(managerClient.getCity());
		City city = destinationSelect(keyboard, cities);
		if (isNull(city)) {
			return;
		}
		String date = dateSelect(keyboard);
		if (isCancel(date)) {
			return;
		}
		int firstSeats = seatsSelect(keyboard, FlightClass.FIRST);
		if(firstSeats == exitCommand){
			return;
		}
		int businessSeats = seatsSelect(keyboard, FlightClass.BUSINESS);
		if(businessSeats == exitCommand){
			return;
		}
		int economySeats = seatsSelect(keyboard, FlightClass.ECONOMY);
		if(businessSeats == exitCommand){
			return;
		}
		String fieldName = FlightRecordOperation.CREATE.toString();
		String newValue = city.toString() + Constants.DELIMITER + date + Constants.DELIMITER
				+ economySeats + Constants.DELIMITER + businessSeats + Constants.DELIMITER + firstSeats;
		// recordId not used, so set to 0
		managerClient.editFlightRecord(0, fieldName, newValue);
	}
	
	public static City[] destinationCities(City city) {
		switch(city){
		case MTL:
			return new City[]{ City.WST, City.NDL };
		case WST:
			return new City[]{ City.MTL, City.NDL };
		case NDL:
			return new City[]{ City.MTL, City.WST };
		default:
			return new City[0];
		}
	}

	public static void removeFlightRecord(Scanner keyboard, ManagerClient managerClient) {
		String[] flightRecords = managerClient.getFlightRecords();
		System.out.println("Existing flight records:");
		display(flightRecords);
		if(flightRecords.length == 0){
			return;
		}
		System.out.println("Enter a flight record ID to remove: ");
		int recordId = keyboard.nextInt();
		if(recordId == exitCommand){
			return;
		}
		String fieldName = FlightRecordOperation.DELETE.toString();
		managerClient.editFlightRecord(recordId, fieldName, "");
	}

	public static void editFlightRecord(Scanner keyboard, ManagerClient managerClient) {
		while(true){
			String[] flightRecords = managerClient.getFlightRecords();
			System.out.println("Existing flight records:");
			display(flightRecords);
			if(flightRecords.length == 0){
				return;
			}
			System.out.println("Choose a flight record ID to edit: ");
			int recordId = keyboard.nextInt();
			if(recordId == exitCommand){
				return;
			}
			System.out.println("What field would you like to edit?");
			System.out.println("1 : DESTINATION");
			System.out.println("2 : DATE");
			System.out.println("3 : SEATS");
			int choice = keyboard.nextInt();
			switch (choice) {
			case 1:
				editDestination(keyboard, managerClient, recordId);
				break;
			case 2:
				editDate(keyboard, managerClient, recordId);
				break;
			case 3:
				editSeats(keyboard, managerClient, recordId);
				break;
			}
			if(choice == exitCommand){
				break;
			}
		}
	}

	public static void editDestination(Scanner keyboard, ManagerClient managerClient, int recordId) {
		City city = citySelect(keyboard, "To which city?");
		if (isNull(city)) {
			return;
		}
		String fieldName = FlightRecordOperation.EDIT.toString() + Constants.DELIMITER + FlightRecordField.DESTINATION.toString();
		managerClient.editFlightRecord(recordId, fieldName, city.toString());
	}
	
	public static void editDate(Scanner keyboard, ManagerClient managerClient, int recordId) {
		String date = dateSelect(keyboard);
		if (isCancel(date)) {
			return;
		}
		String fieldName = FlightRecordOperation.EDIT.toString() + Constants.DELIMITER + FlightRecordField.DATE.toString();
		managerClient.editFlightRecord(recordId, fieldName, date);
	}
	
	public static void editSeats(Scanner keyboard, ManagerClient managerClient, int recordId) {
		FlightClass flightClass = flightClassSelect(keyboard);
		if (isNull(flightClass)) {
			return;
		}
		int seats = seatsSelect(keyboard, flightClass);
		if(seats == exitCommand){
			return;
		}
		String fieldName = FlightRecordOperation.EDIT.toString() + Constants.DELIMITER + flightClass.toString();
		String newValue = Integer.toString(seats);
		managerClient.editFlightRecord(recordId, fieldName, newValue);
	}

	public static void bookedFlightCount(Scanner keyboard, ManagerClient managerClient) {
		FlightClass flightClass = flightClassSelect(keyboard);
		if (isNull(flightClass)) {
			return;
		}
		managerClient.getBookedFlightCount(flightClass.toString());
	}

	public static FlightClass flightClassSelect(Scanner keyboard) {
		System.out.println("Which flight class?");
		System.out.println("1 : FIRST CLASS");
		System.out.println("2 : BUSINESS CLASS");
		System.out.println("3 : ECONOMY CLASS");
		int choice = keyboard.nextInt();
		FlightClass flightClass = null;
		switch (choice) {
		case 1:
			flightClass = FlightClass.FIRST;
			break;
		case 2:
			flightClass = FlightClass.BUSINESS;
			break;
		case 3:
			flightClass = FlightClass.ECONOMY;
			break;
		}
		return flightClass;
	}

	public static City citySelect(Scanner keyboard, String message) {
		System.out.println(message);
		System.out.println("1 : MTL");
		System.out.println("2 : WST");
		System.out.println("3 : NDL");
		int choice = keyboard.nextInt();
		City city = null;
		switch (choice) {
		case 1:
			city = City.MTL;
			break;
		case 2:
			city = City.WST;
			break;
		case 3:
			city = City.NDL;
			break;
		}
		return city;
	}
	
	public static City destinationSelect(Scanner keyboard, City[] destinations) {
		if (destinations.length < 2){
			return null;
		}
		System.out.println("To which destination?");
		System.out.println("1 : " + destinations[0]);
		System.out.println("2 : " + destinations[1]);
		int choice = keyboard.nextInt();
		City city = null;
		switch (choice) {
		case 1:
			city = destinations[0];
			break;
		case 2:
			city = destinations[1];
			break;
		}
		return city;
	}

	public static String stringSelect(Scanner keyboard, String parameter) {
		System.out.println("What is your " + parameter + "?");
		String value = keyboard.next();
		return value;
	}

	public static String dateSelect(Scanner keyboard) {
		System.out.println("Which date?");
		String date = keyboard.next();
		return date;
	}

	public static int seatsSelect(Scanner keyboard, FlightClass flightClass) {
		System.out.println("Number of seats for " + flightClass.toString() + " class:");
		int seats = keyboard.nextInt();
		return seats;
	}

	public static void flightSelect(Scanner keyboard, PassengerClient passengerClient) {
		while (true) {
			String[] flightRecords = passengerClient.getFlightRecords();
			System.out.println("Existing flight records:");
			display(flightRecords);
			if(flightRecords.length == 0){
				return;
			}
			String date = dateSelect(keyboard);
			if (isCancel(date)) {
				return;
			}
			City[] destinations = destinationCities(passengerClient.getCity());
			City destination = destinationSelect(keyboard, destinations);
			if (isNull(destination)) {
				return;
			}
			FlightClass flightClass = flightClassSelect(keyboard);
			if (isNull(flightClass)) {
				return;
			}
			passengerClient.bookFlight(destination.toString(), date, flightClass.toString());
		}
	}

	public static void display(String[] items) {
		if(items.length == 0){
			System.out.println("Nothing to display!");
			return;
		}
		for (String item : items) {
			System.out.println(item);
		}
	}
	
	public static boolean isNull(Object obj) {
		if (obj == null) {
			return true;
		}
		return false;
	}

	public static boolean isCancel(String input) {
		try {
			if (Integer.parseInt(input) == exitCommand) {
				return true;
			}
		} catch (Exception e) {
			// Continue
		}
		return false;
	}
}
