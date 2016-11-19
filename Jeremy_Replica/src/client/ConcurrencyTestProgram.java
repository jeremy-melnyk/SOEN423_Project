package client;

import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.ORB;

import enums.City;

public class ConcurrencyTestProgram {

	public static void main(String[] args) {
		// Initialize ORB
		ORB orb = ORB.init(args, null);
		
		ManagerClient managerMtl = new ManagerClient(City.MTL, "Moose", "Daniel", 1111, orb);
		ManagerClient managerWst = new ManagerClient(City.WST, "Crook", "Peter", 1111, orb);
		ManagerClient managerNdl = new ManagerClient(City.NDL, "Adams", "Samuel", 1111, orb);	
		List<Thread> threads = new ArrayList<Thread>();
		
		int count = 50;
		while(--count >= 0){
			Thread t0 = new Thread(() -> {
				managerMtl.getBookedFlightCount("BUSINESS");
				managerMtl.editFlightRecord("EDIT|0", "ORIGIN", "WST");
				managerMtl.editFlightRecord("EDIT|0", "DESTINATION", "MTL");
				managerMtl.editFlightRecord("EDIT|0", "DATE", "10-Jul-2017");
				managerMtl.editFlightRecord("EDIT|0", "SEATS", "FIRST|30");
				managerMtl.editFlightRecord("EDIT|0", "SEATS", "FIRST|10");
				managerMtl.editFlightRecord("ADD|0", "NONE", "MTL|NDL|15-Jul-2017|5|5|5");
				managerMtl.editFlightRecord("REMOVE|0", "NONE", "");
				managerMtl.getBookedFlightCount("FIRST");
				
				String[] passengerRecords = managerMtl.getPassengerRecords();
				for (String passengerRecord : passengerRecords){
					System.out.println(passengerRecord);
				}
			});
			t0.start();
			threads.add(t0);
			
			Thread t1 = new Thread(() -> {
				managerWst.getBookedFlightCount("ECONOMY");
				managerWst.editFlightRecord("EDIT|5", "ORIGIN", "WST");
				managerWst.editFlightRecord("EDIT|2", "DESTINATION", "MTL");
				managerWst.editFlightRecord("EDIT|0", "DATE", "10-Jul-2017");
				managerWst.editFlightRecord("EDIT|4", "SEATS", "FIRST|30");
				managerWst.editFlightRecord("EDIT|0", "SEATS", "FIRST|10");
				managerWst.editFlightRecord("ADD|0", "NONE", "MTL|NDL|15-Jul-2017|5|5|5");
				managerWst.editFlightRecord("REMOVE|3", "NONE", "");
				managerWst.getBookedFlightCount("FIRST");
				
				String[] passengerRecords = managerWst.getPassengerRecords();
				for (String passengerRecord : passengerRecords){
					System.out.println(passengerRecord);
				}
			});
			t1.start();
			threads.add(t1);
			
			Thread t2 = new Thread(() -> {
				managerNdl.getBookedFlightCount("FIRST");
				managerNdl.editFlightRecord("EDIT|5", "ORIGIN", "WST");
				managerNdl.editFlightRecord("EDIT|2", "DESTINATION", "MTL");
				managerNdl.editFlightRecord("EDIT|0", "DATE", "10-Jul-2017");
				managerNdl.editFlightRecord("EDIT|4", "SEATS", "FIRST|30");
				managerNdl.editFlightRecord("EDIT|0", "SEATS", "FIRST|10");
				managerNdl.editFlightRecord("ADD|0", "NONE", "MTL|NDL|15-Jul-2017|5|5|5");
				managerNdl.editFlightRecord("REMOVE|3", "NONE", "");
				managerNdl.getBookedFlightCount("FIRST");
				
				String[] passengerRecords = managerNdl.getPassengerRecords();
				for (String passengerRecord : passengerRecords){
					System.out.println(passengerRecord);
				}
			});
			t2.start();
			threads.add(t2);
			
			Thread t3 = new Thread(() -> {
				PassengerClient passengerMtl = new PassengerClient(City.MTL, "Doe", "John", "1000 Moose Avenue|Montreal|QC|H1B 5U0|Canada", "514-123-4567", orb);
				passengerMtl.bookFlight("WST", "5-Jun-2016", "FIRST");
				passengerMtl.bookFlight("NDL", "5-Jun-2016", "FIRST");
			});
			t3.start();
			threads.add(t3);
			
			Thread t4 = new Thread(() -> {
				PassengerClient passengerWst = new PassengerClient(City.WST, "Bell", "Graham", "5000 Donald Trump|Washington|DC|12345|United States", "648-123-4567", orb);
				passengerWst.bookFlight("NDL", "5-Jun-2016", "BUSINESS");
				passengerWst.bookFlight("MTL", "5-Jun-2016", "BUSINESS");
			});
			t4.start();
			threads.add(t4);
			
			Thread t5 = new Thread(() -> {
				PassengerClient passengerNdl = new PassengerClient(City.NDL, "Bobby", "Brown", "5000 Connaught Place|New Delhi|DC|12345|India", "648-123-4567", orb);
				passengerNdl.bookFlight("MTL", "5-Jun-2016", "ECONOMY");
				passengerNdl.bookFlight("WST", "5-Jun-2016", "ECONOMY");
			});
			t5.start();
			threads.add(t5);
		}
		
		// Join threads
		for(Thread thread : threads){
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println();	
		System.out.println("Threads finished!");
		System.out.println();	
		managerMtl.getBookedFlightCount("FIRST");
		managerMtl.getBookedFlightCount("BUSINESS");
		managerMtl.getBookedFlightCount("ECONOMY");
		System.out.println();	
		System.out.println("MTL");	
		display(managerMtl.getPassengerRecords());
		display(managerMtl.getFlightRecords());
		display(managerMtl.getFlightReservations());
		System.out.println();	
		System.out.println("WST");	
		display(managerWst.getPassengerRecords());
		display(managerWst.getFlightRecords());
		display(managerWst.getFlightReservations());
		System.out.println();
		System.out.println("NDL");	
		display(managerNdl.getPassengerRecords());
		display(managerNdl.getFlightRecords());
		display(managerNdl.getFlightReservations());
	}
	
	public static void display(String[] items) {
		if(items.length == 0){
			System.out.println("Nothing to display!");
			return;
		}
		for (String item : items) {
			System.out.println(item);
		}
	}
}
