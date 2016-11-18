package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class FlightRecord {
	private static int idBase = 0;
	private ArrayList<Flight> records;
	public final static Object bookingLock = new Object();
	
	public FlightRecord(){
		records = new ArrayList<Flight>();
	}
	
	public Flight attemptFlightReservation(String departure, String destination, String date, int flightClass){
		for(Flight f : records){
			if(departure.equalsIgnoreCase(f.getDeparture()) && destination.equalsIgnoreCase(f.getDestination())){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				if(date.equalsIgnoreCase(sdf.format(f.getDate()))){
					synchronized (f) {
						if(f.hasAvailability(flightClass)) return f;
					}
				}
			}
		}
		throw new RuntimeException("ERR-NO AVAILABLE FLIGHTS");
	}
	
	public synchronized String add(String from, String to,
			String date, int economicSeats,
			int businessSeats, int firstSeats){
		Date d;
		try {
			d = (new SimpleDateFormat("yyyy/MM/dd")).parse(date);
			Flight f = new Flight(++idBase, from, to, d, economicSeats, businessSeats, firstSeats);
			synchronized(records){
				if(records.add(f))
					return records.get(records.size()-1).toString();
				else
					return "ERR-Flight could not be added";
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "ERR-UNKNOWN";
	}
	
	public synchronized Boolean deleteById(int flightID){
		Iterator<Flight> iter = records.iterator();
		while(iter.hasNext()) {
			Flight f = iter.next();
			if(f.getFlightNumber() == flightID){
				iter.remove();
				return true;
			}
		}
		return null;
	}
	
	public String changeFlightAttributes(int flightID, String fields[], String values[]){
		for(Flight f : records){
			if(f.getFlightNumber()==flightID){
				synchronized(f){
					for(int i = 0; i < fields.length; i++){
						if(fields[i].equalsIgnoreCase("dest")){
							f.setDestination(values[i]);
						}else if(fields[i].equalsIgnoreCase("date")){
							Date d;
							try {
								d = (new SimpleDateFormat("yyyy/MM/dd")).parse(values[i]);
							} catch (ParseException e) {
								return "ERR-INVALID DATE CHANGE";
							}
							f.setDate(d);
						}else if(fields[i].equalsIgnoreCase("econ")){
							if(values[i].contains("+")||values[i].contains("+"))
								f.incDecEconomic(values[i]);
							else
								f.setEconomic(Integer.parseInt(values[i]));
						}else if(fields[i].equalsIgnoreCase("bus")){
							if(values[i].contains("+")||values[i].contains("+"))
								f.incDecBusiness(values[i]);
							else
								f.setBusiness(Integer.parseInt(values[i]));
						}else if(fields[i].equalsIgnoreCase("first")){
							if(values[i].contains("+")||values[i].contains("+"))
								f.incDecFirst(values[i]);
							else
								f.setFirst(Integer.parseInt(values[i]));
						}else{
							return "ERR-INVALID FIELD TO EDIT";
						}
					}
				}
				return f.toString();
			}
		}
		return "ERR-FLIGHT NOT FOUND";
	}
	
	@Override
	public String toString(){
		StringBuilder s = new StringBuilder();
		for(Flight f : records){
			s.append(f.toString() + '\n');
		}
		return s.toString();
	}
	
	

}
