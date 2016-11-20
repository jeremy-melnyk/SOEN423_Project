package models;

public class PassengerRecord extends PersonRecord {
	private static final long serialVersionUID = 1L;
	private Address address;
	private String phoneNumber;

	public PassengerRecord(Integer id, String lastName, String firstName, Address address, String phoneNumber) {
		super(id, lastName, firstName);
		this.address = address;
		this.phoneNumber = phoneNumber;
	}
	
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	@Override
	public String toString() {
		return "PassengerRecord" + DELIMITER + id + DELIMITER + lastName + DELIMITER + firstName;
	}
}
