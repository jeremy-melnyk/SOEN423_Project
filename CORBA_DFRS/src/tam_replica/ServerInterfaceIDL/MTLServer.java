package tam_replica.ServerInterfaceIDL;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.omg.CORBA.ORB;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;

import java.util.Date;
import java.text.SimpleDateFormat;



public class MTLServer extends Server {

	public MTLServer(String servername, int port) {
		super(servername, port);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args)
	{
		try {
			ORB orb=ORB.init(args,null);
			POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
					
			
			Server mtlserver=new Server("MTL",1010);
		
		
			Flight mtlflight1= new Flight (mtlserver.getNextFlightID(),"WST","01/01/2017", 5,10,15,"MTL");
			Flight mtlflight2= new Flight (mtlserver.getNextFlightID(),"NDL","01/02/2017", 10,20,30,"MTL");
			Flight mtlflight3= new Flight (mtlserver.getNextFlightID(),"WST","01/01/2020", 2,2,2,"MTL");
			
			HashMap<Date,List<Flight>> defaultmtlflightmap= new HashMap<Date,List<Flight>>();
			
			defaultmtlflightmap.put(mtlflight1.getDate(), new ArrayList<Flight>());
			defaultmtlflightmap.get(mtlflight1.getDate()).add(mtlflight1);
			defaultmtlflightmap.put(mtlflight2.getDate(), new ArrayList<Flight>());
			defaultmtlflightmap.get(mtlflight2.getDate()).add(mtlflight2);
			defaultmtlflightmap.put(mtlflight3.getDate(), new ArrayList<Flight>());
			defaultmtlflightmap.get(mtlflight3.getDate()).add(mtlflight3);
			
/*			List<Flight> defaultmtlflightlist= new ArrayList<Flight>();
			defaultmtlflightlist.add(mtlflight1);
			defaultmtlflightlist.add(mtlflight2);
			defaultmtlflightlist.add(mtlflight3);
*/			
			PassengerRecord mtlrecord1=new PassengerRecord(mtlserver.getNextRecordID(),mtlflight1,"Adam" , "Smith","22 Sherbrooke", "5149028799","WST","01/01/2017","Economy","MTL");
			PassengerRecord mtlrecord2=new PassengerRecord(mtlserver.getNextRecordID(),mtlflight2,"Bob" , "Downey","15 Jean-Talon", "1279096599","WST","01/01/2020","First","MTL");
			
			List<PassengerRecord> mtllistS=new ArrayList<PassengerRecord>();
			List<PassengerRecord> mtllistD=new ArrayList<PassengerRecord>();
			

			mtllistS.add(mtlrecord1);
			mtllistD.add(mtlrecord2);
			
			HashMap<Character,List<PassengerRecord>> defaultmtlmap= new HashMap<Character,List<PassengerRecord>>();
			defaultmtlmap.put('S',mtllistS);
			defaultmtlmap.put('D', mtllistD);
			
	//		mtlserver.setFlightlist(defaultmtlflightlist);
			mtlserver.setMap(defaultmtlmap);
			mtlserver.setFlightMap(defaultmtlflightmap);
			
			
		//	mtlserver.exportServer();
			byte [] id =rootPOA.activate_object(mtlserver);
			org.omg.CORBA.Object ref = rootPOA.id_to_reference(id);
			
			String ior = orb.object_to_string(ref);
			System.out.println(ior);
			
			PrintWriter file=new PrintWriter("iormtl.txt");
			file.println(ior);
			file.close();
			
			rootPOA.the_POAManager().activate();
			//orb.run();
			
			System.out.println("MTL Server up and running!");
			mtlserver.UDPServer();

			
			
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
