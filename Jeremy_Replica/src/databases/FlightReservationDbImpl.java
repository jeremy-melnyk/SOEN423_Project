package databases;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import enums.FlightClass;
import models.FlightRecord;
import models.FlightReservation;
import models.PassengerRecord;

public class FlightReservationDbImpl implements FlightReservationDb {
	private static int RECORD_ID = 0;
	private HashMap<FlightClass, HashMap<Character, HashMap<Integer, FlightReservation>>> records;
	private static Lock idLock = new ReentrantLock(true);

	public FlightReservationDbImpl() {
		super();
		this.records = new HashMap<FlightClass, HashMap<Character, HashMap<Integer, FlightReservation>>>();
		initRecords();
	}

	@Override
	public FlightReservation getFlightReservation(FlightClass flightClass, Character c, Integer id) {
		HashMap<Character, HashMap<Integer, FlightReservation>> lastNameRecords = records.get(flightClass);
		HashMap<Integer, FlightReservation> flightReservations = lastNameRecords.get(Character.toUpperCase(c));
		synchronized (flightReservations) {
			return flightReservations.get(id);
		}
	}

	@Override
	public FlightReservation getFlightReservation(Integer id) {
		for (Entry<FlightClass, HashMap<Character, HashMap<Integer, FlightReservation>>> entry : records.entrySet()) {
			HashMap<Character, HashMap<Integer, FlightReservation>> lastNameRecords = entry.getValue();
			for (Entry<Character, HashMap<Integer, FlightReservation>> lastNameEntry : lastNameRecords.entrySet()) {
				HashMap<Integer, FlightReservation> flightReservations = lastNameEntry.getValue();
				synchronized (flightReservations) {
					if (flightReservations.containsKey(id)) {
						return flightReservations.get(id);
					}
				}
			}
		}
		return null;
	}

	@Override
	public HashMap<Integer, FlightReservation> getFlightReservations(FlightClass flightClass, Character c) {
		HashMap<Character, HashMap<Integer, FlightReservation>> lastNameRecords = records.get(flightClass);
		HashMap<Integer, FlightReservation> flightReservations = lastNameRecords.get(Character.toUpperCase(c));
		return flightReservations;
	}

	@Override
	public FlightReservation removeFlightReservation(FlightClass flightClass, Character c, Integer id) {
		HashMap<Character, HashMap<Integer, FlightReservation>> lastNameRecords = records.get(flightClass);
		HashMap<Integer, FlightReservation> flightReservations = lastNameRecords.get(Character.toUpperCase(c));
		synchronized (flightReservations) {
			return flightReservations.remove(id);
		}
	}

	@Override
	public FlightReservation[] removeFlightReservations(FlightClass flightClass, int count) {
		boolean canBreak = false;
		List<FlightReservation> flightReservationsList = new ArrayList<FlightReservation>();
		HashMap<Character, HashMap<Integer, FlightReservation>> lastNameRecords = records.get(flightClass);
		for (Entry<Character, HashMap<Integer, FlightReservation>> lastNameEntry : lastNameRecords.entrySet()) {
			HashMap<Integer, FlightReservation> flightReservations = lastNameEntry.getValue();
			synchronized (flightReservations) {
				for (Entry<Integer, FlightReservation> flightReservationEntry : flightReservations.entrySet()) {
					Integer key = flightReservationEntry.getKey();
					FlightReservation flightReservation = flightReservations.remove(key);
					flightReservationsList.add(flightReservation);
					--count;
					if (count == 0) {
						canBreak = true;
					}
				}
			}
			if (canBreak) {
				break;
			}
		}
		FlightReservation[] flightReservations = new FlightReservation[flightReservationsList.size()];
		return flightReservationsList.toArray(flightReservations);
	}

	@Override
	public FlightReservation[] removeFlightReservations(int flightRecordId) {
		List<FlightReservation> flightReservationsList = new ArrayList<FlightReservation>();
		for (Entry<FlightClass, HashMap<Character, HashMap<Integer, FlightReservation>>> entry : records.entrySet()) {
			HashMap<Character, HashMap<Integer, FlightReservation>> lastNameRecords = entry.getValue();
			for (Entry<Character, HashMap<Integer, FlightReservation>> lastNameEntry : lastNameRecords.entrySet()) {
				HashMap<Integer, FlightReservation> flightReservations = lastNameEntry.getValue();
				synchronized (flightReservations) {
					Iterator<Entry<Integer, FlightReservation>> iterator = flightReservations.entrySet().iterator();
					while (iterator.hasNext()) {
						Entry<Integer, FlightReservation> flightReservationEntry = iterator.next();
						FlightReservation flightReservation = flightReservationEntry.getValue();
						FlightRecord flightRecord = flightReservation.getFlightRecord();
						if (flightRecord.getId() == flightRecordId) {
							flightReservationsList.add(flightReservation);
							iterator.remove();
						}
					}
				}
			}
		}
		FlightReservation[] flightReservations = new FlightReservation[flightReservationsList.size()];
		return flightReservationsList.toArray(flightReservations);
	}

