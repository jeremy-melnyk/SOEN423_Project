package ServerInterfaceIDL;

import java.util.Date;
import java.text.SimpleDateFormat;

import java.text.ParseException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import java.net.*;
import java.io.*;








public class Server extends ServerIDLPOA{

	private String servername;
	private int port;
	private HashMap<Character,List<PassengerRecord>> map = new HashMap<Character,List<PassengerRecord>>();
	private HashMap<Date,List<Flight>> flightmap =new HashMap<Date,List<Flight>>();
	private List<Flight> flightlist = new ArrayList<Flight>();
	private AtomicInteger flightID=new AtomicInteger(100);
	private AtomicInteger recordID=new AtomicInteger(1000);
	

	public String bookFlight(String firstname, String lastname, String address, String phone, String originanddestination, String date, String flightclass) {

		String[] destsplit=originanddestination.split("\\|");
		String destination=destsplit[1];
		Logger log1=new Logger(servername);
		String operation="BookFlight";
		

		String pattern="MM/dd/yyyy";
		SimpleDateFormat format= new SimpleDateFormat(pattern);
		Flight flightfound;
		int newrecordid=this.getNextRecordID();

		//try with hashmapflight
		for (Map.Entry<Date, List<Flight>> entry : flightmap.entrySet())
		{
			
			try {//finds a flight
				Flight flight=new Flight();
				PassengerRecord newrecord = new PassengerRecord(newrecordid, flight,firstname, lastname, address, phone, destination, date, flightclass,servername);
		
				if (format.parse(date).compareTo(entry.getKey())==0)
				{
	
					for (int i=0;i<entry.getValue().size();i++)
					{

						if (entry.getValue().get(i).getDestination().equalsIgnoreCase(destination))
						{

							flightfound=entry.getValue().get(i);
							synchronized (this){
								newrecord.setFlight(flightfound);	
							}
							newrecord.setFlightID(flightfound.getFlightID());
							if (newrecord.getValid()==true)
							{			
								Flight chosenflight=entry.getValue().get(i);
								
								//if the flight is full, it jumps to the next iteration and checks another flight.
								if (flightclass.equalsIgnoreCase("Business") && chosenflight.getCurrentBusinessSeats()==chosenflight.getTotalBusinessSeats()){
									continue;
								}
								else if (flightclass.equalsIgnoreCase("First") && chosenflight.getCurrentFirstSeats()==chosenflight.getTotalFirstSeats()){
									continue;
								}
								else if (flightclass.equalsIgnoreCase("Economy") && chosenflight.getCurrentEconomySeats()==chosenflight.getTotalEconomySeats()){
									continue;
								}
								
								//otherwise tries to add passenger record to the hashmap
								char lastnamechar=newrecord.getLastNameChar();
								try{
									List<PassengerRecord> templist=map.get(lastnamechar);
									synchronized (this){
										templist.add(newrecord);
									}
									newrecord.setRecordID(newrecordid);
									
									
									//increments flightcounter
									if (flightclass.equalsIgnoreCase("Business"))
									{
										chosenflight.setCurrentBusinessSeats(chosenflight.getCurrentBusinessSeats()+1);
									}
									else if (flightclass.equalsIgnoreCase("First"))
									{
										chosenflight.setCurrentFirstSeats(chosenflight.getCurrentFirstSeats()+1);
									}
									else if (flightclass.equalsIgnoreCase("Economy"))
									{
										chosenflight.setCurrentEconomySeats(chosenflight.getCurrentEconomySeats()+1);
									}
								}
								//in the event that the key does not exist (the first time a person with that letter for their last name comes up)
								//puts that letter into the hashmap and then creates the record
								catch (Exception e)
								{
									map.put(lastnamechar, new ArrayList<PassengerRecord>());
									
									List<PassengerRecord> templist=map.get(lastnamechar);
									synchronized (this){
										templist.add(newrecord);
									}
									newrecord.setRecordID(newrecordid);

									
									//increments flightcounter
									if (flightclass.equalsIgnoreCase("Business"))
									{
										chosenflight.setCurrentBusinessSeats(chosenflight.getCurrentBusinessSeats()+1);
									}
									else if (flightclass.equalsIgnoreCase("First"))
									{
										chosenflight.setCurrentFirstSeats(chosenflight.getCurrentFirstSeats()+1);
									}
									else if (flightclass.equalsIgnoreCase("Economy"))
									{
										chosenflight.setCurrentEconomySeats(chosenflight.getCurrentEconomySeats()+1);
									}
								}
								
							
							log1.writeToManagers("Success. FlightID" +flight.getFlightID() + destination.toUpperCase() + " on " + date + "RecordID: " +newrecordid, servername);
							log1.writetoFile("Success. RecordID: " +newrecordid + " Name: " +lastname+", " +firstname + " Address: " +address +" Phone: " +phone +
									" Dest: "+ destination.toUpperCase() + " Date: " + date + " FlightClass: " + flightclass + " FlightID: " + flightfound.getFlightID(), operation,true);
							return "Successfully booked a flight to " + destination.toUpperCase() + " on " + date + " RecordID: " +newrecordid;	
							

						}
						}
					}
					//
					
					
				log1.writeToManagers("Error creating PassengerRecord", servername);
				log1.writetoFile("Error creating PassengerRecord",operation,true);
				return "Error creating PassengerRecord";
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			log1.writeToManagers("Error. Improper Date", servername);
			log1.writetoFile("Error. Improper Date",operation,true);
			return "Error. Improper Date";
		}
		catch (Exception e){
			log1.writeToManagers(e.getMessage(), servername);
			log1.writetoFile(e.getMessage(),operation,true);
			return e.getMessage();
		}
						

		}
		
		log1.writeToManagers("There are no availible seats/flights to " + destination.toUpperCase() + " on " + date, servername);
		log1.writetoFile("There are no availible seats/flights to " + destination.toUpperCase() + " on " + date,operation,true);
		return "There are no availible seats/flights to " + destination.toUpperCase() + " on " + date;
			
	}
		
		/* Old Code
		for (int i=0;i<flightlist.size();i++)
		{
			
				try {//finds a flight
					Flight flight=new Flight();
					int newrecordid=this.getNextRecordID();
					PassengerRecord newrecord = new PassengerRecord(newrecordid, flight,firstname, lastname, address, phone, destination, date, flightclass,servername);

					if (destination.equalsIgnoreCase(flightlist.get(i).getDestination())&& format.parse(date).compareTo(flightlist.get(i).getDate())==0)
					{
						//
						flightfound=flightlist.get(i);
						newrecord.setFlight(flightfound);
						newrecord.setFlightID(flightfound.getFlightID());
						if (newrecord.getValid()==true)
						{			
							Flight chosenflight=flightlist.get(i);
							
							//if the flight is full, it jumps to the next iteration and checks another flight.
							if (flightclass.equalsIgnoreCase("Business") && chosenflight.getCurrentBusinessSeats()==chosenflight.getTotalBusinessSeats()){
								continue;
							}
							else if (flightclass.equalsIgnoreCase("First") && chosenflight.getCurrentFirstSeats()==chosenflight.getTotalFirstSeats()){
								continue;
							}
							else if (flightclass.equalsIgnoreCase("Economy") && chosenflight.getCurrentEconomySeats()==chosenflight.getTotalEconomySeats()){
								continue;
							}
							
							//otherwise tries to add passenger record to the hashmap
							char lastnamechar=newrecord.getLastNameChar();
							try{
								List<PassengerRecord> templist=map.get(lastnamechar);
								synchronized (this){
									templist.add(newrecord);
								}
								
								
								//increments flightcounter
								if (flightclass.equalsIgnoreCase("Business"))
								{
									chosenflight.setCurrentBusinessSeats(chosenflight.getCurrentBusinessSeats()+1);
								}
								else if (flightclass.equalsIgnoreCase("First"))
								{
									chosenflight.setCurrentFirstSeats(chosenflight.getCurrentFirstSeats()+1);
								}
								else if (flightclass.equalsIgnoreCase("Economy"))
								{
									chosenflight.setCurrentEconomySeats(chosenflight.getCurrentEconomySeats()+1);
								}
							}
							//in the event that the key does not exist (the first time a person with that letter for their last name comes up)
							//puts that letter into the hashmap and then creates the record
							catch (Exception e)
							{
								map.put(lastnamechar, new ArrayList<PassengerRecord>());
								
								List<PassengerRecord> templist=map.get(lastnamechar);
								synchronized (this){
									templist.add(newrecord);
								}
								
								
								//increments flightcounter
								if (flightclass.equalsIgnoreCase("Business"))
								{
									chosenflight.setCurrentBusinessSeats(chosenflight.getCurrentBusinessSeats()+1);
								}
								else if (flightclass.equalsIgnoreCase("First"))
								{
									chosenflight.setCurrentFirstSeats(chosenflight.getCurrentFirstSeats()+1);
								}
								else if (flightclass.equalsIgnoreCase("Economy"))
								{
									chosenflight.setCurrentEconomySeats(chosenflight.getCurrentEconomySeats()+1);
								}
							}
							
						
						log1.writeToManagers("Success. FlightID" +flight.getFlightID() + destination.toUpperCase() + " on " + date + "RecordID: " +newrecordid, servername);
						log1.writetoFile("Success. RecordID: " +newrecordid + " Name: " +lastname+", " +firstname + " Address: " +address +" Phone: " +phone +
								" Dest: "+ destination.toUpperCase() + " Date: " + date + "FlightClass: " + flightclass + " FlightID: " + flight.getFlightID(), operation,true);
						return "Successfully booked a flight to " + destination.toUpperCase() + " on " + date + "RecordID: " +newrecordid;	
						

					}
					log1.writeToManagers("Error creating PassengerRecord", servername);
					log1.writetoFile("Error creating PassengerRecord",operation,true);
					return "Error creating PassengerRecord";
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				log1.writeToManagers("Error. Improper Date", servername);
				log1.writetoFile("Error. Improper Date",operation,true);
				return "Error. Improper Date";
			}
			catch (Exception e){
				log1.writeToManagers(e.getMessage(), servername);
				log1.writetoFile(e.getMessage(),operation,true);
				return e.getMessage();
			}
			*/	
		
	
		
		
	
	public String getBookedFlightCount(String manageridandrecordtype) {
		
		String[] type=manageridandrecordtype.split("\\|");
		String managerid=type[0];
		String recordtype="";
		
		Logger serverlog= new Logger(servername,managerid);
		String operation="getBookedFlightCount";
		String message="";
		
		try{
			
			if (!this.isManagerID(managerid))
			{
				
				message="Error. Improper ManagerID";
				serverlog.setUnknownUser();
				serverlog.writetoFile(message, operation, true);
				return "Error. Improper ManagerID";
			}
			recordtype=type[1];
		}
		catch (Exception e){
			message="Invalid input";
			serverlog.setUnknownUser();
			serverlog.writetoFile(message, operation, true);
			return "Invalid input";
		}
		
		Logger managerlog= new Logger(managerid,managerid);
		
		
		
		int[] otherports=new int[2];
		if (port==1010)
		{
			otherports[0]=2021;
			otherports[1]=3031;
		}
		else if (port==2020)
		{
			otherports[0]=1011;
			otherports[1]=3031;
		}
		else if (port==3030)
		{
			otherports[0]=1011;
			otherports[1]=2021;
		}
		
		String finalresult="";
		DatagramSocket aSocket = null;
		try {
			finalresult = this.gettingBookedFlightCount(recordtype);
			for (int i=0;i<otherports.length;i++)
			{
				String determinefunction="bookflightcount|";
				String sentmessage=determinefunction+recordtype;
				aSocket = new DatagramSocket();    
				byte [] m =sentmessage.getBytes();
				InetAddress aHost = InetAddress.getByName("localhost");
				int serverPort = otherports[i];		                                                 
				DatagramPacket request =new DatagramPacket(m,  sentmessage.length(), aHost, serverPort);
				aSocket.send(request);			                        
				byte[] buffer = new byte[1000];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);	
				aSocket.receive(reply);
			
				finalresult+="\t" + (new String(reply.getData())).trim();
			}
			
			
		}catch (SocketException e){
			message="Socket: " + e.getMessage();
			serverlog.writetoFile(message, operation, true);
			managerlog.writetoFile(message, operation, false);
			return "Socket: " + e.getMessage();
		}
		catch (IOException e){
			message="IO: " + e.getMessage();
			serverlog.writetoFile(message, operation, true);
			managerlog.writetoFile(message, operation, false);
			return "IO: " + e.getMessage();
		}
		catch (Exception e) {
			message=e.getMessage();
			serverlog.writetoFile(message, operation, true);
			managerlog.writetoFile(message, operation, false);
			return e.getMessage();
		}
		finally {if(aSocket != null) aSocket.close();}
		
		message=finalresult;
		serverlog.writetoFile(message, operation, true);
		managerlog.writetoFile(message, operation, false);
		return finalresult;
		
		
	}
	
