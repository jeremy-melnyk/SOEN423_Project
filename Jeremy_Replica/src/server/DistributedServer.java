package server;

<<<<<<< refs/remotes/origin/master
import java.text.ParseException;
import java.text.SimpleDateFormat;
=======
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
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
<<<<<<< refs/remotes/origin/master
import global.Constants;
=======
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
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
<<<<<<< refs/remotes/origin/master
		mtlFlights.add("MTL|WST|06/05/2016|10|5|2");
		mtlFlights.add("MTL|NDL|06/05/2016|10|5|2");
		
		List<String> wstFlights = new ArrayList<String>();
		wstFlights.add("WST|MTL|06/05/2016|10|5|2");
		wstFlights.add("WST|NDL|06/05/2016|10|5|2");
		
		List<String> ndlFlights = new ArrayList<String>();
		ndlFlights.add("NDL|MTL|06/05/2016|10|5|2");
		ndlFlights.add("NDL|WST|06/05/2016|10|5|2");
=======
		mtlFlights.add("MTL|WST|5-Jun-2016|10|5|2");
		mtlFlights.add("MTL|NDL|5-Jun-2016|10|5|2");
		
		List<String> wstFlights = new ArrayList<String>();
		wstFlights.add("WST|MTL|5-Jun-2016|10|5|2");
		wstFlights.add("WST|NDL|5-Jun-2016|10|5|2");
		
		List<String> ndlFlights = new ArrayList<String>();
		ndlFlights.add("NDL|MTL|5-Jun-2016|10|5|2");
		ndlFlights.add("NDL|WST|5-Jun-2016|10|5|2");
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
		
		flightServers.put("MTL",
				initServer(City.MTL, mtlPort,
						new FlightServerAddress[] { new FlightServerAddress(City.WST, wstPort, host),
								new FlightServerAddress(City.NDL, ndlPort, host) }, mtlFlights));
		
		flightServers.put("WST",
				initServer(City.WST, wstPort,
						new FlightServerAddress[] { new FlightServerAddress(City.MTL, mtlPort, host),
								new FlightServerAddress(City.NDL, ndlPort, host) }, wstFlights));
		
		flightServers.put("NDL",
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
	
<<<<<<< refs/remotes/origin/master
	private FlightRecordDb initFlightRecordDb(List<String> flights){
		FlightRecordDb flightRecordDb = new FlightRecordDbImpl();
		for (String flight : flights){
			String[] tokens = flight.split(Constants.DELIMITER_ESCAPE);
=======
	@SuppressWarnings("deprecation")
	private FlightRecordDb initFlightRecordDb(List<String> flights){
		FlightRecordDb flightRecordDb = new FlightRecordDbImpl();
		for (String flight : flights){
			String[] tokens = flight.split("\\|");
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
			HashMap<FlightClass, FlightSeats> flightClasses = new HashMap<FlightClass, FlightSeats>();
			flightClasses.put(FlightClass.FIRST, new FlightSeats(Integer.parseInt(tokens[3])));
			flightClasses.put(FlightClass.BUSINESS, new FlightSeats(Integer.parseInt(tokens[4])));
			flightClasses.put(FlightClass.ECONOMY, new FlightSeats(Integer.parseInt(tokens[5])));
<<<<<<< refs/remotes/origin/master
			Date date = new Date();
			try {
				date = new SimpleDateFormat(Constants.DATE_FORMAT).parse(tokens[2]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			flightRecordDb.addFlightRecord(City.valueOf(tokens[0]), City.valueOf(tokens[1]), date, flightClasses);
=======
			flightRecordDb.addFlightRecord(City.valueOf(tokens[0]), City.valueOf(tokens[1]), new Date(tokens[2]), flightClasses);
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
		}
		return flightRecordDb;
	}
}
