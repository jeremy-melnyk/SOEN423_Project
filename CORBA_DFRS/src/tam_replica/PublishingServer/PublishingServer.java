package tam_replica.PublishingServer;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

import caio_replica.udp_parser.UdpParser;
import json.JSONReader;
import tam_replica.ServerInterfaceIDL.Flight;
import tam_replica.ServerInterfaceIDL.PassengerRecord;
import tam_replica.ServerInterfaceIDL.Server;
import tam_replica.ServerInterfaceIDL.ServerIDL;
import tam_replica.ServerInterfaceIDL.ServerIDLHelper;
import udp_parser.UdpParserBase;

public class PublishingServer {
	private static final String USERNAME = "Patrick";


	public static void main(String[] args) {
		
		
JSONReader jsonReader = new JSONReader();
		try{
			
			//initialize ports
			int udpPort = jsonReader.getPortForKeys(USERNAME, "");
			int mtlPort = jsonReader.getPortForKeys(USERNAME, "MTL");
			int wstPort = jsonReader.getPortForKeys(USERNAME, "WST");
			int ndlPort = jsonReader.getPortForKeys(USERNAME, "NDL");
	
			
			//orb
			ORB orb = ORB.init(args, null);
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			
			//set up servers
			
			//mtl
			Server mtlserver=new Server("MTL",mtlPort);
			
			HashMap<Date,List<Flight>> defaultmtlflightmap= new HashMap<Date,List<Flight>>();
			HashMap<Character,List<PassengerRecord>> defaultmtlmap= new HashMap<Character,List<PassengerRecord>>();
			
			mtlserver.setMap(defaultmtlmap);
			mtlserver.setFlightMap(defaultmtlflightmap);

			//mtl orb
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(mtlserver);
			ServerIDL href = ServerIDLHelper.narrow(ref);
			NameComponent path[] = ncRef.to_name("MTL");
			ncRef.rebind(path, href);
			
			//wst
			Server wstserver=new Server("WST",wstPort);

			HashMap<Date,List<Flight>> defaultwstflightmap= new HashMap<Date,List<Flight>>();
			HashMap<Character,List<PassengerRecord>> defaultwstmap= new HashMap<Character,List<PassengerRecord>>();
			
			wstserver.setMap(defaultwstmap);
			wstserver.setFlightMap(defaultwstflightmap);
			
			//wst orb
			ref = rootpoa.servant_to_reference(wstserver);
			href = ServerIDLHelper.narrow(ref);
			path = ncRef.to_name("WST");
			ncRef.rebind(path, href);
			
			//ndl
			Server ndlserver=new Server("NDL",ndlPort);

			HashMap<Date,List<Flight>> defaultndlflightmap= new HashMap<Date,List<Flight>>();
			HashMap<Character,List<PassengerRecord>> defaultndlmap= new HashMap<Character,List<PassengerRecord>>();
			
			ndlserver.setMap(defaultndlmap);
			ndlserver.setFlightMap(defaultndlflightmap);
			
			//ndl orb
			ref = rootpoa.servant_to_reference(ndlserver);
			href = ServerIDLHelper.narrow(ref);
			path = ncRef.to_name("NDL");
			ncRef.rebind(path, href);
			
			
			// Set UDP Parser
			UdpParserBase udpParser = new UdpParser(orb, udpPort);
			
			new Thread(udpParser).start();
			
			orb.run();
			
			
		}
		catch (InvalidName e) {
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
