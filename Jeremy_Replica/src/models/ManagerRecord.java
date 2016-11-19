package models;

import enums.City;

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
		return "ManagerRecord" + DELIMITER + id + DELIMITER + lastName + DELIMITER + firstName + DELIMITER + city.name();
	}
}
