package server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import databases.DatabaseRepository;
import databases.FlightRecordDb;
import databases.FlightRecordDbImpl;
import databases.FlightReservationDb;
import databases.FlightReservationDbImpl;
import databases.ManagerRecordDb;
import databases.ManagerRecordDbImpl;
import databases.PassengerRecordDb;
import databases.PassengerRecordDbImpl;
import enums.City;
import enums.FlightClass;
import global.Constants;
import models.FlightSeats;
import models.FlightServerAddress;

public class DistributedServer {
	private int mtlPort;
	private int wstPort;
	private int ndlPort;
	private String host;
	

	public DistributedServer(int mtlPort, int wstPort, int ndlPort, String host) {
		super();
		this.mtlPort = mtlPort;
		this.wstPort = wstPort;
		this.ndlPort = ndlPort;
		this.host = host;
	}

	public HashMap<String, IFlightReservationServer> init(){
		HashMap<String, IFlightReservationServer> flightServers = new HashMap<String, IFlightReservationServer>();
		
		// Initialization of some flight records
		
		List<String> mtlFlights = new ArrayList<String>();
		mtlFlights.add("MTL|WST|06/05/2016|10|5|2");
		mtlFlights.add("MTL|NDL|06/05/2016|10|5|2");
		
		List<String> wstFlights = new ArrayList<String>();
		wstFlights.add("WST|MTL|06/05/2016|10|5|2");
		wstFlights.add("WST|NDL|06/05/2016|10|5|2");
		
		List<String> ndlFlights = new ArrayList<String>();
		ndlFlights.add("NDL|MTL|06/05/2016|10|5|2");
		ndlFlights.add("NDL|WST|06/05/2016|10|5|2");
		
		flightServers.put("JEREMY_MTL",
				initServer(City.MTL, mtlPort,
						new FlightServerAddress[] { new FlightServerAddress(City.WST, wstPort, host),
								new FlightServerAddress(City.NDL, ndlPort, host) }, mtlFlights));
		
		flightServers.put("JEREMY_WST",
				initServer(City.WST, wstPort,
						new FlightServerAddress[] { new FlightServerAddress(City.MTL, mtlPort, host),
								new FlightServerAddress(City.NDL, ndlPort, host) }, wstFlights));
		
		flightServers.put("JEREMY_NDL",
				initServer(City.NDL, ndlPort,
						new FlightServerAddress[] { new FlightServerAddress(City.MTL, mtlPort, host),
								new FlightServerAddress(City.WST, wstPort, host) }, ndlFlights));
		
		for(Entry<String, IFlightReservationServer> entry : flightServers.entrySet()){
			IFlightReservationServer flightServer = entry.getValue();
			new Thread(flightServer).start();
		}
		
		// TODO: Add Manager Records
		
		return flightServers;
	}

	private IFlightReservationServer initServer(City city, int port, FlightServerAddress[] flightServerAddresses, List<String> flights) {
		FlightRecordDb flightRecordDb = initFlightRecordDb(flights);
		FlightReservationDb flightReservationDb = new FlightReservationDbImpl();
		PassengerRecordDb passengerRecordDb = new PassengerRecordDbImpl();
		ManagerRecordDb managerRecordDb = new ManagerRecordDbImpl();
		DatabaseRepository dbRepository = new DatabaseRepository(flightReservationDb, flightRecordDb, passengerRecordDb,
				managerRecordDb);
		
		IFlightReservationServer flightServer = new FlightReservationServerImpl(port, city, dbRepository, flightServerAddresses);
		return flightServer;
	}
	
	private FlightRecordDb initFlightRecordDb(List<String> flights){
		FlightRecordDb flightRecordDb = new FlightRecordDbImpl();
		for (String flight : flights){
			String[] tokens = flight.split(Constants.DELIMITER_ESCAPE);
			HashMap<FlightClass, FlightSeats> flightClasses = new HashMap<FlightClass, FlightSeats>();
			flightClasses.put(FlightClass.FIRST, new FlightSeats(Integer.parseInt(tokens[3])));
			flightClasses.put(FlightClass.BUSINESS, new FlightSeats(Integer.parseInt(tokens[4])));
			flightClasses.put(FlightClass.ECONOMY, new FlightSeats(Integer.parseInt(tokens[5])));
			Date date = new Date();
			try {
				date = new SimpleDateFormat(Constants.DATE_FORMAT).parse(tokens[2]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			flightRecordDb.addFlightRecord(City.valueOf(tokens[0]), City.valueOf(tokens[1]), date, flightClasses);
		}
		return flightRecordDb;
	}
}