	public   String editFlightRecord(String recordID, String fieldname, String newValue){
	
		
		String[] ID=recordID.split("\\|");
		String[] type=fieldname.split("\\|");
		String[] values=newValue.split("\\|");
		
		
		String selectedflightID="";
		String selectedmanagerID=ID[0];
		if (ID.length==1){
			selectedflightID="000";

		}
		else{
			selectedflightID=ID[1];

		}
		

		String action=type[0];
		
		Logger serverlog= new Logger(servername,selectedmanagerID);
		String operation="editFlight";
		String message="";
		
			if (!this.isManagerID(selectedmanagerID))
			{
				
				message="Error. Improper ManagerID";
				serverlog.setUnknownUser();
				serverlog.writetoFile(message, operation, true);
				return "Error. Improper ManagerID";
			}
		
		
		Logger managerlog= new Logger(selectedmanagerID,selectedmanagerID);
		
		
		for (int i=1;i<type.length;i++)
		{
			for (int j=1;j<type.length && i!=j;j++)
			{
				if (type[i].equalsIgnoreCase(type[j])){
					message="Error. Please do not enter duplicate field types.";
					serverlog.writetoFile(message, operation, true);
					managerlog.writetoFile(message, operation, false);
					return "Error. Please do not enter duplicate field types.";
				}
			}
		}
		
		
		
		//create
		if (action.equalsIgnoreCase("create")){

			String date = "";
			int totalfirst=0,totalbusiness=0,totaleconomy=0;
			String destination="";
			
			
			//ensures no improper field types
			for (int i=1;i<type.length;i++){
				if (!type[i].equalsIgnoreCase("destination") &&!type[i].equalsIgnoreCase("date") && !type[i].equalsIgnoreCase("first") && !type[i].equalsIgnoreCase("business") && !type[i].equalsIgnoreCase("economy"))
				{
					message="Error. Improper field type.";
					serverlog.writetoFile(message, operation, true);
					managerlog.writetoFile(message, operation, false);
					return "Error. Improper field type.";
				}
			}
			
			//checks for which values to set for the flight
			for (int i=1;i<type.length;i++)
			{
				try{
					if (type[i].equalsIgnoreCase("destination"))
					{
						destination+=values[i-1];			
					}
					else if (type[i].equalsIgnoreCase("date"))
					{
						date+=values[i-1];
						
					}
					else if (type[i].equalsIgnoreCase("First"))
					{
						totalfirst=Integer.parseInt(values[i-1]);
					}
					else if (type[i].equalsIgnoreCase("Business"))
					{
						totalbusiness=Integer.parseInt(values[i-1]);
					}
					else if (type[i].equalsIgnoreCase("Economy"))
					{
						totaleconomy=Integer.parseInt(values[i-1]);
					}
				}
				
				catch (IndexOutOfBoundsException e){
					message="Error. Improper number of field values vs field types.";
					serverlog.writetoFile(message, operation, true);
					managerlog.writetoFile(message, operation, false);
					return "Error. Improper number of field values vs field types.";
				}
				catch (Exception e){
					message="Error. Improper field type.";
					serverlog.writetoFile(message, operation, true);
					managerlog.writetoFile(message, operation, false);
					return"Error. Improper field type.";
				}
			}
			
			Flight newflight;
			try {
				int givenflightid;
				
					
					givenflightid=this.getNextFlightID(); 
					newflight = new Flight(givenflightid,destination,date,totalfirst,totalbusiness,totaleconomy,servername);
					
					//adds flight to flightmap
					try {
						List<Flight> tempflightlist=flightmap.get(newflight.getDate());
						
						synchronized (this){
						tempflightlist.add(newflight);
						}
					}
					//if flight not in flightmap, puts date in flight map, then adds
					catch (Exception e){
						
						flightmap.put(newflight.getDate(), new ArrayList<Flight>());
						List<Flight> tempflightlist=flightmap.get(newflight.getDate());
						
						synchronized (this){
						tempflightlist.add(newflight);
						}
					}
					flightlist.add(newflight);
				
				
				message="Successfully created Flight " + givenflightid + " going to " +destination + " on " + date + " with (" + totalfirst +"/" + totalbusiness + "/" +totaleconomy + ") (F/B/E) seats";
				serverlog.writetoFile(message, operation, true);
				managerlog.writetoFile(message, operation, false);
				return "Successfully created Flight " + givenflightid + " going to " +destination + " on " + date + " with (" + totalfirst +"/" + totalbusiness + "/" +totaleconomy + ") (F/B/E) seats";
			} 
			catch (ParseException e) {
				message="Error. Proper date input required (MM/dd/yyyy hh:mm:ss AM)";
				serverlog.writetoFile(message, operation, true);
				managerlog.writetoFile(message, operation, false);
				return "Error. Proper date input required (MM/dd/yyyy hh:mm:ss AM)";
			}
			
			catch (Exception e){
				message=e.getMessage();
				serverlog.writetoFile(message, operation, true);
				managerlog.writetoFile(message, operation, false);
				return e.getMessage();
			}
			
				
		}
		
		//edit
		else if (action.equalsIgnoreCase("edit")){
			
			
			//checks if flightid is proper
			int selectedflightIDinteger=-1;
			try{
				selectedflightIDinteger=Integer.parseInt(selectedflightID);
				if (selectedflightIDinteger<0){
					throw new Exception();
				}
			}
			catch (Exception e){
				message="Error. Improper flightID";
				serverlog.writetoFile(message, operation, true);
				managerlog.writetoFile(message, operation, false);
				return "Error. Improper flightID";
			}


			for (Map.Entry<Date, List<Flight>> entry : flightmap.entrySet())
			{
				List <Flight> currentflightlist=entry.getValue();
				for (int i=0;i<currentflightlist.size();i++)
				{
					Flight currentflight=currentflightlist.get(i);
						if (selectedflightIDinteger==currentflight.getFlightID())
						{
							//checks if any input type is wrong, if it is then it returns error
							for (int i1=1;i1<type.length;i1++)
							{
								
								if ((!type[i1].equalsIgnoreCase("First")) && (!type[i1].equalsIgnoreCase("Business")) && (!type[i1].equalsIgnoreCase("Economy")) && (!type[i1].equalsIgnoreCase("date"))
									&& (!type[i1].equalsIgnoreCase("destination"))	){
									message="Error. Improper flight class/destination/date";
									serverlog.writetoFile(message, operation, true);
									managerlog.writetoFile(message, operation, false);
									return "Error. Improper flight class/destination/date";
								}
							}
						
						//	if all input types are correct it 
							String economy="";
							String first="";
							String business="";
							String date="";
							String dest="";
							int previousfirst=currentflight.getTotalFirstSeats();
							int previousbusiness=currentflight.getTotalBusinessSeats();
							int previouseconomy=currentflight.getTotalEconomySeats();
							String previousdate=currentflight.getStringDate();
							String previousdest=currentflight.getDestination();
							for (int i1=1;i1<type.length;i1++)
							{
								try{
									if (type[i1].equalsIgnoreCase("First")){
									
											previousfirst=currentflight.getTotalFirstSeats();
											currentflight.setTotalFirstSeats(Integer.parseInt(values[i1-1]));
										
										first+=Integer.parseInt(values[i1-1]) + " First seats\t";
									}
									else if (type[i1].equalsIgnoreCase("Business")){
										
											previousbusiness=currentflight.getTotalBusinessSeats();
											currentflight.setTotalBusinessSeats(Integer.parseInt(values[i1-1]));
										
										business+=Integer.parseInt(values[i1-1]) + " Business seats\t";
									}
									else if (type[i1].equalsIgnoreCase("Economy")){
										
											previouseconomy=currentflight.getTotalEconomySeats();
											currentflight.setTotalEconomySeats(Integer.parseInt(values[i1-1]));
										
										economy+=Integer.parseInt(values[i1-1]) + " Economy seats\t";
									}
									else if (type[i1].equalsIgnoreCase("date")){
										previousdate=currentflight.getStringDate();
				
										synchronized (this){
											currentflightlist.remove(i);
										}
										i--;
										
										currentflight.setDate(values[i1-1]);

										List<Flight> tempflightlist=flightmap.get(currentflight.getDate());
										try{
											synchronized (this) {
												tempflightlist.add(currentflight);

											}
										}
										catch (Exception e){

											flightmap.put(currentflight.getDate(), new ArrayList<Flight>());
											List<Flight> newtempflightlist=flightmap.get(currentflight.getDate());
											synchronized (this) {
												newtempflightlist.add(currentflight);
											}
										}

										date+="Date: " +values[i1-1];
										
										for (Map.Entry<Character, List<PassengerRecord>> recordentry :map.entrySet()){
											
											List<PassengerRecord> temprecordlist=recordentry.getValue();
											for (int j=0;j<temprecordlist.size();j++){
												PassengerRecord temprecord=temprecordlist.get(j);
												if (temprecord.getStringDate().equalsIgnoreCase(previousdate))
												{
													synchronized (this){
														temprecord.setDateFromString(values[i1-1]);
	
													}
												}
											}
										}
									}
									else if (type[i1].equalsIgnoreCase("destination")){
										currentflight.setDestination(values[i1-1]);
										int currentflightid=currentflight.getFlightID();
										
										dest+="Destination: " +values[i1-1];
										
										for (Map.Entry<Character, List<PassengerRecord>> recordentry :map.entrySet()){
											List<PassengerRecord> temprecordlist=recordentry.getValue();
											for (int j=0;j<temprecordlist.size();j++){
												PassengerRecord temprecord=temprecordlist.get(j);
												if (temprecord.getFlightID()==currentflightid){
													synchronized (this){
														temprecord.setDestination(values[i1-1]);
													}
												}
											}
										}
										
									}
								}
								catch (NumberFormatException e){
									
								//	resets previously updated information in the event of any errors
									try 
									{
										currentflight.setTotalFirstSeats(previousfirst);
										currentflight.setTotalBusinessSeats(previousbusiness);
										currentflight.setTotalEconomySeats(previouseconomy);
										currentflight.setDate(previousdate);
										currentflight.setDestination(previousdest);
									
										message="Error. Cannot parse value";
										serverlog.writetoFile(message, operation, true);
										managerlog.writetoFile(message, operation, false);
										return "Error. Cannot parse value";

									} catch (Exception e1) {}
								}
								catch (Exception e){
								
									//resets previously updated information in the event of any errors
									try 
									{
										currentflight.setTotalFirstSeats(previousfirst);
										currentflight.setTotalBusinessSeats(previousbusiness);
										currentflight.setTotalEconomySeats(previouseconomy);
										currentflight.setDate(previousdate);


										message=e.getMessage();
										serverlog.writetoFile(message, operation, true);
										managerlog.writetoFile(message, operation, false);
										return e.getMessage();
										
									} catch (Exception e1) {}
								}
							
							
							
							
							}
							if (first.equalsIgnoreCase("") && economy.equalsIgnoreCase("") && business.equalsIgnoreCase("") && date.equalsIgnoreCase("") && dest.equalsIgnoreCase("")){
								message="Did not edit Flight " +selectedflightID;
							}
							else {
								message="Successully edited Flight " + selectedflightID + " to have :\t" + first +business + economy+ date+dest;
							}
							serverlog.writetoFile(message, operation, true);
							managerlog.writetoFile(message, operation, false);
							return "Successully edited Flight " + selectedflightID + " to have :\t" + first +business + economy + date+dest;
						}
					
						
				}
			}
			

			message="No record of that FlightID exists.";
			serverlog.writetoFile(message, operation, true);
			managerlog.writetoFile(message, operation, false);
			return "No record of that FlightID exists.";
					

		}
		
		//delete
		if (action.equalsIgnoreCase("delete")){
			
			

			//checks if flightid is proper
			int selectedflightIDinteger=-1;
			try{
				selectedflightIDinteger=Integer.parseInt(selectedflightID);
				if (selectedflightIDinteger<0){
					throw new Exception();
				}
			}
			catch (Exception e){
				message="Error. Improper flightID";
				serverlog.writetoFile(message, operation, true);
				managerlog.writetoFile(message, operation, false);
				return "Error. Improper flightID";
			}
			
			
			boolean flightexists=false;
			
			
			for (Map.Entry<Date, List<Flight>> entry : flightmap.entrySet()){
				
				List<Flight> currentflightlist=entry.getValue();
				for (int i=0;i<currentflightlist.size();i++)//finds selected flight that needs to be deleted
				{
					Flight currentflight=currentflightlist.get(i);
						
						if (selectedflightIDinteger==currentflight.getFlightID())
						{
							synchronized (this) {
								currentflightlist.remove(i);
								flightexists=true;
							}
								//delete passenger records
								for (Map.Entry<Character, List<PassengerRecord>> entry1 : map.entrySet())
								{
									for (int i1=0;i1<entry1.getValue().size();i1++)
									{
										if (entry1.getValue().get(i1).getFlightID()==selectedflightIDinteger)
										{
											synchronized (this){
											entry1.getValue().remove(i1);
											i1--;
											}
											
										}
									}
								}
								
								break;
							
							
						}
										
				}
			}
			
			
			
			if (flightexists)
			{
				
					
					
				
				
				
				message="Flight " + selectedflightIDinteger + " has been removed from the Flight Management System.";
				serverlog.writetoFile(message, operation, true);
				managerlog.writetoFile(message, operation, false);
				return "Flight " + selectedflightIDinteger + " has been removed from the Flight Management System.";
			}

			else {
				message="Error. Flight " + selectedflightIDinteger + " did not exist.";
				serverlog.writetoFile(message, operation, true);
				managerlog.writetoFile(message, operation, false);
				return "Error. Flight " + selectedflightIDinteger + " did not exist.";

			}
			
				
		}
		message="Error. Incorrect action";
		serverlog.writetoFile(message, operation, true);
		managerlog.writetoFile(message, operation, false);
		return "Error. Incorrect action";
		
		
	}

	
	public String transferReservation(String managerandPassengerID, String currentcity, String newcity){
		String[] Split=managerandPassengerID.split("\\|");
		String managerid=Split[0];
		String PassengerID=Split[1];
		int passengeridint=Integer.parseInt(PassengerID);
		
		Logger serverlog= new Logger(servername,managerid.trim());
		Logger managerlog=new Logger(managerid,managerid.trim());
		String operation="transferReservation";
		String message="";
		
		boolean validcurrentcity=false, validnewcity=false;
		try{
			if (currentcity.equalsIgnoreCase(servername))
			{
				
				validcurrentcity=true;
			}
			else {
				throw new Exception("Invalid Current city");
			}
			
			
			if (newcity.trim().equalsIgnoreCase("MTL") || newcity.trim().equalsIgnoreCase("WST") || newcity.trim().equalsIgnoreCase("NDL"))
			{
				if (newcity.trim().equalsIgnoreCase(servername)){
					throw new Exception("New City cannot be identical to Current City");
				}
				else{
					validnewcity=true;
				}
			}
			else {
				throw new Exception ("Invalid New city");
			}
		}
		catch (Exception e){
			message=e.getMessage();
			serverlog.writetoFile(message, operation, true);
			managerlog.writetoFile(message, operation, false);
			return e.getMessage();
		}
		
		if (validcurrentcity==true && validnewcity==true){
			
			int otherport=0;
			if (newcity.equalsIgnoreCase("MTL")){
				otherport=1011;
			}
			else if (newcity.equalsIgnoreCase("WST")){
				otherport=2021;
			}
			else if (newcity.equalsIgnoreCase("NDL")){
				otherport=3031;
			}
			
			PassengerRecord chosenrecord;
			//iterates over the lastnames
			for (Map.Entry<Character, List<PassengerRecord>> entry : map.entrySet()){
							
				List<PassengerRecord> currentrecordlist=entry.getValue();
				//iterates over the records until the proper one is found
				for (int i=0;i<currentrecordlist.size();i++){
					
					PassengerRecord currentrecord=currentrecordlist.get(i);
					
					synchronized (this){
					if (passengeridint==currentrecord.getRecordID()){
						chosenrecord=currentrecord;
						String chosenfirstname, chosenlastname, chosenaddress, chosenphone, chosendestination, 
						chosendate, chosenflightclass,chosenservername,chosenmanagerid,chosenpassengerid;
						chosenfirstname=chosenrecord.getFirstName();
						chosenlastname=chosenrecord.getLastName();
						chosenaddress=chosenrecord.getAddress();
						chosenphone=chosenrecord.getPhone();
						chosendestination=chosenrecord.getDestination();
						chosendate=chosenrecord.getStringDate();
						chosenflightclass=chosenrecord.getFlightClass();
						chosenservername=servername;
						chosenmanagerid=managerid;
						chosenpassengerid=""+chosenrecord.getRecordID();
						
						
						DatagramSocket aSocket = null;
						try {
							
							
								String determinefunction="transferreservation";
								String sentmessage=determinefunction+"|"+chosenfirstname+"|"+chosenlastname+"|"+chosenaddress+"|"+chosenphone+"|"+chosendestination
										+"|"+chosendate+"|"+chosenflightclass+"|"+chosenservername+"|"+chosenmanagerid+"|"+chosenpassengerid;
								aSocket = new DatagramSocket();    
								byte [] m =sentmessage.getBytes();
								InetAddress aHost = InetAddress.getByName("localhost");
								int serverPort = otherport;	                                                 
								DatagramPacket request =new DatagramPacket(m,  sentmessage.length(), aHost, serverPort);
								aSocket.send(request);			     
								
								byte[] buffer = new byte[1000];
								DatagramPacket reply = new DatagramPacket(buffer, buffer.length);	
								aSocket.receive(reply);
								
								//obtains result from other city
								String finalresult=(new String(reply.getData())).trim();
								
								//deletes old record if true
								if (finalresult.contains("success")){
									synchronized (this){
									currentrecordlist.remove(i);
									}
								}
													
								message=finalresult;
								serverlog.writetoFile(message, operation, true);
								managerlog.writetoFile(message, operation, false);
								return finalresult;
							
			
						}
					
						catch (SocketException e){
							message="Socket: " + e.getMessage();
							serverlog.writetoFile(message, operation, true);
							managerlog.writetoFile(message, operation, false);
							return "Socket: " + e.getMessage();
						}
						catch (IOException e){
							message="IO: " + e.getMessage();
							serverlog.writetoFile(message, operation, true);
							managerlog.writetoFile(message, operation, false);
							return "IO: " + e.getMessage();
						}
						catch (Exception e) {
							message=e.getMessage();
							serverlog.writetoFile(message, operation, true);
							managerlog.writetoFile(message, operation, false);
							return e.getMessage();
						}
						finally {if(aSocket != null) aSocket.close();}
						
						
					}
				}	
				}
				
				
					
				
			}
			
			message="That Passenger ID does not exist within the " + servername +" Server records";
			serverlog.writetoFile(message, operation, true);
			managerlog.writetoFile(message, operation, false);
			return message;
			
		}
		
		message="Invalid city";
		serverlog.writetoFile(message, operation, true);
		managerlog.writetoFile(message, operation, false);
		return message;
		
		
	}
	
