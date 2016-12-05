package ServerInterfaceIDL;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import org.omg.CORBA.ORB;

public class Client {

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
			
			
			

			
			System.out.println("Welcome to Patrick's Flight Management System.");
			
			
			Scanner keyboard = new Scanner(System.in);
			
			System.out.println("Press 1 to login and 0 to exit.");
			while (true)
			{
				String usercity;
				int userint=9;
				try{
					 userint=keyboard.nextInt();
				}
				catch (Exception e)
				{
					
				}
				keyboard.nextLine();
				if (userint==1)
				{
					System.out.println("Please enter your ID");
					String userid=keyboard.nextLine().trim();
					usercity= userid.substring(0, 3).toUpperCase();
					
					boolean ismanager=false;
					
					//determining if userid is a valid manager
					if (userid.length()==7 )
					{
						try{
							Integer.parseInt(userid.substring(3));
							
							if (usercity.equalsIgnoreCase("MTL") || usercity.equalsIgnoreCase("NDL") || usercity.equalsIgnoreCase("WST"))
							{
								ismanager=true;//only true if user input is of length 7, first 3 character is the proper city name and last 4 character is an Integer
							}
						}
						catch (Exception e){
							
						}
					}
					
					//displaying manager or passenger status to the user
					if (ismanager==true){
						System.out.println("You are logged in as Manager " + userid.substring(3) + " of the " + usercity + " server." );
					}
					else
					{
						System.out.println("You are logged in as a passenger.");
						userid="passenger";
					}
					
					
					boolean loggedin=true;		
					while(loggedin)
					{
						//Passenger selecting a city
						if (userid.equalsIgnoreCase("passenger")){
							System.out.println("Please select a city (MTL/WST/NDL) or type \"exit\" to log out.");
							
							boolean check=true;
							
							//ensuring proper city input
							while (check)
							{
								String userinput=keyboard.nextLine();
								if (userinput.equalsIgnoreCase("MTL") || userinput.equalsIgnoreCase("WST") || userinput.equalsIgnoreCase("NDL")){
									usercity=userinput;
									
									
									
									System.out.println("Selected city is :\t" + usercity.toUpperCase());
									System.out.println("Type \"book\" to book a flight or \"back\" to go back");
									
									boolean check2=true;
									while (check2){
										
										String userinput2=keyboard.nextLine();
										if (userinput2.equalsIgnoreCase("book"))
										{
											System.out.println("Please enter your information in the following format: Lastname,Firstname,Address,Phone ");
											System.out.println("Example: Smith,John,12 Sherbrooke,4121234567");
											String lastname="",firstname="",address="",phone="",destination="",date="",flightclass="";
											boolean check3=true;
											
											while (check3==true)
											{
												
												try{
													String userinput3=keyboard.nextLine();
													String[] parts=userinput3.split(",");
													lastname=parts[0];
													firstname=parts[1];
													address=parts[2];
													phone=parts[3];	
													
													System.out.println("Please enter your desired destination (MTL/WST/NDL), date (MM/DD/YYYY) and flight class (First/Business/Economy) in the same format");
													System.out.println("Example: MTL,12/22/201,First");
													userinput3=keyboard.nextLine();
													String[] parts2=userinput3.split(",");
													destination=parts2[0];
													date=parts2[1];
													flightclass=parts2[2];
													
													if (usercity.equalsIgnoreCase("MTL")){
														System.out.println(mtlserver.bookFlight(firstname, lastname, address, phone, destination, date, flightclass));
													}
													if (usercity.equalsIgnoreCase("WST")){
														System.out.println(wstserver.bookFlight(firstname, lastname, address, phone, destination, date, flightclass));
													}
													if (usercity.equalsIgnoreCase("NDL")){
														System.out.println(ndlserver.bookFlight(firstname, lastname, address, phone, destination, date, flightclass));
													}
													check3=false;
													System.out.println("Selected city is :\t" + usercity.toUpperCase());
													System.out.println("Type \"book\" to book a flight or \"back\" to go back");
												}
												catch (Exception e)
												{
													System.out.println("Error. Improper format");
												}
												
												
												
											}
											
											
										}
										else if (userinput2.equalsIgnoreCase("back")){
											check2=false;
											System.out.println("Please select a city (MTL/WST/NDL) or type \"exit\" to log out.");
											
										}
									}
								}
								else if (userinput.equalsIgnoreCase("exit")){
									check=false;
									loggedin=false;
									System.out.println("Logging out of passenger");
									System.out.println("Press 1 to login and 0 to exit.");
								}
							}
							
							
							
							
						}
						
						else{
						
							System.out.println("Type \"count\" to get the number of flights booked");
							System.out.println("Type \"edit\" to create/modify/delete flights");
							System.out.println("Type \"transfer\" to transfer passengers records to a different city");
							System.out.println("Type \"exit\" to log out");
							
							boolean check2=true;
							
							while (check2==true)
							{
								String userinput=keyboard.nextLine().trim();
								
								if (userinput.equalsIgnoreCase("count")){
									System.out.println("Please enter the type of flight class you wish to get a count of (First,Economy,Business,ALL)");
									System.out.println("Type \"back\" to go back");
									
									boolean check3=true;
									while (check3)
									{
										String userinputflightclass=keyboard.nextLine().trim();
										
										
										if (userinputflightclass.equalsIgnoreCase("First") || userinputflightclass.equalsIgnoreCase("Business") || userinputflightclass.equalsIgnoreCase("Economy") || userinputflightclass.equalsIgnoreCase("ALL")){
											
											if (usercity.equalsIgnoreCase("MTL")){
												System.out.println(mtlserver.getBookedFlightCount(userid+"|"+userinputflightclass));
											}
											else if (usercity.equalsIgnoreCase("WST")){
												System.out.println(wstserver.getBookedFlightCount(userid+"|"+userinputflightclass));

											}
											else if (usercity.equalsIgnoreCase("NDL")){
												System.out.println(ndlserver.getBookedFlightCount(userid+"|"+userinputflightclass));

											}
											check3=false;
											System.out.println("Type \"count\" to get the number of flights booked");
											System.out.println("Type \"edit\" to create/modify/delete flights");
											System.out.println("Type \"transfer\" to transfer passengers records to a different city");
											System.out.println("Type \"exit\" to log out");
										}
										else if (userinputflightclass.equalsIgnoreCase("back")){
											check3=false;
											System.out.println("Type \"count\" to get the number of flights booked");
											System.out.println("Type \"edit\" to create/modify/delete flights");
											System.out.println("Type \"transfer\" to transfer passengers records to a different city");
											System.out.println("Type \"exit\" to log out");
										}
										
									}
								}
								else if (userinput.equalsIgnoreCase("edit")){
									System.out.println("Please type the action you wish to do (create/edit/delete)");
									String action=keyboard.nextLine().trim();
									String flightID="5";
									if (!action.equalsIgnoreCase("create")){
										System.out.println("Please enter the the FlightID");
										flightID=keyboard.nextLine().trim();
										if (flightID.isEmpty()){
											flightID="5";
										}
									}
									
									
									if (action.equalsIgnoreCase("create")){
										
										System.out.println("Please enter the flights values in the following format:");
										System.out.println("destination|date|economy|business|first");
										System.out.println("Example : MTL|01/03/2018|10|20|30");
									}
									else if (action.equalsIgnoreCase("edit")){
										System.out.println("Use the keywords: first/economy/business/date/destination  to edit the desired number of seats or date");
										System.out.println("Please enter the new values in the following format:");
										System.out.println("Example : first , 10");
										System.out.println("Example : date , 01/03/2018");
										System.out.println("Example : destination , MTL");


									}
									
									
									String userinputfield="";
									String userinputvalue="";
									
									if (action.equalsIgnoreCase("create")){
										String userinputeditflight=keyboard.nextLine();
										 userinputfield="dest|date|economy|business|first";
										 userinputvalue=userinputeditflight;
										}
									
									
									if (action.equalsIgnoreCase("edit") ){
										String userinputeditflight=keyboard.nextLine();
										if (userinputeditflight.isEmpty()){
											userinputeditflight="invalid,100";
										}
										String[] split=userinputeditflight.split(",");
										
										try{
											 userinputfield=split[0].trim();
											 userinputvalue=split[1].trim();
											 
											 
										}
										catch (Exception e){
											
										}
									}
									
									
									
									if (action.equalsIgnoreCase("delete")){
										userinputfield="nothing";
										userinputvalue="100";
									}
									
									
									
									
									
									String firstfield=userid +"|" + flightID;
									String secondfield=action+"|"+userinputfield;
									String thirdfield=userinputvalue;
									
									if (usercity.equalsIgnoreCase("MTL")){
										System.out.println(mtlserver.editFlightRecord(firstfield, secondfield, thirdfield));
									}
									else if (usercity.equalsIgnoreCase("WST")){
										System.out.println(wstserver.editFlightRecord(firstfield, secondfield, thirdfield));

									}
									else if (usercity.equalsIgnoreCase("NDL")){
										System.out.println(ndlserver.editFlightRecord(firstfield, secondfield, thirdfield));
									}
									
									System.out.println("Type \"count\" to get the number of flights booked");
									System.out.println("Type \"edit\" to create/modify/delete flights");
									System.out.println("Type \"transfer\" to transfer passengers records to a different city");
									System.out.println("Type \"exit\" to log out");
									
									
								}
								else if (userinput.equalsIgnoreCase("transfer")){
									System.out.println("Please enter a record ID that you wish to transfer (must be a valid integer): ");
									boolean fakeinteger=true;
									int recordID=0;
									String recordIDstring="";
									//ensures valid integer
									while (fakeinteger){
										 recordIDstring=keyboard.nextLine().trim();
										
										try {
											recordID=Integer.parseInt(recordIDstring);
											fakeinteger=false;
										}
										catch (Exception e){
											//does nothing, but the while loop will continue until a valid is entered
										}
									}
									System.out.println("Please enter the city you wish to transfer the record to (MTL/WST/NDL, cannot be same city as origin):");
									boolean fakecity=true;
									//ensures valid city
									String othercity="";
									while (fakecity){
										String tempothercity=keyboard.nextLine().trim();
										if (tempothercity.equalsIgnoreCase("MTL") ||tempothercity.equalsIgnoreCase("WST") ||tempothercity.equalsIgnoreCase("NDL") ){
											
											if (!(tempothercity.equalsIgnoreCase(userid.substring(0, 3)))){
												othercity=tempothercity;
												fakecity=false;
											}
										}
									}
									if (usercity.equalsIgnoreCase("MTL")){
										System.out.println(mtlserver.transferReservation(userid+"|" +recordIDstring, "MTL", othercity));
									}
									else if (usercity.equalsIgnoreCase("WST")){
										System.out.println(wstserver.transferReservation(userid+"|" +recordIDstring, "WST", othercity));

									}
									else if (usercity.equalsIgnoreCase("NDL")){
										System.out.println(ndlserver.transferReservation(userid+"|" +recordIDstring, "NDL", othercity));
										System.out.println(userid+"|" +recordIDstring+ "NDL"+ othercity);
									}
									
									System.out.println("Type \"count\" to get the number of flights booked");
									System.out.println("Type \"edit\" to create/modify/delete flights");
									System.out.println("Type \"transfer\" to transfer passengers records to a different city");
									System.out.println("Type \"exit\" to log out");
									
								}
								else if (userinput.equalsIgnoreCase("exit")){
									check2=false;
									loggedin=false;
									System.out.println("Logging out of " + userid);
									System.out.println("Press 1 to login and 0 to exit.");
								}
							}
						}
						
						
						
					}
					
				}
				if (userint==0)
				{
					keyboard.close();
					System.out.println("Exiting Flight Management System.");
					System.exit(0);
				}
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
	}

}

