package mark_replica.server;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import json.JSONReader;
import mark_replica.flight_reservation_system.FlightReservation;
import mark_replica.flight_reservation_system.FlightReservationHelper;
import mark_replica.flight_reservation_system.FlightReservationImplementation;
import mark_replica.udp_parser.UdpParser;
import udp_parser.UdpParserBase;

public class FlightReservationServerPublisher {
	private static final String USERNAME = "Mark";

	public static void main(String[] args) throws InvalidName, ServantAlreadyActive, WrongPolicy, ObjectNotActive,
			FileNotFoundException, AdapterInactive {

		System.out.println("Flight Reservation Server");
		
		Set<Integer> citiesCount = new HashSet<Integer>();
		for (int i = 0; i < 3; i++) {
			citiesCount.add(i);
		}
		
		// Initialize ports configuration
		JSONReader jsonReader = new JSONReader();
		
		int udpParserPort = jsonReader.getPortForKeys(USERNAME, "");
		int mtlPort = jsonReader.getPortForKeys(USERNAME, "MTL");
		int wstPort = jsonReader.getPortForKeys(USERNAME, "WST");
		int ndlPort = jsonReader.getPortForKeys(USERNAME, "NDL");
		
		ORB orb = ORB.init(args, null);

		citiesCount.parallelStream().forEach((Integer) -> {
			try {
			POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			
			String cityCode = null;
			
			// Creating new server object for selected city
			FlightReservationImplementation flightReservationSystem = null;
			if (Integer == 0) {
				flightReservationSystem = new FlightReservationImplementation("Montreal", "MTL", mtlPort);
				cityCode = "MTL";
			} else if (Integer == 1) {
				flightReservationSystem = new FlightReservationImplementation("Washington", "WST", wstPort);
				cityCode = "WST";
			} else if (Integer == 2) {
				flightReservationSystem = new FlightReservationImplementation("New Delhi", "NDL", ndlPort);
				cityCode = "NDL";
			}
			
			byte[] id = rootPOA.activate_object(flightReservationSystem);
			org.omg.CORBA.Object ref = rootPOA.id_to_reference(id);
			
			// Bind city code using name service for easy lookup
			org.omg.CORBA.Object nameServiceRef = orb.resolve_initial_references("NameService");
			NamingContextExt namingContextRef = NamingContextExtHelper.narrow(nameServiceRef);
			NameComponent[] path = namingContextRef.to_name(cityCode);
			org.omg.CORBA.Object flightReservationSystemRef = rootPOA.servant_to_reference(flightReservationSystem);
			FlightReservation flightReservationServer = FlightReservationHelper.narrow(flightReservationSystemRef);
			namingContextRef.rebind(path, flightReservationServer);

			String ior = orb.object_to_string(ref);
			System.out.println(ior);

			PrintWriter file = new PrintWriter(cityCode + "ior.txt");
			file.println(ior);
			file.close();

			rootPOA.the_POAManager().activate();
			
			new Thread(flightReservationSystem).start();
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		// Set UDP Parser
		UdpParserBase udpParser = new UdpParser(orb, udpParserPort);
		
		// Spins up UdpParser
		new Thread(udpParser).start();
		
		orb.run();
	}
}
