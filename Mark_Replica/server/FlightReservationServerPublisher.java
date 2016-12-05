package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import flight_reservation_system.FlightReservationImplementation;

public class FlightReservationServerPublisher {

	public static void main(String[] args) throws InvalidName, ServantAlreadyActive, WrongPolicy, ObjectNotActive,
			FileNotFoundException, AdapterInactive {

		System.out.println("Flight Reservation Server");
		
		Set<Integer> citiesCount = new HashSet<Integer>();
		for (int i = 0; i < 3; i++) {
			citiesCount.add(i);
		}
		
		ORB orb = ORB.init(args, null);

		citiesCount.parallelStream().forEach((Integer) -> {
			try {
			POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			
			String cityCode = null;
			
			// Creating new server object for selected city
			FlightReservationImplementation flightReservationSystem = null;
			if (Integer == 0) {
				flightReservationSystem = new FlightReservationImplementation("Montreal", "MTL", 2020);
				cityCode = "MTL";
			} else if (Integer == 1) {
				flightReservationSystem = new FlightReservationImplementation("Washington", "WST", 2021);
				cityCode = "WST";
			} else if (Integer == 2) {
				flightReservationSystem = new FlightReservationImplementation("New Delhi", "NDL", 2022);
				cityCode = "NDL";
			}
			
			byte[] id = rootPOA.activate_object(flightReservationSystem);
			org.omg.CORBA.Object ref = rootPOA.id_to_reference(id);

			//System.out.println("Server created for " + city);

			String ior = orb.object_to_string(ref);
			System.out.println(ior);

			PrintWriter file = new PrintWriter(cityCode + "ior.txt");
			file.println(ior);
			file.close();

			rootPOA.the_POAManager().activate();
			flightReservationSystem.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		orb.run();

		/*FlightReservationImplementation flightReservationSystem[] = new FlightReservationImplementation[3];

		for (int i = 0; i < 3; i++) {
			ORB orb = ORB.init(args, null);
			POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			
			if (i == 0) {
				city = "Montreal";
				cityCode = "MTL";
				port = 2020;
				choice = true;
			} else if (i == 1) {
				city = "Washington";
				cityCode = "WST";
				port = 2021;
				choice = true;
				break;
			} else if (i == 2) {
				city = "New Delhi";
				cityCode = "NDL";
				port = 2022;
				choice = true;
			}

			// Creating new server object for selected city
			flightReservationSystem[i] = new FlightReservationImplementation(city, cityCode, port);
			byte[] id = rootPOA.activate_object(flightReservationSystem[i]);
			org.omg.CORBA.Object ref = rootPOA.id_to_reference(id);

			System.out.println("Server created for " + city);

			String ior = orb.object_to_string(ref);
			System.out.println(ior);

			PrintWriter file = new PrintWriter(cityCode + "ior.txt");
			file.println(ior);
			file.close();

			rootPOA.the_POAManager().activate();
			flightReservationSystem[i].run();
			orb.run();
		}
		
		flightReservationSystem[0].run();
		flightReservationSystem[1].run();
		flightReservationSystem[2].run();*/
	}

}
