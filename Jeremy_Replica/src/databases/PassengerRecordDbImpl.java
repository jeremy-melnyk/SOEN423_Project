package databases;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import models.Address;
import models.PassengerRecord;

public class PassengerRecordDbImpl implements PassengerRecordDb {
	private static int RECORD_ID = 0;
	private HashMap<Integer, PassengerRecord> records;
	private ReadWriteLock recordsLock;
	private static Lock idLock = new ReentrantLock(true);

	public PassengerRecordDbImpl() {
		super();
		this.records = new HashMap<Integer, PassengerRecord>();
		this.recordsLock = new ReentrantReadWriteLock(true);
	}

	@Override
	public PassengerRecord getPassengerRecord(Integer id) {
		recordsLock.readLock().lock();
		try {
			return records.get(id);
		} finally {
			recordsLock.readLock().unlock();
		}
	}
	
	@Override
	public PassengerRecord getPassengerRecord(String firstName, String lastName) {
		recordsLock.readLock().lock();
		try {
			for (Entry<Integer, PassengerRecord> entry : records.entrySet()){
				PassengerRecord passengerRecord = entry.getValue();
				if(passengerRecord.getFirstName().equalsIgnoreCase(firstName)
						&& passengerRecord.getLastName().equalsIgnoreCase(lastName)){
					return passengerRecord;
				}
			}
			return null;
		} finally {
			recordsLock.readLock().unlock();
		}
	}

	@Override
	public PassengerRecord[] getPassengerRecords() {
		recordsLock.readLock().lock();
		try {
			PassengerRecord[] passengerRecords = new PassengerRecord[records.size()];
			return records.values().toArray(passengerRecords);
		} finally {
			recordsLock.readLock().unlock();
		}
	}

	@Override
	public PassengerRecord removePassengerRecord(Integer id) {
		recordsLock.writeLock().lock();
		try {
			return records.remove(id);
		} finally {
			recordsLock.writeLock().unlock();
		}
	}

	@Override
	public PassengerRecord addPassengerRecord(String lastName, String firstName, Address address, String phoneNumber) {
		int id = 0;
		idLock.lock();
		try {
			id = RECORD_ID++;
		} finally {
			idLock.unlock();
		}

		PassengerRecord record = new PassengerRecord(id, lastName, firstName, address, phoneNumber);
		recordsLock.writeLock().lock();
		try {
			if (!records.containsKey(id)){
				records.put(id, record);	
			}
		} finally {
			recordsLock.writeLock().unlock();
		}
		return record;
	}

	@Override
	public PassengerRecord addPassengerRecord(PassengerRecord passengerRecord) {
		recordsLock.writeLock().lock();
		try {
			int id = passengerRecord.getId();
			if (!records.containsKey(id)){
				records.put(id, passengerRecord);
			}
			return records.get(id);
		} finally {
			recordsLock.writeLock().unlock();
		}
	}
}
