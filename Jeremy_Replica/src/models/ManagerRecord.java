package models;

import enums.City;
<<<<<<< refs/remotes/origin/master
import global.Constants;
=======
>>>>>>> Added CORBA replica implementation to Jeremy_Replica

public class ManagerRecord extends PersonRecord {
	private static final long serialVersionUID = 1L;
	private City city;

	public ManagerRecord(Integer id, String lastName, String firstName, City city) {
		super(id, lastName, firstName);
		this.city = city;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}
	
	@Override
	public String toString() {
<<<<<<< refs/remotes/origin/master
		return "ManagerRecord" + Constants.DELIMITER + id + Constants.DELIMITER + lastName + Constants.DELIMITER + firstName + Constants.DELIMITER + city.name();
=======
		return "ManagerRecord" + DELIMITER + id + DELIMITER + lastName + DELIMITER + firstName + DELIMITER + city.name();
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
	}
}