	@Override
	public FlightReservation[] getFlightReservations() {
		List<FlightReservation> flightReservationsList = new ArrayList<FlightReservation>();
		for (Entry<FlightClass, HashMap<Character, HashMap<Integer, FlightReservation>>> entry : records.entrySet()) {
			HashMap<Character, HashMap<Integer, FlightReservation>> lastNameRecords = entry.getValue();
			for (Entry<Character, HashMap<Integer, FlightReservation>> lastNameEntry : lastNameRecords.entrySet()) {
				HashMap<Integer, FlightReservation> flightReservations = lastNameEntry.getValue();
				synchronized (flightReservations) {
					for (Entry<Integer, FlightReservation> flightReservationEntry : flightReservations.entrySet()) {
						flightReservationsList.add(flightReservationEntry.getValue());
					}
				}
			}
		}
		FlightReservation[] flightReservations = new FlightReservation[flightReservationsList.size()];
		return flightReservationsList.toArray(flightReservations);
	}

	@Override
	public FlightReservation addFlightReservation(FlightClass flightClass, PassengerRecord passengerRecord,
			FlightRecord flightRecord) {
		boolean bookingResult = flightRecord.getFlightClasses().get(flightClass).acquireSeat();
		if (!bookingResult) {
			return null;
		}
		int id = 0;
		idLock.lock();
		try {
			id = RECORD_ID++;
		} finally {
			idLock.unlock();
		}

		Character lastNameCharacter = Character.toUpperCase(passengerRecord.getLastName().charAt(0));
		FlightReservation flightReservation = new FlightReservation(id, passengerRecord, flightRecord, flightClass,
				new Date());

		HashMap<Character, HashMap<Integer, FlightReservation>> lastNameRecords = records.get(flightClass);
		HashMap<Integer, FlightReservation> flightReservations = lastNameRecords.get(lastNameCharacter);
		synchronized (flightReservations) {
			flightReservations.put(id, flightReservation);
		}
		return flightReservation;
	}

	@Override
	public FlightReservation addFlightReservation(FlightReservation flightReservation) {
		HashMap<Character, HashMap<Integer, FlightReservation>> lastNameRecords = records
				.get(flightReservation.getFlightClass());
		String lastName = flightReservation.getPassengerRecord().getLastName();
		Character lastNameCharacter = Character.toUpperCase(lastName.charAt(0));
		HashMap<Integer, FlightReservation> flightReservations = lastNameRecords.get(lastNameCharacter);
		int id = flightReservation.getId();
		synchronized (flightReservations) {
			if (!flightReservations.containsKey(id)) {
				flightReservations.put(id, flightReservation);
			}
			return flightReservations.get(id);
		}
	}

	@Override
	public int getFlightReservationCount(FlightClass flightClass) {
		int count = 0;
		HashMap<Character, HashMap<Integer, FlightReservation>> lastNameRecords = records.get(flightClass);
		for (Entry<Character, HashMap<Integer, FlightReservation>> lastNameEntry : lastNameRecords.entrySet()) {
			HashMap<Integer, FlightReservation> flightReservations = lastNameEntry.getValue();
			synchronized (flightReservations) {
				count += flightReservations.size();
			}
		}
		return count;
	}

	private void initRecords() {
		for (FlightClass flightClass : FlightClass.values()) {
			HashMap<Character, HashMap<Integer, FlightReservation>> lastNameRecords = new HashMap<Character, HashMap<Integer, FlightReservation>>();
			for (Character c = 'A'; c <= 'Z'; ++c) {
				HashMap<Integer, FlightReservation> flightReservations = new HashMap<Integer, FlightReservation>();
				lastNameRecords.put(c, flightReservations);
			}
			records.put(flightClass, lastNameRecords);
		}
	}
}
