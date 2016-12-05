package caio_replica.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

public class PassengerRecord {
	private ItineraryEntry hashMap[];
	private int size;
	
	public PassengerRecord(){
		hashMap = new ItineraryEntry[26];
		size = 0;
	}
	
	public void put(char key, Itinerary value){
		int keyLocation = getKeyLocation(key);
		if(hashMap[keyLocation] == null){
			hashMap[keyLocation] = new ItineraryEntry(key);
		}
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				hashMap[keyLocation].addValueToList(value);
				size++;
			}
		});
		t.start();
	}
	
	public boolean deleteRecord(Itinerary it){
		return hashMap[getKeyLocation(it.getLastName().charAt(0))].value.remove(it);
	}
	
	public void deleteFlightBookings(int flightNumber){
		ArrayList<Thread> threads = new ArrayList<Thread>(size);
		for(ItineraryEntry i : hashMap){
			if(i != null){
				threads.add(new Thread(new RunnableItineraryDeleter(i, flightNumber)));
				threads.get(threads.size()-1).start();
			}
		}
		for(Thread t : threads){
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String changeBookingAttributesByFlight(int flightID, String fields[], String values[]){
		ArrayList<Itinerary> records = this.getValues();
		String changedReservations = "";
		for(Itinerary i : records){
			if(i.getFlightID()==flightID){
				synchronized(i){
					for(int count = 0; count < fields.length; count++){
						if(fields[count].equalsIgnoreCase("dest")){
							i.setDestination(values[count]);
						}else if(fields[count].equalsIgnoreCase("date")){
							Date d;
							try {
								d = (new SimpleDateFormat("yyyy/MM/dd")).parse(values[count]);
							} catch (ParseException e) {
								return "ERR-INVALID DATE CHANGE";
							}
							i.setDate(d);
						}
					}
				}
				changedReservations += i.toString() +"\n";
			}
		}
		if(changedReservations.isEmpty())
			System.out.println("No mods");
		return changedReservations;
	}
	
	private class RunnableItineraryDeleter implements Runnable{
		private ItineraryEntry i;
		private int flightNumber;
		@Override
		public void run() {
			i.deleteByFlightNumber(flightNumber);
		}
		
		public RunnableItineraryDeleter(ItineraryEntry i, int flightNumber){
			this.i = i;
			this.flightNumber = flightNumber;
		}
		
	}
	
	private int getKeyLocation(Character c){
		int asciiBase = 96;
		return ((int) Character.toLowerCase(c)) - asciiBase - 1;
	}
	
	public synchronized Integer getRecordByClass(int flightClass){
		int counter = 0;
		ArrayList<Itinerary> values = this.getValues();
		if(flightClass == 4){
			return values.size();
		}else{
			for(Itinerary i : values){
				if(i.getFlightClass() == flightClass)
					counter++;
			}
			return counter;
		}
	}
	
	public Itinerary getRecordByPassengerID(int passengerID){
		ArrayList<Itinerary> values = this.getValues();
		for(Itinerary val : values){
			if(val.getRecordID() == passengerID)
				synchronized(val){return val;};
		}
		return null;
	}
	
	public ArrayList<Itinerary> getValues(){
		ArrayList<Itinerary> values= new ArrayList<Itinerary>(size);
		ArrayList<RunnableItineraryGetter> threads = new ArrayList<RunnableItineraryGetter>(hashMap.length);
		for(int i = 0; i < hashMap.length; i++){
			if(hashMap[i] != null){
				threads.add(new RunnableItineraryGetter(values, hashMap[i].getValue()));
				threads.get(threads.size()-1).start();
			}
		}
		for(RunnableItineraryGetter r : threads) {
			try {
				r.t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return values;
	}
	
	private static class RunnableItineraryGetter implements Runnable{
		Thread t;
		LinkedList<Itinerary> list;
		ArrayList<Itinerary> values;
		
		public RunnableItineraryGetter(ArrayList<Itinerary> i, LinkedList<Itinerary> l) {
			this.values = i;
			list = l;
		}
		
		@Override
		public void run() {
			for(Itinerary value : list){
				values.add(value);
			}
		}
		
		public void start(){
			t = new Thread(this);
			t.start();
		}	
	}
	
	@SuppressWarnings("unused")
	private class ItineraryEntry{
		private char key;
		private LinkedList<Itinerary> value;
		
		public ItineraryEntry(char k){
			key = k;
			value = new LinkedList<Itinerary>();
		}
		
		public synchronized void addValueToList(Itinerary i){
				value.add(new Itinerary(i));
		}
		
		
		public char getKey() {
			return key;
		}
		public void setKey(char key) {
			this.key = key;
		}
		public LinkedList<Itinerary> getValue() {
			return value;
		}
		public void setValue(LinkedList<Itinerary> value) {
			this.value = value;
		}
		
		public synchronized void deleteByFlightNumber(int flightNumber){
			Iterator<Itinerary> l = value.iterator();
			while(l.hasNext()){
				Itinerary i = l.next();
				if(i.getFlightID()==flightNumber){
					l.remove();
				}
			}
		}
		
		
	}

}
