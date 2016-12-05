package ServerInterfaceIDL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import org.omg.CORBA.ORB;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;

public class WSTServer extends Server {

	public WSTServer(String servername, int port) {
		super(servername, port);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try{
			ORB orb=ORB.init(args,null);
			POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			
			Server wstserver=new Server("WST",2020);
			
/*			Flight wstflight1= new Flight (wstserver.getNextFlightID(),"NDL","02/02/2017", 5,10,15,"WST");
			Flight wstflight2= new Flight (wstserver.getNextFlightID(),"MTL","02/03/2017", 10,20,30,"WST");
			Flight wstflight3= new Flight (wstserver.getNextFlightID(),"NDL","03/06/2017", 2,2,2,"WST");
*/			
			HashMap<Date,List<Flight>> defaultwstflightmap= new HashMap<Date,List<Flight>>();
			
/*			defaultwstflightmap.put(wstflight1.getDate(), new ArrayList<Flight>());
			defaultwstflightmap.get(wstflight1.getDate()).add(wstflight1);
			defaultwstflightmap.put(wstflight2.getDate(), new ArrayList<Flight>());
			defaultwstflightmap.get(wstflight2.getDate()).add(wstflight2);
			defaultwstflightmap.put(wstflight3.getDate(), new ArrayList<Flight>());
			defaultwstflightmap.get(wstflight3.getDate()).add(wstflight3);
*/			
/*			List<Flight> defaultwstflightlist= new ArrayList<Flight>();
			defaultwstflightlist.add(wstflight1);
			defaultwstflightlist.add(wstflight2);
			defaultwstflightlist.add(wstflight3);
			*/
/*			
			PassengerRecord wstrecord1=new PassengerRecord(wstserver.getNextRecordID(),wstflight1,"Sergei" , "Gastov","22 Delhi", "5149028799","NDL","02/02/2017","Business","WST");
			PassengerRecord wstrecord2=new PassengerRecord(wstserver.getNextRecordID(),wstflight2,"Samantha" , "Brett","15 Dolla", "5149028799","NDL","03/06/2017","First","WST");
*/			
			List<PassengerRecord> wstlistG=new ArrayList<PassengerRecord>();
			List<PassengerRecord> wstlistB=new ArrayList<PassengerRecord>();
			
	//		wstlistG.add(wstrecord1);
	//		wstlistB.add(wstrecord2);
			
			HashMap<Character,List<PassengerRecord>> defaultwstmap= new HashMap<Character,List<PassengerRecord>>();
//			defaultwstmap.put('G',wstlistG);
//			defaultwstmap.put('B', wstlistB);
			
//			wstserver.setFlightlist(defaultwstflightlist);
			wstserver.setMap(defaultwstmap);
			wstserver.setFlightMap(defaultwstflightmap);
			
			
	//		wstserver.exportServer();
			byte [] id =rootPOA.activate_object(wstserver);
			org.omg.CORBA.Object ref = rootPOA.id_to_reference(id);
			
			String ior = orb.object_to_string(ref);
			System.out.println(ior);
			
			PrintWriter file=new PrintWriter("iorwst.txt");
			file.println(ior);
			file.close();
			
			rootPOA.the_POAManager().activate();
			//orb.run();
			
			
			System.out.println("WST Server up and running!");
			wstserver.UDPServer();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