	public  int getNextFlightID(){
		return flightID.getAndIncrement();
	}
	
	public  int getNextRecordID(){
		return recordID.getAndIncrement();
	}
	
	public  String gettingBookedFlightCount(String recordtype) throws Exception{
		if (recordtype.equalsIgnoreCase("First") || recordtype.equalsIgnoreCase("Economy") || recordtype.equalsIgnoreCase("Business") || recordtype.equalsIgnoreCase("ALL"))
		{
			int count=0;
			
			//iterates over hashmap
			for (Map.Entry<Character, List<PassengerRecord>> entry : map.entrySet())
			{
				//if recordtype=all, then every passenger record is counted once, therefore a list of records has passengerrecords equal to its size, 
				//thus the count is equal to hashmap's summation of the list sizes
				if (recordtype.equalsIgnoreCase("ALL")){
					synchronized (this){
						count+=entry.getValue().size();
					}
				}
				//otherwise, iterates over each List to find the proper flightclass to count
				else {
					for (int i=0;i<entry.getValue().size();i++)
					{	
						PassengerRecord temprecord=entry.getValue().get(i);
						synchronized (this){
							
							String flightclasscheck=temprecord.getFlightClass();
							if (recordtype.equalsIgnoreCase(flightclasscheck))
							{
								count++;
							}	
						}
						
						
						
					}
				}
				
					
			}
			return servername + " " + count;
		}
		else
		{
			throw new Exception("Failure. Incorrect recordtype");
		}
	}
	
