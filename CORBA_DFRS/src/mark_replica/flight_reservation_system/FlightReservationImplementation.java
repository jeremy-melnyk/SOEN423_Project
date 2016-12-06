package mark_replica.flight_reservation_system;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import json.JSONReader;
import mark_replica.global.Constants;
import mark_replica.models.FlightRecord;
import mark_replica.models.PassengerRecord;

public class FlightReservationImplementation extends FlightReservationPOA implements Runnable {

	private final String USERNAME = "Mark";

	private int port;
	private String city;
	private String cityCode;
	private HashMap<Integer, ArrayList<PassengerRecord>> passengers;
	private List<FlightRecord> flights;

	public FlightReservationImplementation(String city, String cityCode, int port) {
		this.city = city;
		this.cityCode = cityCode;
		this.port = port;

		System.out.println("Setting up server for " + city + "...");

		int recordID = 0;

		// Loading flight record ID
		String fileName = "flightRecordID.txt";
		File tempFile = new File(fileName);
		Scanner intReader = null;
		boolean fileExists = tempFile.exists();
		if (fileExists) {
			try {
				intReader = new Scanner(new File(fileName));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			while (intReader.hasNextInt()) {
				recordID = intReader.nextInt();
			}
		} else {
			recordID = 0;
		}

		FlightRecord.setRecordCount(recordID);

		// Loading passenger record ID
		fileName = "passengerRecordID.txt";
		tempFile = new File(fileName);
		intReader = null;
		fileExists = tempFile.exists();
		if (fileExists) {
			try {
				intReader = new Scanner(new File(fileName));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			while (intReader.hasNextInt()) {
				recordID = intReader.nextInt();
			}
		} else {
			recordID = 0;
		}

		PassengerRecord.setRecordCount(recordID);

		if (flights == null) {
			flights = new ArrayList<FlightRecord>();
		}

		// Loading flight records
		String filename = cityCode + "flights.ser";

		FileInputStream fis = null;
		ObjectInputStream in = null;

		tempFile = new File(filename);
		fileExists = tempFile.exists();
		if (fileExists) {
			try {
				fis = new FileInputStream(filename);
				in = new ObjectInputStream(fis);
				flights = (List<FlightRecord>) in.readObject();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (passengers == null) {
			passengers = new HashMap<Integer, ArrayList<PassengerRecord>>();
		}

		// Loading passenger records
		filename = cityCode + "passengers.ser";

		fis = null;
		in = null;

		tempFile = new File(filename);
		fileExists = tempFile.exists();
		if (fileExists) {
			try {
				fis = new FileInputStream(filename);
				in = new ObjectInputStream(fis);
				passengers = (HashMap<Integer, ArrayList<PassengerRecord>>) in.readObject();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Used for concurrent execution with other servers, executes at runtime
	@Override
	public void run() {
		DatagramSocket aSocket = null;

		try {
			// Creating socket
			aSocket = new DatagramSocket(port);

			byte[] buffer = new byte[1000];

			// Looking for client requests from other servers
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);

				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

				byte[] newMessage = null;

				String requestMessage = new String(request.getData(), 0, request.getLength()), replyMessage = "";

				// Checking whether request is for number of booked flights or
				// reservation transfer
				if (requestMessage.equals("count")) {
					// Counting number of booked flights on this server
					int count = 0;

					for (ArrayList<PassengerRecord> lists : passengers.values()) {
						synchronized (lists) {
							for (int i = 0; i < lists.size(); i++) {
								count++;
							}
						}
					}
					replyMessage = cityCode + Constants.DELIMITER_ESCAPE + Integer.toString(count);
				} else {
					String data[] = requestMessage.split("/");

					// Checking if correct city to transfer passenger
					// reservation to
					if (data[0].equals(cityCode)) {
						// Attempting to book flight on this server with
						// received data from other server
						replyMessage = bookFlight(data[1], data[2], data[3], data[4], data[5], data[6], data[7]);
					}
				}

				// Sending back reply
				outputStream.write(replyMessage.getBytes());
				newMessage = outputStream.toByteArray();

				DatagramPacket reply = new DatagramPacket(newMessage, newMessage.length, request.getAddress(),
						request.getPort());
				aSocket.send(reply);
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}

	}

	// Used to connect via UDP to other servers
	private String call(String message) {

		List<String> list = new ArrayList<String>();

		String s = "";

		JSONReader r = new JSONReader();

		Set<Integer> serverPorts = new HashSet<Integer>();

		for (int i = 0; i < 3; i++) {
			String temp = null;
			if (i == 0) {
				temp = "MTL";
			} else if (i == 1) {
				temp = "WST";
			} else if (i == 2) {
				temp = "NDL";
			}
			if (!cityCode.equalsIgnoreCase(temp)) {
				serverPorts.add(r.getPortForKeys(USERNAME, temp));
			}
		}

		serverPorts.parallelStream().forEach((Integer) -> {

			DatagramSocket aSocket = null;

			try {
				// Creating socket
				aSocket = new DatagramSocket();

				int serverPort = Integer;

				// Storing message
				byte[] m = message.getBytes();
				InetAddress aHost = InetAddress.getByName("localhost");

				// Creating and sending packet
				DatagramPacket request = new DatagramPacket(m, message.length(), aHost, serverPort);
				aSocket.send(request);
				System.out.println("Request sent to port " + serverPort + ".");
				byte[] buffer = new byte[1000];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(reply);
				String temp = new String(reply.getData(), 0, reply.getLength());
				list.add(temp);
			} catch (SocketException e) {
				System.out.println("Socket: " + e.getMessage());
			} catch (IOException e) {
				System.out.println("IO: " + e.getMessage());
			} finally {
				if (aSocket != null)
					aSocket.close();
			}
		});

		for (int j = 0; j < list.size(); j++) {
			s += list.get(j) + " ";
		}
		return s;
	}

	// Used to write to external log files
	private synchronized void writeToLog(String content) {

		try {
			File file = new File(cityCode + "serverLog.txt");

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Used to save the passenger data
	private synchronized void savePassengers() {

		// Saving the passenger hash map
		String filename = cityCode + "passengers.ser";

		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(passengers);

			out.close();

			System.out.println("Passenger records saved.");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// Saving the passenger record ID count
		try {
			File file = new File("passengerRecordID.txt");

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(new Integer(PassengerRecord.recordCount).toString());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Used to save the flights in external files and logs
	private void saveFlights() {

		// Saving the flights list
		String filename = cityCode + "flights.ser";

		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(flights);

			out.close();

			System.out.println("Flight records saved.");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// Saving the flight record ID count
		try {
			File file = new File("flightRecordID.txt");

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(new Integer(FlightRecord.recordCount).toString());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Used by passengers to book flights
	public String bookFlight(String firstName, String lastName, String address, String phone, String destination,
			String date, String seating) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		Date d;
		String internalDate;

		// Checking for validity
		if (!seating.equalsIgnoreCase("ECONOMY") && !seating.equalsIgnoreCase("BUSINESS")
				&& !seating.equalsIgnoreCase("FIRST")) {
			return "Invalid seating.";
		} else if (destination.equals(cityCode)) {
			return "Invalid destination: flight is already originating from " + city + ".";
		} else if (!destination.equals("MTL") && !destination.equals("WST") && !destination.equals("NDL")) {
			return "Invalid destination.";
		} else {
			try {
				// Parsing the string
				String dateData[] = date.split("\\.");

				if (dateData.length != 3) {
					return "Invalid date.";
				}

				internalDate = dateData[0] + "." + (Integer.parseInt(dateData[1]) - 1) + "." + dateData[2];

				d = dateFormat.parse(internalDate);
			} catch (ParseException e) {
				e.printStackTrace();
				return "Invalid date.";
			}
		}

		// Checking if a suitable flight exists
		boolean flightExists = false;

		// Making a list of all available flights
		ArrayList<FlightRecord> list = new ArrayList<FlightRecord>();

		for (int i = 0; i < flights.size(); i++) {
			if (flights.get(i).getDestination().equals(destination)
					&& flights.get(i).getDateString().equals(internalDate)) {
				flightExists = true;
				list.add(flights.get(i));
			}
		}

		if (!flightExists) {
			return "No available flights.";
		}

		int recordID = 0;

		// Loading and updating passenger record ID
		String fileName = "passengerRecordID.txt";
		File tempFile = new File(fileName);
		Scanner intReader = null;
		boolean fileExists = tempFile.exists();
		if (fileExists) {
			try {
				intReader = new Scanner(new File(fileName));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			while (intReader.hasNextInt()) {
				recordID = intReader.nextInt();
			}
		} else {
			recordID = 0;
		}

		PassengerRecord.setRecordCount(recordID);

		boolean passengerAdded = false;

		PassengerRecord pRecord = new PassengerRecord(firstName, lastName, address, phone, destination, date, seating);

		FlightRecord fRecord = null;

		// Trying to add passenger to a flightF
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).checkSeats(seating)) {
				// Ensuring access to the flight that the passenger is added to
				// is synchronized
				synchronized (list.get(i)) {
					list.get(i).addToFlight(pRecord.getID(), seating);
					fRecord = list.get(i);
				}
				passengerAdded = true;
				break;
			}
		}

		if (!passengerAdded) {
			return "No seats remaining.";
		}

		// Creating and organizing records by the first letter of the
		// passenger's last name
		int firstLetter = lastName.charAt(0);
		ArrayList<PassengerRecord> temp;
		if (passengers.containsKey(firstLetter)) {
			temp = passengers.get(firstLetter);
		} else {
			temp = new ArrayList<PassengerRecord>();
		}

		temp.add(pRecord);

		// Ensuring access to list of passenger records by name's first letter
		// is synchronized
		synchronized (passengers) {
			passengers.put(firstLetter, temp);
		}

		writeToLog("Flight booked for passenger " + firstName + " " + lastName + " with ID " + pRecord.getID());
		savePassengers();

		return "Flight successfully booked for passenger:" + Constants.DELIMITER_ESCAPE + pRecord.getID()
				+ Constants.DELIMITER_ESCAPE + fRecord.getID() + Constants.DELIMITER_ESCAPE + cityCode
				+ Constants.DELIMITER_ESCAPE + destination + Constants.DELIMITER_ESCAPE + lastName
				+ Constants.DELIMITER_ESCAPE + firstName + Constants.DELIMITER_ESCAPE + date
				+ Constants.DELIMITER_ESCAPE + seating;
	}

	// Used by managers to get a count of the number of booked flights on this
	// server
	public String getBookedFlightCount(String recordType) {

		// Checking own server's flight records
		int count = 0;

		// Ensuring access to list of passenger records by name's first letter
		// is synchronized
		synchronized (passengers.values()) {
			for (ArrayList<PassengerRecord> lists : passengers.values()) {
				for (int i = 0; i < lists.size(); i++) {
					if (!recordType.equalsIgnoreCase("ALL")) {
						if (lists.get(i).getSeating().equalsIgnoreCase(recordType)) {
							count++;
						}
					} else {
						count++;
					}
				}
			}
		}

		// Requesting other servers' data for a count
		String msg = cityCode + Constants.DELIMITER_ESCAPE + count + Constants.DELIMITER_ESCAPE + call("count");

		writeToLog("Booked flights checked: " + msg);

		return "Booked flights:" + Constants.DELIMITER_ESCAPE + msg;
	}

	// Used by managers to add, edit or delete flight records
	public String editFlightRecord(int flightID, String fieldName, String newValue) {

		int id;

		// Determining which operation to perform
		if (fieldName.equals("add")) {
			// Adding a new flight record
			Calendar temp = Calendar.getInstance();

			// Checking whether given destination is valid
			if (newValue.equals(cityCode)) {
				return "Invalid destination: flight is already originating from " + city + ".";
			} else if ((newValue.equals("MTL") || newValue.equals("WST") || newValue.equals("NDL"))) {
				FlightRecord record = new FlightRecord(cityCode, newValue, temp, 12, 30, 20, 10);
				id = record.getID();

				// Ensuring access to flights list is synchronized
				synchronized (record) {
					flights.add(record);
				}
			} else {
				return "Invalid destination.";
			}

			writeToLog("Flight record with ID " + id + " and destination " + newValue + " created");
			saveFlights();

			return "Flight record with ID " + id + " and destination " + newValue + " successfully created.";
		} else if (fieldName.equals("delete")) {

		} else {
			// Checking if the field name given is valid
			if (!fieldName.equalsIgnoreCase("destination") && !fieldName.equalsIgnoreCase("date")
					&& !fieldName.equalsIgnoreCase("time") && !fieldName.equalsIgnoreCase("economy")
					&& !fieldName.equalsIgnoreCase("business") && !fieldName.equalsIgnoreCase("first")) {
				return "No such field name.";
			}
		}

		boolean recordExists = false;
		FlightRecord record = null;

		// Checking if the flight record with the specified ID exists
		for (int i = 0; i < flights.size(); i++) {
			int temp = flights.get(i).getID();
			if (flightID == temp) {
				recordExists = true;
				record = flights.get(i);
				break;
			}
		}

		if (recordExists) {
			if (fieldName.equals("delete")) {
				// Deleting a flight record
				// Ensuring access to flight record is synchronized
				for (int i = 0; i < flights.size(); i++) {
					int temp = flights.get(i).getID();
					if (flightID == temp) {
						synchronized (flights.get(i)) {
							// Removing affected passenger records
							for (Integer ids : flights.get(i).getPassengersOnFlight()) {
								removePassengerRecord(ids);
							}
							flights.remove(i);
							break;
						}
					}
				}

				writeToLog("Flight record with ID " + flightID + " deleted");
				saveFlights();

				return "Flight record with ID " + flightID + " successfully deleted.";
			} else {
				// Checking whether new value is valid
				if (fieldName.equals("destination")) {
					if (newValue.equals(cityCode)) {
						return "Invalid destination: flight is already originating from " + city + ".";
					} else if ((newValue.equals("MTL") || newValue.equals("WST") || newValue.equals("NDL"))) {
						record.setDestination(newValue);
					} else {
						return "Invalid destination.";
					}
				} else if (fieldName.equals("date")) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
					dateFormat.setLenient(false);
					Calendar temp = Calendar.getInstance();
					Date date;
					try {
						// Parsing the string
						date = dateFormat.parse(newValue);
						temp.setTime(date);
					} catch (ParseException e) {
						e.printStackTrace();
						return "Invalid date.";
					}
					record.setDate(temp);
				} else if (fieldName.equals("time")) {
					int i = -1;
					try {
						i = Integer.parseInt(newValue);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					if (i >= 0 && i < 24) {
						record.setTime(i);
					} else {
						return "Invalid time value. Please enter an integer between 0 and 23";
					}
				} else if (fieldName.equals("economySeats") || fieldName.equals("businessSeats")
						|| fieldName.equals("firstSeats")) {
					int i = -1;
					try {
						i = Integer.parseInt(newValue);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					if (i < 0) {
						return "Invalid number of seats. Please enter an integer greater than or equal to 0";
					}

					ArrayList<Integer> temp = new ArrayList<Integer>();

					if (fieldName.equals("economySeats")) {
						temp = record.setEconomySeats(i);
					} else if (fieldName.equals("businessSeats")) {
						temp = record.setBusinessSeats(i);
					} else if (fieldName.equals("firstSeats")) {
						temp = record.setFirstSeats(i);
					}

					if (temp.size() > 0) {
						// Removing affected passenger records
						for (int j = 0; j < temp.size(); j++) {
							removePassengerRecord(temp.get(j));
						}
						System.out.println("Excess passengers removed.");
					}
				}
				// Replacing the flight record in the list after editing
				// Ensuring access to flight record is synchronized
				for (int i = 0; i < flights.size(); i++) {
					int temp = flights.get(i).getID();
					if (flightID == temp) {
						synchronized (flights.get(i)) {
							flights.set(i, record);
						}
						break;
					}
				}

				writeToLog("Flight record with ID " + flightID + " edited");
				saveFlights();

				return "Flight record with ID " + flightID + " successfully edited.";
			}
		} else {

			return "No such flight record ID.";
		}
	}

	public String transferReservation(int passengerID, String currentCity, String otherCity) {

		String result = null;

		boolean recordExists = false;

		PassengerRecord record = null;

		// Checking if a passenger record with the specified ID exists
		for (ArrayList<PassengerRecord> lists : passengers.values()) {
			// Ensuring access to list of passenger records by name's first
			// letter is synchronized
			for (int i = 0; i < lists.size(); i++) {
				int temp = lists.get(i).getID();
				if (passengerID == temp) {
					recordExists = true;
					record = lists.get(i);
					break;
				}
			}
		}

		if (recordExists) {
			// Checking if other city is valid
			if (otherCity.equals(cityCode)) {
				return "Invalid destination: flight is already originating from " + city + ".";
			} else if ((otherCity.equals("MTL") || otherCity.equals("WST") || otherCity.equals("NDL"))) {

				String dateData[] = record.getDate().split("\\.");

				String passengerRecordData = record.getFirstName() + "/" + record.getLastName() + "/"
						+ record.getAddress() + "/" + record.getPhone() + "/" + record.getDestination() + "/"
						+ record.getDate() + "/" + record.getSeating();

				// Requesting transfer to other city server
				result = call(otherCity + "/" + passengerRecordData);

				// Checking whether the transfer was successful
				if (result.contains("Flight successfully")) {
					removePassengerRecord(record.getID());
				}
			} else {
				return "Invalid destination.";
			}
		} else {
			return "No such passenger record ID.";
		}
		return result;
	}

	// Used by managers to display flight records
	public String displayRecords() {
		String result = "";

		for (int i = 0; i < flights.size(); i++) {
			result += flights.get(i).toString() + "\n";
		}

		return result;
	}

	// Used by clients to display passenger records
	public String displayPassengerRecords() {
		String result = "";

		for (ArrayList<PassengerRecord> lists : passengers.values()) {
			for (int i = 0; i < lists.size(); i++) {
				result += lists.get(i).toString() + "\n";
			}
		}

		return result;
	}

	private void removePassengerRecord(int id) {

		// Ensuring access to list of passenger records by name's first
		// letter
		for (ArrayList<PassengerRecord> lists : passengers.values()) {
			for (int i = 0; i < lists.size(); i++) {
				int temp = lists.get(i).getID();
				if (temp == id) {
					synchronized (lists.get(i)) {
						lists.remove(i);
					}
					break;
				}
			}
		}
	}

	public String getServerName() {
		return city;
	}

	public String getCityCode() {
		return cityCode;
	}

	public String getPortNumber() {
		return Integer.toString(port);
	}

}
