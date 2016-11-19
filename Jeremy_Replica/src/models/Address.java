package models;

import java.io.Serializable;

<<<<<<< refs/remotes/origin/master
import global.Constants;

public class Address implements Serializable {
=======
public class Address implements Serializable{
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
	private static final long serialVersionUID = 1L;
	private String street;
	private String city;
	private String province;
	private String postalCode;
	private String country;
<<<<<<< refs/remotes/origin/master
=======
	private final String DELIMITER = "|";
	private final String DELIMITER_ESCAPE = "\\" + DELIMITER;
>>>>>>> Added CORBA replica implementation to Jeremy_Replica

	public Address(String street, String city, String province, String postalCode, String country) {
		super();
		this.street = street;
		this.city = city;
		this.province = province;
		this.postalCode = postalCode;
		this.country = country;
	}
<<<<<<< refs/remotes/origin/master

	public Address(String address) {
		String[] tokens = address.split(Constants.DELIMITER_ESCAPE);

		// Single string address
		if (tokens.length < 2) {
			this.street = tokens[0];
			this.city = "";
			this.province = "";
			this.postalCode = "";
			this.country = "";
		} else {
			this.street = tokens[0];
			this.city = tokens[1];
			this.province = tokens[2];
			this.postalCode = tokens[3];
			this.country = tokens[4];
		}
=======
	
	public Address(String address){
		String[] tokens = address.split(DELIMITER_ESCAPE);
		this.street = tokens[0];
		this.city = tokens[1];
		this.province = tokens[2];
		this.postalCode = tokens[3];
		this.country = tokens[4];
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
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
<<<<<<< refs/remotes/origin/master
		return street + Constants.DELIMITER + city + Constants.DELIMITER + province + Constants.DELIMITER + postalCode
				+ Constants.DELIMITER + country;
=======
		return street + DELIMITER + city + DELIMITER + province + DELIMITER + postalCode + DELIMITER + country;
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
	}
}
