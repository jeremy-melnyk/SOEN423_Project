package caio_replica.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Flight{
	private int id;
	private String departure, destination;
	private Date date;
	private int economic, business, first;
	
	@SuppressWarnings("deprecation")
	public Flight(int id, String from, String to,
			Date date, int economicSeats, int businessSeats, int firstSeats){
		this.id = id;
		this.departure = from;
		this.destination = to;
		this.date = new Date(date.toString());
		this.economic = economicSeats;
		this.business = businessSeats;
		this.first = firstSeats;
	}
	
	public void incDecEconomic(String incDec){
		if(incDec.charAt(0) == '+')
			economic += Integer.parseInt(incDec.substring(1));
		else if(incDec.charAt(0) == '-')
			economic -= Integer.parseInt(incDec.substring(1));
	}
	
	public void incDecBusiness(String incDec){
		if(incDec.charAt(0) == '+')
			business += Integer.parseInt(incDec.substring(1));
		else if(incDec.charAt(0) == '-')
			business -= Integer.parseInt(incDec.substring(1));
	}
	
	public void incDecFirst(String incDec){
		if(incDec.charAt(0) == '+')
			first += Integer.parseInt(incDec.substring(1));
		else if(incDec.charAt(0) == '-')
			first -= Integer.parseInt(incDec.substring(1));
	}
	
	public boolean hasAvailability(int flightClass){
		switch (flightClass) {
		case 1:
			return (this.economic > 0) ? true : false;
		case 2:
			return (this.business > 0) ? true : false;
		case 3:
			return (this.first > 0)? true : false;
		}
		return false;
	}
	
	public synchronized boolean reserveSeat(int flightClass){
		Boolean success = null;
		switch (flightClass) {
		case 1:
			if (this.economic > 0){
				this.economic--;
				success = true;
			}else
				success = false;
			break;
		case 2:
			if (this.business > 0){
				this.business--;
				success = true;
			}
			else
				success = false;
			break;
		case 3:
			if (this.first > 0){
				this.first--;
				success = true;
			}
			else 
				success = false;
		}
		return success;
	}
	

	public int getFlightNumber(){
		return id;
	}


	public String getDeparture() {
		return departure;
	}

	public void setDeparture(String departure) {
		this.departure = departure;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getEconomic() {
		return economic;
	}

	public void setEconomic(int economic) {
		this.economic = economic;
	}

	public int getBusiness() {
		return business;
	}

	public void setBusiness(int business) {
		this.business = business;
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}
	
	@Override
	public String toString(){
		StringBuilder s = new StringBuilder();
		s.append(id + " | " + departure + " --> " + destination + " | " + 
		new SimpleDateFormat("yyyy/MM/dd 'at' HH:mm").format(date) + " | " + 
			"Econ: " + economic + ", Bus: " + business + ", First: " + first);
		return s.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		try{
			Flight flight = (Flight) obj;
			return (flight.id == this.id) ? true : false;
		}catch(Exception e){
			return false;
		}
	}
	
	
}
