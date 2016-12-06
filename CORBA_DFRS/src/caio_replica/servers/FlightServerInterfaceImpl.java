package caio_replica.servers;

import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

import org.omg.CORBA.ORB;

import caio_replica.FlightBookingServer.FlightServerInterfacePOA;
import caio_replica.models.Flight;
import caio_replica.models.FlightRecord;
import caio_replica.models.Itinerary;
import caio_replica.models.PassengerRecord;
import caio_replica.utils.Logger;

public abstract class FlightServerInterfaceImpl extends FlightServerInterfacePOA{
	public final int UDP_PORT;
	protected FlightRecord flightRecord;
	protected PassengerRecord passengerRecord;
	protected String acronym;
	protected Logger logger;
	public static final String UPDRegistry = "upd-registry.txt";
	private ORB orb;
	
	public FlightServerInterfaceImpl(String ac, int UPDPort){
		UDP_PORT = UPDPort;
		this.acronym = ac;
		logger = new Logger("./logs/servers/"+ac+".log");
		this.flightRecord = new FlightRecord();
		this.passengerRecord = new PassengerRecord();
		createInitialRecord(this.flightRecord);
		logger.log(acronym, "Server Created");
		this.createUdpServer(this);
	}
	
	public void setORB(ORB orb){
		this.orb = orb;
	}
	
	public void shutdown() {
		orb.shutdown(false);
	}
	
	// Pass flight Class as int from parser
	
	@Override
	public String bookFlight(String firstName, String lastName, String address, String tel, String dest, String date,
			String flightClass) {

		Itinerary i = null;
		try {
			Flight flight = null;
			synchronized (FlightRecord.bookingLock) {
				flight = this.flightRecord.attemptFlightReservation(this.acronym, dest, date, Integer.parseInt(flightClass));
			} 
			synchronized(flight){
				if(flight.reserveSeat(Integer.parseInt(flightClass))){
					i = new Itinerary(firstName, lastName, address, tel, flight.getFlightNumber(), date, Integer.parseInt(flightClass), acronym, dest);
					logger.log("bookFlight", "Booking Flight...");
					passengerRecord.put(lastName.charAt(0), i);
				}else{
					logger.log("bookFlight", "ERR-NO AVAILABLE SEATS");
					return "ERR-NO AVAILABLE SEATS";
				}
					
			}
		} catch(RuntimeException e){
			logger.log("bookFlight", e.getMessage());
			return e.getMessage();
		}
		logger.log("bookFlight", "Flight Booked!\n\t"+i.toString());
		return "OKK-"+i.toString();
	}
	
	private class RunnableSocketRequest implements Runnable{
		private Thread t;
		private DatagramSocket s;
		private InetAddress host;
		private int port;
		private String reply;
		private byte message[];
		
