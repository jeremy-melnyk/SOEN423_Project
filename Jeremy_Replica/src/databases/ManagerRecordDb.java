package databases;

import enums.City;
import models.ManagerRecord;

public interface ManagerRecordDb {
	public ManagerRecord getManagerRecord(Integer id);
	public ManagerRecord[] getManagerRecords();
	public ManagerRecord removeManagerRecord(Integer id);
	public ManagerRecord addManagerRecord(String lastName, String firstName, City city);
	public ManagerRecord addManagerRecord(ManagerRecord managerRecord);
}
