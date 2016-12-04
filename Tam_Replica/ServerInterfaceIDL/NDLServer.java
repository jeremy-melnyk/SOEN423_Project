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

public class NDLServer extends Server {

	public NDLServer(String servername, int port) {
		super(servername, port);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try{
			ORB orb=ORB.init(args,null);
			POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			
			Server ndlserver=new Server("NDL",3030);
			
			Flight ndlflight1= new Flight (ndlserver.getNextFlightID(),"MTL","03/03/2017", 5,10,15,"NDL");
			Flight ndlflight2= new Flight (ndlserver.getNextFlightID(),"WST","03/04/2017", 10,20,30,"NDL");
			Flight ndlflight3= new Flight (ndlserver.getNextFlightID(),"WST","01/01/2020", 2,2,2,"NDL");
			
			HashMap<Date,List<Flight>> defaultndlflightmap= new HashMap<Date,List<Flight>>();
			
			defaultndlflightmap.put(ndlflight1.getDate(), new ArrayList<Flight>());
			defaultndlflightmap.get(ndlflight1.getDate()).add(ndlflight1);
			defaultndlflightmap.put(ndlflight2.getDate(), new ArrayList<Flight>());
			defaultndlflightmap.get(ndlflight2.getDate()).add(ndlflight2);
			defaultndlflightmap.put(ndlflight3.getDate(), new ArrayList<Flight>());
			defaultndlflightmap.get(ndlflight3.getDate()).add(ndlflight3);
			
/*			List<Flight> defaultndlflightlist= new ArrayList<Flight>();
			defaultndlflightlist.add(ndlflight1);
			defaultndlflightlist.add(ndlflight2);
			defaultndlflightlist.add(ndlflight3);
*/			
			PassengerRecord ndlrecord1=new PassengerRecord(ndlserver.getNextRecordID(),ndlflight1,"Sergei" , "Gastov","22 Delhi", "5149028799","MTL","03/03/2017","Business","NDL");
			PassengerRecord ndlrecord2=new PassengerRecord(ndlserver.getNextRecordID(),ndlflight2,"Samantha" , "Brett","15 Dolla", "5149028799","WST","01/01/2020","First","NDL");
			
			List<PassengerRecord> ndllistG=new ArrayList<PassengerRecord>();
			List<PassengerRecord> ndllistB=new ArrayList<PassengerRecord>();
			
			ndllistG.add(ndlrecord1);
			ndllistB.add(ndlrecord2);
			
			HashMap<Character,List<PassengerRecord>> defaultndlmap= new HashMap<Character,List<PassengerRecord>>();
			defaultndlmap.put('G',ndllistG);
			defaultndlmap.put('B', ndllistB);
			
//			ndlserver.setFlightlist(defaultndlflightlist);
			ndlserver.setMap(defaultndlmap);
			ndlserver.setFlightMap(defaultndlflightmap);
			
			
		//	ndlserver.exportServer();
			byte [] id =rootPOA.activate_object(ndlserver);
			org.omg.CORBA.Object ref = rootPOA.id_to_reference(id);
			
			String ior = orb.object_to_string(ref);
			System.out.println(ior);
			
			PrintWriter file=new PrintWriter("iorndl.txt");
			file.println(ior);
			file.close();
			
			rootPOA.the_POAManager().activate();
			//orb.run();
			
			
			
			System.out.println("NDL Server up and running!");
			ndlserver.UDPServer();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
