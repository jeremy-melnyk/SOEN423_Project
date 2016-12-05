package tam_replica.ServerInterfaceIDL;
import java.io.PrintWriter;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;

import org.omg.CORBA.ORB;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class test  extends Thread{
	private ServerIDL mtlserver;
	private ServerIDL ndlserver;
	private ServerIDL wstserver;

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub

		try{
		ORB orb= ORB.init(args,null);
		
		BufferedReader brmtl= new BufferedReader (new FileReader("iormtl.txt"));
		String iormtl=brmtl.readLine();
		brmtl.close();
		
		org.omg.CORBA.Object omtl=orb.string_to_object(iormtl);
		
		ServerIDL mtlserver=ServerIDLHelper.narrow(omtl);
		
		BufferedReader brwst= new BufferedReader (new FileReader("iorwst.txt"));
		String iorwst=brwst.readLine();
		brwst.close();
		
		org.omg.CORBA.Object owst=orb.string_to_object(iorwst);
		
		ServerIDL wstserver=ServerIDLHelper.narrow(owst);
		
		BufferedReader brndl= new BufferedReader (new FileReader("iorndl.txt"));
		String iorndl=brndl.readLine();
		brndl.close();
		
		org.omg.CORBA.Object ondl=orb.string_to_object(iorndl);
		
		ServerIDL ndlserver=ServerIDLHelper.narrow(ondl);
		
		
		
		test test=new test();
		test test2=new test();
		test test3=new test();
		test.mtlserver=mtlserver;
		test.wstserver=wstserver;
		test.ndlserver=ndlserver;
		
		test2.mtlserver=mtlserver;
		test2.wstserver=wstserver;
		test2.ndlserver=ndlserver;
		
		test3.mtlserver=mtlserver;
		test3.wstserver=wstserver;
		test3.ndlserver=ndlserver;
		
		test.start();
		test2.start();
		test3.start();
		
		}
		catch (Exception e){
			System.out.println("Error");
		}
		

		
		

		
	}

	
	public void run(){
		
		
		try{
			
			
			//testing code from Assignment 1. uncomment to run it.
	/*		
			
			
			//testing bookflight
			mtlserver.bookFlight("goodfirstname", "124badlastname", "address", "1231234123", "WST", "01/01/2017 12:00:00 AM", "First");//bad lastname
			mtlserver.bookFlight("12badfirstname", "goodlastname", "address", "1231234123", "WST", "01/01/2017 12:00:00 AM", "First");//bad firstname
			mtlserver.bookFlight("Firstname", "Goodlastname", "address", "123456789t", "WST", "01/01/2017 12:00:00 AM", "First");// bad phone
			mtlserver.bookFlight("Firstname", "Goodlastname", "address", "1231234123", "CTY", "01/01/2017 12:00:00 AM", "First");// bad city
			mtlserver.bookFlight("Firstname", "Goodlastname", "address", "1231234123", "WST", "Jan/01/2017 12:00:00 AM", "First");//bad date
			mtlserver.bookFlight("Firstname", "Goodlastname", "address", "1231234123", "WST", "01/01/2017 12:00:00 AM", "wherever");//bad class
			mtlserver.bookFlight("Firstname", "Goodlastname", "address", "1231234123", "MTL", "01/01/2017 12:00:00 AM", "First");//same destination as starting;
			
			mtlserver.bookFlight("Firstname", "Goodlastname", "address", "1231234123", "WST", "01/01/2017 12:00:00 AM", "First");// good
			
			//testing getbookedflightcount
			mtlserver.getBookedFlightCount("First");//no manager
			mtlserver.getBookedFlightCount("TL2111.First");//invalid manager id
			mtlserver.getBookedFlightCount(".First");//no managerid
			mtlserver.getBookedFlightCount("WST2111.First");//improper manager id
			mtlserver.getBookedFlightCount("MTL2111.wherever");//bad classtype
			
			mtlserver.getBookedFlightCount("MTL5111.Economy");//good
			mtlserver.getBookedFlightCount("MTL5111.Business");
			mtlserver.getBookedFlightCount("MTL5111.ALL");
			
			
			mtlserver.editFlightRecord("MTL5111.101", "create.dest.date.first.business.economy", "WST.01/01/2017 1:00:00 AM.5.10.15");//good
			
			mtlserver.editFlightRecord("MTL2111.101", "create.dest.date.first.business.economy", "MTL.01/01/2017 1:00:00 AM.5.10.15");//create same city bad
			mtlserver.editFlightRecord("s1.101", "create.dest.date.first.business.economy", "MTL.01/01/2017 1:00:00 AM.5.10.15");		//bad manager
			mtlserver.editFlightRecord("MTL2111.101", "dest.date.first.business.economy", "MTL.01/01/2017 1:00:00 AM.5.10.15"); // no action
			mtlserver.editFlightRecord("MTL2111.101", "randomaction.dest.date.first.business.economy", "MTL.01/01/2017 1:00:00 AM.5.10.15"); // bad action
			mtlserver.editFlightRecord("MTL2111.101", "create.notdest.date.first.business.economy", "MTL.01/01/2017 1:00:00 AM.5.10.15"); //invalid first field
			mtlserver.editFlightRecord("MTL2111.101", "create.dest.notdate.first.business.economy", "MTL.01/01/2017 1:00:00 AM.5.10.15"); // invalid second field
			mtlserver.editFlightRecord("MTL2111.101", "create.dest.date.first.business.economy", "01/01/2017 1:00:00 AM.MTL.5.10.15");// swapped field values
			mtlserver.editFlightRecord("MTL2111.101", "create.dest.date.first.business.economy", "MTL.01/01/2017 1:00:00 AM.510.15");//improper array
			mtlserver.editFlightRecord("MTl2111.101", "create.dest.date.first.business.economy", "MTL.01/01/2017 1:00:00 AM.5.10.15");//starting destination same as final
			mtlserver.editFlightRecord("WST2111.101", "create.dest.date.first.business.economy", "WST.01/01/2017 1:00:00 AM.5.10.15");//manager from different city
			mtlserver.editFlightRecord("MTL2111.101", "create.dest.first.business.economy", "NDL.5.10.15");								//missing date which is required for flight
			mtlserver.editFlightRecord("MTL2111.101", "create.date.first.business.economy", "01/01/2097 1:00:00 AM.5.10.15");//missing destination
			mtlserver.editFlightRecord("MTL2111.101", "create.dest.date.first.first.economy", "WST.01/01/2017 1:00:00 AM.5.10.15");//duplicate field


			//sucessful create, then deletes 2 different flights without certain parameters. Finally tries to delete a flight that DNE and thus fails
			wstserver.editFlightRecord("WST5111.101", "create.dest.date.first.business.economy", "NDL.01/01/2097 1:00:00 AM.5.10.15");//create good
			wstserver.editFlightRecord("WST5111.102", "delete.doesntmatter.", "whatever...");										//delete good
			wstserver.editFlightRecord("WST5111.100", "delete.doesntmatter.", "");										//delete good
			
			wstserver.editFlightRecord("WST2111.100", "delete.doesntmatter.", "");										//Flight DNE, should fail

			
			
			//create flights+bookings, getbookedcount, delete flight and then the getbookedcount should decrease
			wstserver.editFlightRecord("WST5111.101", "create.dest.date.first.business.economy", "NDL.01/01/2100 1:00:00 AM.5.10.15");//create good. testing unique flightID
			wstserver.editFlightRecord("WST5111.", "create.dest.date.first.business.economy", "NDL.01/01/2100 1:00:00 AM.5.10.15");//good create even though flightID doesn't exist
			wstserver.bookFlight("Firstname", "Goodlastname", "address", "1231234123", "NDL", "01/01/2100 1:00:00 AM", "First");// good testing unique RecordID
			wstserver.bookFlight("Firstname", "Goodlastname", "address", "1231234123", "NDL", "01/01/2100 1:00:00 AM", "First");// good
			wstserver.bookFlight("Firstname", "Goodlastname", "address", "1231234123", "NDL", "01/01/2100 1:00:00 AM", "First");// good
			wstserver.bookFlight("Firstname", "Goodlastname", "address", "1231234123", "NDL", "01/01/2100 1:00:00 AM", "First");// good
			wstserver.bookFlight("Firstname", "Goodlastname", "address", "1231234123", "NDL", "01/01/2100 1:00:00 AM", "First");// good
			wstserver.bookFlight("Firstname", "Goodlastname", "address", "1231234123", "NDL", "01/01/2100 1:00:00 AM", "First");// good
			wstserver.getBookedFlightCount("WST5111.First");//good
			wstserver.editFlightRecord("WST5111.104", "delete.doesntmatter.", "");										//delete good
			wstserver.editFlightRecord("WST5111.105", "delete.doesntmatter.", "");										//delete good
			wstserver.editFlightRecord("WST5111.106", "delete.doesntmatter.", "");										//delete good

			wstserver.getBookedFlightCount("WST5111.First");// the number should be lower
			


			
			///synchronization testing
			ndlserver.bookFlight("Firstname", "Goodlastname", "address", "1231234123", "MTL", "01/01/2020 12:00:00 AM", "First");// Book 3 times, but only 2 seats availible
			ndlserver.bookFlight("Firstname", "Goodlastname", "address", "1231234123", "MTL", "01/01/2020 12:00:00 AM", "First");// good
			ndlserver.bookFlight("Firstname", "Goodlastname", "address", "1231234123", "MTL", "01/01/2020 12:00:00 AM", "First");// good but will fail due to lack of seating

			ndlserver.editFlightRecord("NDL2111.102", "edit.first.", "1");//error, already booked
			ndlserver.bookFlight("Firstname", "Goodlastname", "address", "1231234123", "MTL", "03/06/2017 12:00:00 AM", "First");// good
			ndlserver.bookFlight("Firstname", "Goodlastname", "address", "1231234123", "MTL", "03/06/2017 12:00:00 AM", "First");// good
			ndlserver.editFlightRecord("NDL5111.102", "edit.first.", "10");//sets to 10

			
*/
			
			////////All Managers with a 5 should succeed on their tests, all managers with a 2 should fail their tests
			

			
/*			//testing transferreservation
			System.out.println(mtlserver.transferReservation(1000, "MTL111a", "NDL"));//bad managerid
			System.out.println(mtlserver.transferReservation(1000, "YTL9111", "NDL"));//bad original city
			System.out.println(mtlserver.transferReservation(1000, "MTL9111", "HDL"));//bad new city
			
			System.out.println(mtlserver.getBookedFlightCount("MTL9111.ALL"));
			System.out.println(mtlserver.transferReservation(1000, "MTL9111", "WST"));//no flights availible in WST
			System.out.println(mtlserver.getBookedFlightCount("MTL9111.ALL"));
			System.out.println(mtlserver.transferReservation(1600, "MTL9111", "NDL"));//no record ID matching specified ID
			System.out.println(mtlserver.getBookedFlightCount("MTL9111.ALL"));
			System.out.println(ndlserver.transferReservation(1001, "WST9111", "MTL"));//managerID is not matching the server
			System.out.println(mtlserver.getBookedFlightCount("MTL9111.ALL"));
			System.out.println(ndlserver.transferReservation(1001, "NDL9111", "MTL"));//good
			System.out.println(mtlserver.getBookedFlightCount("MTL9111.ALL"));
			
	*/		



			




		}
		
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
