package tam_replica.ServerInterfaceIDL;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class Flight {

	
	
	private String destination; 
	private int current_first_seats=0;
	private int current_business_seats=0;
	private int current_economy_seats=0;
	private int total_first_seats=0;
	private int total_business_seats=0;
	private int total_economy_seats=0;
	private Date date;
	private int flightID;
	private String startingdestination;
	private String stringdate;
	
	public Flight(){
		destination="";
		current_first_seats=0;
		current_business_seats=0;
		current_economy_seats=0;
		total_first_seats=0;
		total_business_seats=0;
		total_economy_seats=0;
	}
	
	public Flight(int flightID,String destination,String date, int total_first_seats, int total_business_seats, int total_economy_seats,String starting) throws ParseException, Exception{
		String pattern="MM/dd/yyyy";
		SimpleDateFormat format= new SimpleDateFormat(pattern);
		String tempdest=destination;
		
			this.date=format.parse(date);
			
			Date fixeddate=format.parse("10/10/2016");
			
			if (fixeddate.after(this.date)){
				throw new Exception ("Error. Cannot create a flight earlier than 10/10/2016 12:00:00 AM");
			}
			 if (tempdest.equalsIgnoreCase(starting)){
				throw new Exception("Error. Cannot fly to the same city as the flight begins in.");
			}
			 else if (tempdest.equalsIgnoreCase("MTL") || tempdest.equalsIgnoreCase("WST") || tempdest.equalsIgnoreCase("NDL"))
			{
				this.destination=destination;
			}
			
			else
			{
				throw new Exception("Error. Proper destination input required (MTL/WST/NDL)");
			}
			
			this.stringdate=date;
			this.total_first_seats=total_first_seats;
			this.total_business_seats=total_business_seats;
			this.total_economy_seats=total_economy_seats;
			this.flightID=flightID;
		
		
	}
	
	public int getTotalFirstSeats()
	{
		return total_first_seats;
	}
	public void setTotalFirstSeats(int newtotal) throws Exception{
		if (newtotal<current_first_seats)
		{
			throw new Exception("Error. Cannot set total First Class seat number lower than currently booked number of First Class seats");
		}
		else{
			synchronized (this){
				this.total_first_seats=newtotal;		
			}
		}
	
	}
	
	public int getTotalBusinessSeats()
	{
		return total_business_seats;
	}
	public void setTotalBusinessSeats(int newtotal) throws Exception{
		
		if (newtotal<current_business_seats)
		{
			throw new Exception("Error. Cannot set total Business Class seat number lower than currently booked number of Business Class seats");
		}
		else
		{
		synchronized (this){
			this.total_business_seats=newtotal;
		}
		}
	}
	
	public int getTotalEconomySeats()
	{
		return total_economy_seats;
	}
	public void setTotalEconomySeats(int newtotal) throws Exception{
		if (newtotal<current_economy_seats)
		{
			throw new Exception("Error. Cannot set total Economy Class seat number lower than currently booked number of Economy Class seats");
		}
		else{
			synchronized (this){
				this.total_economy_seats=newtotal;
			}
		}
	}
	
	public int getCurrentFirstSeats()
	{
		return current_first_seats;
	}
	public void setCurrentFirstSeats(int newtotal) throws Exception{
		if (newtotal>total_first_seats)
		{
			throw new Exception("Error. All total first seats are occupied");
		}
		else{
			this.current_first_seats=newtotal;	
		}
	
	}
	
	public int getCurrentBusinessSeats()
	{
		return current_business_seats;
	}
	public void setCurrentBusinessSeats(int newtotal) throws Exception{
		
		if (newtotal>total_business_seats)
		{
			throw new Exception("Error. All total business seats are occupied");
		}
		else
		{
			this.current_business_seats=newtotal;
		}
	}
	
	public int getCurrentEconomySeats()
	{
		return current_economy_seats;
	}
	public void setCurrentEconomySeats(int newtotal) throws Exception{
		if (newtotal>total_economy_seats)
		{
			throw new Exception("Error. All total economy seats are occupied");
		}
		else{
			this.current_economy_seats=newtotal;
		}
	}
	
	public String getDestination(){
		return destination;
	}
	
	public void setDestination(String destination) throws Exception{
		
		if (destination.equalsIgnoreCase("MTL") || destination.equalsIgnoreCase("WST") || destination.equalsIgnoreCase("NDL"))
		{
			this.destination=destination;
		}
		else
		{
			throw new Exception("Error. Incorrect destination : MTL/WST/NDL");
		}
	}
	
	public Date getDate(){
		return date;
	}
	
	public void setDate(String date) throws ParseException{
		String pattern="MM/dd/yyyy hh:mm:ss aa";
		SimpleDateFormat format= new SimpleDateFormat(pattern);
		synchronized (this){
			this.date=format.parse(date);
		}
	}
	public int getFlightID(){
		return flightID;
	}
	
	public String getStringDate(){
		return stringdate;
	}
	
	
}
