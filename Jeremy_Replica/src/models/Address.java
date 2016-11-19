package models;

import java.io.Serializable;

public class Address implements Serializable{
	private static final long serialVersionUID = 1L;
	private String street;
	private String city;
	private String province;
	private String postalCode;
	private String country;
	private final String DELIMITER = "|";
	private final String DELIMITER_ESCAPE = "\\" + DELIMITER;

	public Address(String street, String city, String province, String postalCode, String country) {
		super();
		this.street = street;
		this.city = city;
		this.province = province;
		this.postalCode = postalCode;
		this.country = country;
	}
	
	public Address(String address){
		String[] tokens = address.split(DELIMITER_ESCAPE);
		this.street = tokens[0];
		this.city = tokens[1];
		this.province = tokens[2];
		this.postalCode = tokens[3];
		this.country = tokens[4];
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return street + DELIMITER + city + DELIMITER + province + DELIMITER + postalCode + DELIMITER + country;
	}
}
