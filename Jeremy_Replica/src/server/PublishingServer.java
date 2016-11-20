package server;

import java.util.HashMap;
import java.util.Map;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import replica_friendly_end.FlightReservationServer;
import replica_friendly_end.FlightReservationServerHelper;

public class PublishingServer {
	private static final int MTL_PORT = 50152;
	private static final int WST_PORT = 50153;
	private static final int NDL_PORT = 50154;
	private static final String HOST = "localhost";
	private static final String ROOT_POA = "RootPOA";
	private static final String NAME_SERVICE = "NameService";

	public static void main(String[] args) {
		// Initialize Servers
		DistributedServer server = new DistributedServer(MTL_PORT, WST_PORT, NDL_PORT, HOST);
		HashMap<String, IFlightReservationServer> flightServers = server.init();
		
		// Initialize ORB
		ORB orb = ORB.init(args, null);
		try {
			POA rootPOA = POAHelper.narrow(orb.resolve_initial_references(ROOT_POA));
			rootPOA.the_POAManager().activate();
			
			org.omg.CORBA.Object nameServiceRef = orb.resolve_initial_references(NAME_SERVICE);
			NamingContextExt namingContextRef = NamingContextExtHelper.narrow(nameServiceRef);
			
			// Register servers with ORB
			for (Map.Entry<String, IFlightReservationServer> entry : flightServers.entrySet()){
				FlightReservationServerImpl flightReservationServerImpl = (FlightReservationServerImpl) entry.getValue();
				org.omg.CORBA.Object flightReservationServerRef = rootPOA.servant_to_reference(flightReservationServerImpl);
				FlightReservationServer flightReservationServer = FlightReservationServerHelper.narrow(flightReservationServerRef);
				String name = entry.getKey();
				NameComponent[] path = namingContextRef.to_name(name);
				namingContextRef.rebind(path, flightReservationServer);
			}
			
			System.out.println("FlightReservationServers published.");
		} catch (InvalidName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AdapterInactive e) {
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
		} catch (ServantNotActive e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongPolicy e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