	public Server(String servername, int port){
		this.servername=servername;
		this.port=port;
	}
	
	public int getPort(){
		return port;
	}
	
	public boolean isManagerID(String managerid){
		if (managerid.length()==7)
		{
			if (managerid.substring(0, 3).equalsIgnoreCase("MTL") ||managerid.substring(0, 3).equalsIgnoreCase("WST") || managerid.substring(0, 3).equalsIgnoreCase("NDL"))
			{
				if (managerid.substring(0, 3).equalsIgnoreCase(servername)){
					try{
						Integer.parseInt(managerid.substring(3));
						return true;
					}
					catch (Exception e){
						
					}
				}
				
			}
		}
		return false;
	}
	
	public void exportServer() throws Exception{
		
		
	}
	
	public void UDPServer(){
		DatagramSocket aSocket = null;
		try{
	    	aSocket = new DatagramSocket(port+1);
					// create socket at agreed port
			
 			while(true){
 				byte[] buffer = new byte[1000];
 				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
  				aSocket.receive(request);
  				
  				String receivedmessage=(new String (request.getData()).trim());
  				String [] messageparts=receivedmessage.split("\\|");
  				String functionsent=messageparts[0];
  				
  				//if UDP message was from bookflightcount
  				if (functionsent.equalsIgnoreCase("bookflightcount")){
  					
  					
  					String receivedrecordtype=messageparts[1];
  	  				String sendingresult=this.gettingBookedFlightCount(receivedrecordtype);
  	  				request.setData(sendingresult.getBytes());
  	  				
  	    			DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), 
  	    				request.getAddress(), request.getPort());
  	    			aSocket.send(reply);
  				}
  				else if (functionsent.equalsIgnoreCase("transferreservation")){
  					
  					//receive passenger info from other server
  					String receivedfirstname=messageparts[1], receivedlastname=messageparts[2], receivedaddress=messageparts[3], receivedphone=messageparts[4];
  					String receiveddestination=messageparts[5], receiveddate=messageparts[6], receivedflightclass=messageparts[7],receivedservername=messageparts[8];
  					String receivedmanagerid=messageparts[9], receivedpassengerid=messageparts[10];
  					
  					Logger log1=new Logger(servername,receivedmanagerid);
  					String operation="TransferReservation";
  					
  					String pattern="MM/dd/yyyy";
  					SimpleDateFormat format= new SimpleDateFormat(pattern);
  					Flight flightfound;
  					
  					//
  					String message="";
  					boolean checkfindflight=false;
  		
  				//try with hashmapflight
  					for (Map.Entry<Date, List<Flight>> entry : flightmap.entrySet())
  					{
  						
  						try {//finds a flight
  							Flight flight=new Flight();
  							int newrecordid=this.getNextRecordID();
  							PassengerRecord newrecord = new PassengerRecord(newrecordid, flight,receivedfirstname, receivedlastname, receivedaddress, receivedphone, 
  									receiveddestination, receiveddate, receivedflightclass,servername);
  							if (format.parse(receiveddate).compareTo(entry.getKey())==0)
  							{
  								checkfindflight=true;
  								boolean checksuccess=false;
  								boolean checkfull=false;
  							
  							
  								for (int i=0;i<entry.getValue().size();i++)
  								{

  									if (entry.getValue().get(i).getDestination().equalsIgnoreCase(receiveddestination))
  									{
  										

  										flightfound=entry.getValue().get(i);
  										newrecord.setFlight(flightfound);
  										newrecord.setFlightID(flightfound.getFlightID());
  										if (newrecord.getValid()==true)
  										{		
  											Flight chosenflight=entry.getValue().get(i);

  											//if the flight is full, it jumps to the next iteration and checks another flight.
  											if (receivedflightclass.equalsIgnoreCase("Business") && chosenflight.getCurrentBusinessSeats()==chosenflight.getTotalBusinessSeats()){
  												checkfull=true;
  												continue;
  											}
  											else if (receivedflightclass.equalsIgnoreCase("First") && chosenflight.getCurrentFirstSeats()==chosenflight.getTotalFirstSeats()){
  												checkfull=true;
  												continue;
  												
  											}
  											else if (receivedflightclass.equalsIgnoreCase("Economy") && chosenflight.getCurrentEconomySeats()==chosenflight.getTotalEconomySeats()){
  												checkfull=true;
  												continue;
  											}

  											//otherwise tries to add passenger record to the hashmap
  											char lastnamechar=newrecord.getLastNameChar();
  											try{
  												List<PassengerRecord> templist=map.get(lastnamechar);
  												synchronized (this){
  													templist.add(newrecord);
  												}
  												
  												
  												//increments flightcounter
  												if (receivedflightclass.equalsIgnoreCase("Business"))
  												{
  													chosenflight.setCurrentBusinessSeats(chosenflight.getCurrentBusinessSeats()+1);
  												}
  												else if (receivedflightclass.equalsIgnoreCase("First"))
  												{
  													chosenflight.setCurrentFirstSeats(chosenflight.getCurrentFirstSeats()+1);
  												}
  												else if (receivedflightclass.equalsIgnoreCase("Economy"))
  												{
  													chosenflight.setCurrentEconomySeats(chosenflight.getCurrentEconomySeats()+1);
  												}
  											}
  											//in the event that the key does not exist (the first time a person with that letter for their last name comes up)
  											//puts that letter into the hashmap and then creates the record
  											catch (Exception e)
  											{
  												map.put(lastnamechar, new ArrayList<PassengerRecord>());
  												
  												List<PassengerRecord> templist=map.get(lastnamechar);
  												synchronized (this){
  													templist.add(newrecord);
  												}
  												
  												
  												//increments flightcounter
  												if (receivedflightclass.equalsIgnoreCase("Business"))
  												{
  													chosenflight.setCurrentBusinessSeats(chosenflight.getCurrentBusinessSeats()+1);
  												}
  												else if (receivedflightclass.equalsIgnoreCase("First"))
  												{
  													chosenflight.setCurrentFirstSeats(chosenflight.getCurrentFirstSeats()+1);
  												}
  												else if (receivedflightclass.equalsIgnoreCase("Economy"))
  												{
  													chosenflight.setCurrentEconomySeats(chosenflight.getCurrentEconomySeats()+1);
  												}
  											}

  											
  										
  										message=receivedservername + " Passenger Record ID: " + receivedpassengerid + " successfully transfered to " 
  										+ servername + " new Passenger Record: " + newrecord.getRecordID();
  										checksuccess=true;
  					  	    			break;
  										

  									}
  									}
  								}
  								//
  								
  							if (checksuccess==false){
  								message="Error creating PassengerRecord";
  	  							
  	  							if (checkfull==true){
  	  								message="Error. All potential flights are full";
  	  							}
  								
  							}
  								
  							
  						}
  						
  					} catch (ParseException e) {
  						// TODO Auto-generated catch block
 
  						 message="Error. Improper Date";
  						log1.writetoFile(message,operation,true);
  						
  						request.setData(message.getBytes());
		  	  				
		  	    			DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), 
		  	    				request.getAddress(), request.getPort());
		  	    			aSocket.send(reply);
  					}
  					catch (Exception e){
  	
  						log1.writetoFile(e.getMessage(),operation,true);

  						request.setData(e.getMessage().getBytes());
		  	  				
		  	    			DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), 
		  	    				request.getAddress(), request.getPort());
		  	    			aSocket.send(reply);
  					}
  									

  					}
  					
  					if (checkfindflight==false){
  					message="Failure. Cannot find matching Flight for Passenger ID: " + receivedpassengerid;
  					}
  					log1.writetoFile(message, operation, true);
  					
  					request.setData(message.getBytes());
  					DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), 
	  	    				request.getAddress(), request.getPort());
	  	    			aSocket.send(reply);
  					
  					
  				}
  				
  				
    		}
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e) {System.out.println("IO: " + e.getMessage());
		}catch (Exception e) {System.out.println(e.getMessage());}
		finally {if(aSocket != null) aSocket.close();}
	}

	public void setFlightlist(List<Flight> newflightlist){
		flightlist=newflightlist;
	}
	
	public void setMap(HashMap<Character,List<PassengerRecord>> newmap){
		map=newmap;
	}
	
	public void setFlightMap(HashMap<Date,List<Flight>> newflightmap){
		flightmap=newflightmap;
	}
	public static void main(String[] args) {
	
		try {
			Server mtlserver=new Server("MTL",1010);
			Server wstserver=new Server("WST",2020);
			Server ndlserver=new Server("NDL",3030);
			
			Flight mtlflight1= new Flight (mtlserver.getNextFlightID(),"NDL","12/13/2018", 10,20,30,"MTL");
			Flight mtlflight2= new Flight (mtlserver.getNextFlightID(),"WST","02/18/2018", 15,30,45,"MTL");
			Flight mtlflight3= new Flight (mtlserver.getNextFlightID(),"NDL","08/23/2018", 30,40,50,"MTL");
			
			mtlserver.flightlist.add(mtlflight1);
			mtlserver.flightlist.add(mtlflight2);
			mtlserver.flightlist.add(mtlflight3);
			
			System.out.println(mtlserver.editFlightRecord("MTL1111|1067", "create|destination|date|economy|business|first", "WST|01/02/2020|100|50|10"));
	
			System.out.println(mtlserver.bookFlight("Tam", "Vu", "12 adress", "1235143256", "WST","01/02/2020" , "First"));
			
			System.out.println(mtlserver.editFlightRecord("MTL1111|103", "edit|destination", "WST"));
			
			System.out.println(mtlserver.editFlightRecord("MTL1111|103", "delete|destination", "WST"));



			//System.out.println(mtlserver.editFlightRecord("MTL1111.100", "edit.first.business", "50.0"));
			
			//System.out.println(mtlserver.editFlightRecord("MTL1111.100", "delete", ""));
			
			//System.out.println(mtlserver.flightlist);

			
			
			String pattern="MM/dd/yyyy";
			SimpleDateFormat format= new SimpleDateFormat(pattern);
			
					
			
			//System.out.println(format.parse("12/13/2015 12:15:11 PM").compareTo(mtlserver.flightlist.get(0).getDate()));
					
			//System.out.println(mtlserver.flightlist.get(0).getFlightID());
			
		//	System.out.println(wstserver.getBookedFlightCount("WST111111.All"));
			
			System.out.println("Server is up and running!");
			
			
			
			
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
