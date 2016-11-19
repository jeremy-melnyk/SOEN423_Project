package databases;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import enums.City;
import models.ManagerRecord;

public class ManagerRecordDbImpl implements ManagerRecordDb {
	private static int RECORD_ID = 0;
	private HashMap<Integer, ManagerRecord> records;
	private ReadWriteLock recordsLock;
	private static Lock idLock = new ReentrantLock(true);
	
	public ManagerRecordDbImpl() {
		super();
		this.records = new HashMap<Integer, ManagerRecord>();
		this.recordsLock = new ReentrantReadWriteLock(true);
	}

	@Override
	public ManagerRecord getManagerRecord(Integer id) {
		recordsLock.readLock().lock();
		try {
			return records.get(id);
		} finally {
			recordsLock.readLock().unlock();
		}
	}

	@Override
	public ManagerRecord[] getManagerRecords() {
		recordsLock.readLock().lock();
		try {
			ManagerRecord[] ManagerRecords = new ManagerRecord[records.size()];
			return records.values().toArray(ManagerRecords);
		} finally {
			recordsLock.readLock().unlock();
		}
	}

	@Override
	public ManagerRecord removeManagerRecord(Integer id) {
		recordsLock.writeLock().lock();
		try {
			return records.remove(id);
		} finally {
			recordsLock.writeLock().unlock();
		}
	}

	@Override
	public ManagerRecord addManagerRecord(String lastName, String firstName, City city) {
		int id = 0;
		idLock.lock();
		try {
			id = RECORD_ID++;
		} finally {
			idLock.unlock();
		}
		
		ManagerRecord record = new ManagerRecord(id, lastName, firstName, city);
		recordsLock.writeLock().lock();
		try {
			records.put(id, record);
		} finally {
			recordsLock.writeLock().unlock();
		}
		return record;
	}

	@Override
	public ManagerRecord addManagerRecord(ManagerRecord managerRecord) {
		recordsLock.writeLock().lock();
		try {
			int id = managerRecord.getId();
			if (!records.containsKey(id)){
				records.put(id, managerRecord);
			}
			return records.get(id);
		} finally {
			recordsLock.writeLock().unlock();
		}
	}
}
