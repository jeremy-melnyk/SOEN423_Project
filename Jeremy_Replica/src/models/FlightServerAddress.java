package models;

import java.io.Serializable;

import enums.City;

public class FlightServerAddress implements Serializable {
	private static final long serialVersionUID = 1L;
	private City city;
	private int port;
	private String host;
	
	public FlightServerAddress(City city, int port, String host) {
		super();
		this.city = city;
		this.port = port;
		this.host = host;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
}
