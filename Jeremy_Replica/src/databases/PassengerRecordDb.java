package databases;

import models.Address;
import models.PassengerRecord;

public interface PassengerRecordDb {
	public PassengerRecord getPassengerRecord(Integer id);
	public PassengerRecord getPassengerRecord(String firstName, String lastName);
	public PassengerRecord[] getPassengerRecords();
	public PassengerRecord removePassengerRecord(Integer id);
	public PassengerRecord addPassengerRecord(String lastName, String firstName, Address address, String phoneNumber);
	public PassengerRecord addPassengerRecord(PassengerRecord passengerRecord);
}
