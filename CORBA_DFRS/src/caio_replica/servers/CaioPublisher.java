package caio_replica.servers;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import caio_replica.FlightBookingServer.FlightServerInterface;
import caio_replica.FlightBookingServer.FlightServerInterfaceHelper;
import caio_replica.udp_parser.UdpParser;
import json.JSONReader;
import udp_parser.UdpParserBase;

public class CaioPublisher {
	//TODO Get correct CORBA Args
	private static final String USERNAME = "Caio";
	private static final String[] CORBAArgs = {"-ORBInitialPort", "1050", "-ORBInitialHost", "localhost"};
	
	public static void main(String[] args){
		try{			
			// Create CORBA ORBS
			//ORB orb = ORB.init(CORBAArgs, null);
			
			// Initial port is set in start orb daemon script
			ORB orb = ORB.init(args, null);
			
			// Initialize ports configuration
			JSONReader jsonReader = new JSONReader();
			
			int udpParserPort = jsonReader.getPortForKeys(USERNAME, "");
			int mtlPort = jsonReader.getPortForKeys(USERNAME, "MTL");
			int wstPort = jsonReader.getPortForKeys(USERNAME, "WST");
			int ndlPort = jsonReader.getPortForKeys(USERNAME, "NDL");
			
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			// Set UDP Parser
			UdpParserBase udpParser = new UdpParser(orb, udpParserPort);
			// Spins up UdpParser
			new Thread(udpParser).start();
			// Set MTL
			MTLServer mtl = new MTLServer(mtlPort);
			mtl.setORB(orb);
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(mtl);
			FlightServerInterface href = FlightServerInterfaceHelper.narrow(ref);
			NameComponent path[] = ncRef.to_name("MTL");
			ncRef.rebind(path, href);
			// Set NDL
			NDLServer ndl = new NDLServer(ndlPort);
			ndl.setORB(orb);
			ref = rootpoa.servant_to_reference(ndl);
			href = FlightServerInterfaceHelper.narrow(ref);
			path = ncRef.to_name("NDL");
			ncRef.rebind(path, href);
			// Set WST
			WSTServer wst = new WSTServer(wstPort);
			wst.setORB(orb);
			ref = rootpoa.servant_to_reference(wst);
			href = FlightServerInterfaceHelper.narrow(ref);
			path = ncRef.to_name("WST");
			ncRef.rebind(path, href);
			// Run ORB
			for (;;){
				orb.run();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
