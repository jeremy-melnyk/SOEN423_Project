package ServerInterfaceIDL;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class PassengerRecord {

	private String firstName;
	private String lastName;
	private String address;
	private String phone;
	private String destination;
	private Date date;
	private String stringdate;
	private String flightclass;
	private int flightID=0;
	private int recordID=0;
	private boolean valid=false;
	Flight flight;
	private String startingdestination;

	public PassengerRecord(){
		
	}
	
	public PassengerRecord(int recordID, Flight flight,String firstname, String lastname, String address, String phone, String destination, String date, String flightclass, String startingdestination) throws Exception
	{
		stringdate=date;
		//ensuring Date input is correct
		String pattern="MM/dd/yyyy";
		SimpleDateFormat format= new SimpleDateFormat(pattern);
		String tempdest=destination;
		String tempflightclass=flightclass.trim();
			this.date=format.parse(date);
			
			Date fixeddate=format.parse("10/10/1016");
			
			if (fixeddate.after(this.date)){
				throw new Exception ("Error. Cannot book a flight earlier than 10/10/1016");
			}
			

			if (tempdest.equalsIgnoreCase("MTL") || tempdest.equalsIgnoreCase("WST") || tempdest.equalsIgnoreCase("NDL"))
			{
				this.destination=destination;
			}
			else if (tempdest.equalsIgnoreCase(startingdestination)){
				throw new Exception("Error. Cannot book a flight to the starting destination.");
			}
			else
			{
				throw new Exception("Error. Incorrect destination : MTL/WST/NDL");
			}
			
			if (tempflightclass.equalsIgnoreCase("First") ||tempflightclass.equalsIgnoreCase("Business") || tempflightclass.equalsIgnoreCase("Economy"))
			{
				this.flightclass=flightclass;
			}
			else 
			{
				throw new Exception("Error. Incorrect flight class : First/Business/Economy");
			}
			
			this.phone=phone.trim();
			this.firstName=firstname.trim();
			this.lastName=lastname.trim();
			
			
			this.address=address.trim();
			
			this.valid=true;
			this.recordID=recordID;
			this.flight=flight;
			this.flightID=this.flight.getFlightID();

	}

	public String getFirstName(){
		return firstName;
	}

	public String getLastName(){
		return lastName;
	}
	public char getLastNameChar(){
	return lastName.charAt(0);
	}

	public String getAddress(){
		return address;
	}

	public String getPhone(){
		return phone;
	}

	public String getDestination(){
		return destination;
	}
	public void setDestination(String destination){
		this.destination=destination;
	}
	public Date getDate(){
		return date;
	}
	public void setDate(Date date){
		this.date=date;
	}
	public String getFlightClass(){
		return flightclass;
	}
	public void setFlightClass(String flightclass) {
		this.flightclass=flightclass;
	}
	
	public boolean getValid(){
		return valid;
	}
	
	public int getFlightID(){
		return flightID;
	}
	public void setFlightID(int flightID){
		this.flightID=flightID;
	}
	public int getRecordID(){
		return recordID;
	}
	public void setRecordID(int newid){
		this.recordID=newid;
	}
	public void setFlight(Flight flight){
		this.flight=flight;
	}
	public String getStringDate(){
		return stringdate;
	}
	public void setStringDate(String newdate){
		this.stringdate=newdate;
	}
	public void setDateFromString(String newdate) throws Exception{
		
		this.setStringDate(newdate);
		String pattern="MM/dd/yyyy hh:mm:ss aa";
		SimpleDateFormat format= new SimpleDateFormat(pattern);
		try {
			this.date=format.parse(newdate);
		} catch (ParseException e) {
			throw new Exception ("Invalid String for Date : " + newdate);
		}
	}
}



/*


hashmaps=(key, value)
key=unique char ex: "A"
value=List

List<PassengerRecord>

*/