		public RunnableSocketRequest(DatagramSocket socket, InetAddress host, int port, byte[] message) {
			s = socket;
			this.host = host;
			this.port = port;
			this.message = message;
		}
		
		
		@Override
		public void run() {
			try{
				DatagramPacket req = new DatagramPacket(message, message.length, host, port);
				s.send(req);
				byte buffer[] = new byte[100];
				DatagramPacket p = new DatagramPacket(buffer, buffer.length);
				s.receive(p);
				reply = ((new String(p.getData())).trim() +" ");
				//semaphore--;
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		public void start(){
			t = new Thread(this); t.start();
		}
		
		public String getReply(){
			return reply;
		}
	}

	@Override
	public String getBookedFlightCount(String recordType) {
		DatagramSocket socket = null;
		String reply = "";
		logger.log("getBookedFlightCount", "Getting Flight Count...");
		try{
			Scanner scan = new Scanner(new FileReader(FlightServerInterfaceImpl.UPDRegistry));
			socket = new DatagramSocket();
			byte[] message =recordType.getBytes("UTF-8");
			InetAddress host = InetAddress.getByName("localhost");
			ArrayList<RunnableSocketRequest> requests = new ArrayList<RunnableSocketRequest>();
			while(scan.hasNextLine()){
				String line = scan.nextLine();
				int port = Integer.parseInt(line.split(" ")[1]);
				requests.add(new RunnableSocketRequest(socket, host, port, message));
				requests.get(requests.size() -1).start();
				
			}
			for(RunnableSocketRequest r : requests){
				r.t.join();
				reply += r.getReply();
			}
			scan.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(socket != null){
				socket.close();
			}
		}
		return reply.toString();
	}

	@Override
	public String editFlightRecord(String recordID, String fieldName, String newValues) {
		String parsedRecordID[] = recordID.split("-");
		String requester = parsedRecordID[0];
		int flightNumber = (parsedRecordID.length > 1) ? Integer.parseInt(parsedRecordID[1]) : -1;
		String reply = "";
		logger.log(requester+" editFlightRecord", flightNumber + " | " + fieldName + " | " + newValues);
		if(fieldName.equalsIgnoreCase("getall")){
			reply = "OKK-"+ this.flightRecord.toString();
		} else if(fieldName.equalsIgnoreCase("add")){
			String values[] = newValues.split("&");
			reply = "OKK-"+ this.flightRecord.add(acronym, values[0], values[1],
					Integer.parseInt(values[2]), Integer.parseInt(values[3]),
					Integer.parseInt(values[4]));
		} else if(fieldName.equalsIgnoreCase("delete")){
			Flight success = null;
			synchronized (FlightRecord.bookingLock) {
				success = this.flightRecord.deleteById(flightNumber);
			}
			this.passengerRecord.deleteFlightBookings(flightNumber);
			if(success == null){
				reply = "ERR-Flight ID not found";
			}else{
				reply = success.toString();
			}
		}else if(fieldName.contains("edit?")){
			String changedFields[] = fieldName.substring(5).split("&");
			String changedValues[] = newValues.split(",");
			if(changedFields.length == changedValues.length){
				String result="";
				synchronized (FlightRecord.bookingLock) {
					result = this.flightRecord.changeFlightAttributes(flightNumber, changedFields, changedValues);
					// TODO UPDATE PASSENGERS
				}
				// If changed date or destination, change itineraries
				if(fieldName.contains("date") || fieldName.contains("dest")){
					String changeResult = passengerRecord.changeBookingAttributesByFlight(flightNumber, changedFields, changedValues);
					logger.log(requester+" editFlightRecord-Itinerary Changed", changeResult);
				}
				reply = (result.contains("ERR")) ? result: "OKK-"+result;
			}
		}
		logger.log(requester+" editFlightRecord", reply);
		return reply;
	}
	
	@Override
	public String transferReservation(String PassengerID, String currentCity, String otherCity) {
		// Send Request to desired server
		
		DatagramSocket socket = null;
		String reply = "";
		logger.log("transferReservation", "Sending request to server...");
		try{
			Scanner scan = new Scanner(new FileReader(FlightServerInterfaceImpl.UPDRegistry));
			socket = new DatagramSocket();
			byte[] message = ("transfer?"+PassengerID+"&"+otherCity).getBytes("UTF-8");
			InetAddress host = InetAddress.getByName("localhost");
			while(scan.hasNextLine()){
				String line[] = scan.nextLine().split(" ");
				String location = line[0];
				if(location.equalsIgnoreCase(currentCity)){
					int port = Integer.parseInt(line[1]);
					DatagramPacket req = new DatagramPacket(message, message.length, host, port);
					socket.send(req);
					byte buffer[] = new byte[1000];
					DatagramPacket p = new DatagramPacket(buffer, buffer.length);
					socket.receive(p);
					reply = new String(p.getData());
					break;
				}
			}
			scan.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(socket != null){
				socket.close();
			}
		}
		return reply.toString();
	}
	
	public void setupUDPServer(int port){
		DatagramSocket socket = null;
		try{
			socket = new DatagramSocket(port);
			System.out.println("UDP Server Running...");
			logger.log(this.acronym, "UDP Server Running");
			while(true){
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				socket.receive(request);
				String data = new String(request.getData());
				if(data.startsWith("transfer")){
					String params[] = data.substring(9).trim().split("&");
					buffer = transferItinerary(Integer.parseInt(params[0]), params[1]).getBytes("UTF-8");
					DatagramPacket reply = new DatagramPacket(buffer, buffer.length, request.getAddress(), request.getPort());
					socket.send(reply);
				}else if(data.startsWith("CreateReservation")){
					String params[] = data.substring(18).split("&");
					String firstName = params[0], lastName = params[1];
					String address = params[2], tel = params[3];
					String date = params[4], destination = params[5];
					String flightClass =params[6].trim();
					buffer = bookFlight(firstName, lastName, address, tel, destination, date, flightClass).getBytes("UTF-8");
					DatagramPacket reply = new DatagramPacket(buffer, buffer.length, request.getAddress(), request.getPort());
					socket.send(reply);
				}else{
					int requestData = Integer.parseInt((data.trim()).toString());
					if(requestData >= 1 && requestData <= 4){
						logger.log(acronym, "Getting flight Count for class: "+requestData);
						buffer = (acronym.toUpperCase()+" " +this.passengerRecord.getRecordByClass(requestData)).toString()
								.getBytes("UTF-8");
						DatagramPacket reply = new DatagramPacket(buffer, buffer.length, request.getAddress(), request.getPort());
						socket.send(reply);
					}
				}
			}
		}catch(SocketException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(socket != null){
				socket.close();
				System.out.println("UDP Server Closed");
			}
		}
	}
	
	private String transferItinerary(int passengerID, String newDeparture){
		Itinerary i = this.passengerRecord.getRecordByPassengerID(passengerID);
		
		if(i == null)
			return "ERR-ITINERARY NOT FOUND";
		if(i.getDestination().equalsIgnoreCase(newDeparture))
			return "ERR-NEW DEPARTURE CANNOT BE THE SAME CITY AS THE DESTINATION";
		DatagramSocket socket = null;
		String reply = "";
		synchronized(i){
			logger.log("transferReservation", "Sending request to server...");
			Scanner scan = null;
			try{
				scan = new Scanner(new FileReader(FlightServerInterfaceImpl.UPDRegistry));
				socket = new DatagramSocket();
				byte[] message = ("CreateReservation?"+i.getFirstName()+"&"+
									i.getLastName()+"&"+
									i.getAddress() +"&"+
									i.getTelephone() + "&"+
									new SimpleDateFormat("yyyy/MM/dd").format(i.getDate())+"&"+
									i.getDestination()+"&"+
									i.getFlightClass())
						.getBytes("UTF-8");
				InetAddress host = InetAddress.getByName("localhost");
				while(scan.hasNextLine()){
					String line[] = scan.nextLine().split(" ");
					String location = line[0];
					if(location.equalsIgnoreCase(newDeparture)){
						int port = Integer.parseInt(line[1]);
						DatagramPacket req = new DatagramPacket(message, message.length, host, port);
						socket.send(req);
						byte buffer[] = new byte[1000];
						DatagramPacket p = new DatagramPacket(buffer, buffer.length);
						socket.receive(p);
						reply = new String(p.getData());
						if(reply.contains("OKK")){
							int flightNumber = i.getFlightID();
							int flightClass = i.getFlightClass();
							if(!this.passengerRecord.deleteRecord(i)){
								return "ERR-OLD ITINERARY WAS NOT DELETE";
							}else{
								String flightClassString[] = new String[1];
								if(flightClass == 1)
									flightClassString[0] = "econ";
								else if(flightClass== 2)
									flightClassString[0] = "bus";
								else if(flightClass == 3)
									flightClassString[0] = "first";
								String incr[] = {"+1"};
								this.flightRecord.changeFlightAttributes(flightNumber, flightClassString, incr);
							}
						}
						break;
					}
				}
			}catch(Exception e){
				return "ERR-OPERATION FAILED";
			}finally{
				socket.close();
				scan.close();
			}
		}
		return reply;
	}
	
	
	private void createUdpServer(FlightServerInterfaceImpl server){
		final int port = server.UDP_PORT;
		(new Thread(new Runnable() {
			@Override
			public void run() {
				server.setupUDPServer(port);
			}
		})).start();
	}
	
	
	public String getAcronym(){
		return acronym.toUpperCase();
	}
	
	public abstract void createInitialRecord(FlightRecord f);

}
