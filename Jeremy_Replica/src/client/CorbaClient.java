package client;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import enums.City;
import replica_friendly_end.FlightReservationServer;
import replica_friendly_end.FlightReservationServerHelper;

public class CorbaClient {
	protected final String NAME_SERVICE = "NameService";
	
	protected final String DELIMITER = "|";
	protected City city;
	protected String lastName;
	protected String firstName;
	protected ORB orb;
	
	public CorbaClient(City city, String lastName, String firstName, ORB orb) {
		super();
		this.city = city;
		this.lastName = lastName;
		this.firstName = firstName;
		this.orb = orb;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public ORB getOrb() {
		return orb;
	}

	public void setOrb(ORB orb) {
		this.orb = orb;
	}
	
	protected String[] getFlightRecords(){
		FlightReservationServer flightServer = getFlightServer();
		if (flightServer == null){
			System.out.println("FlightServer was null for " + city);
			return new String[0];
		}
		return flightServer.getFlightRecords();
	}
	
	protected String[] getFlightReservations(){
		FlightReservationServer flightServer = getFlightServer();
		if (flightServer == null){
			System.out.println("FlightServer was null for " + city);
			return new String[0];
		}
		return flightServer.getFlightReservations();
	}
	
	protected String[] getPassengerRecords(){
		FlightReservationServer flightServer = getFlightServer();
		if (flightServer == null){
			System.out.println("FlightServer was null for " + city);
			return new String[0];
		}
		return flightServer.getPassengerRecords();
	}

	// TODO : Replace with CORBA lookup
	protected FlightReservationServer getFlightServer(){
		org.omg.CORBA.Object nameServiceRef;
		try {
			nameServiceRef = orb.resolve_initial_references(NAME_SERVICE);
			NamingContextExt namingContextRef = NamingContextExtHelper.narrow(nameServiceRef);
			org.omg.CORBA.Object flightReservationServerRef = namingContextRef.resolve_str(city.toString());
			FlightReservationServer flightReservationServer = FlightReservationServerHelper.narrow(flightReservationServerRef);
			return flightReservationServer;
		} catch (InvalidName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotProceed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
