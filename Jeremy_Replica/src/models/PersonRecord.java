package models;

import java.io.Serializable;

import global.Constants;

public abstract class PersonRecord implements Serializable {
	private static final long serialVersionUID = 1L;
	protected Integer id;
	protected String lastName;
	protected String firstName;

	public PersonRecord(Integer id, String lastName, String firstName) {
		super();
		this.id = id;
		this.lastName = lastName;
		this.firstName = firstName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Override
	public String toString() {
		return "PersonRecord" + Constants.DELIMITER + id + Constants.DELIMITER + lastName + Constants.DELIMITER + firstName;
	}
}
