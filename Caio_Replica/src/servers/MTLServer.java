package servers;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import FlightBookingServer.FlightServerInterface;
import FlightBookingServer.FlightServerInterfaceHelper;
import models.FlightRecord;

public class MTLServer extends FlightServerInterfaceImpl {
	
	public MTLServer(int port){
		super("mtl", port);
	}
	
	public static void main(String args[]){
		Thread thread = null;
		try{
			ORB orb = ORB.init(args, null); 
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();
			MTLServer server = new MTLServer(2257);
			server.setORB(orb);
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(server);
			FlightServerInterface href = FlightServerInterfaceHelper.narrow(ref);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			NameComponent path[] = ncRef.to_name("MTL");
			ncRef.rebind(path, href);
			System.out.println("Montreal Server Running");
			System.out.println("Flights Available: ");
			System.out.println(server.flightRecord);
			thread = new Thread(new Runnable() {
				@Override
				public void run() {
					server.setupUDPServer(server.UDP_PORT);
				}
			});
			thread.start();
			for (;;){
				orb.run();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void createInitialRecord(FlightRecord f){
		f.add("mtl", "iad", "2016/10/03", 10, 10, 15);
		f.add("mtl", "del", "2016/10/03", 10, 10, 15);
		f.add("mtl", "iad", "2016/10/04", 10, 10, 15);
		f.add("mtl", "del", "2016/10/04", 10, 10, 15);
		f.add("mtl", "iad", "2016/10/05", 10, 10, 15);
		f.add("mtl", "del", "2016/10/05", 10, 10, 15);
	}

}
