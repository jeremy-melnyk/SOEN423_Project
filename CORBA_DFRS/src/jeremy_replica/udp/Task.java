package jeremy_replica.udp;

import java.util.concurrent.Callable;

import jeremy_replica.models.FlightServerAddress;

public abstract class Task<T> implements Callable<T> {
	protected final int BUFFER_SIZE = 1000;
	protected FlightServerAddress flightServerAddress;

	public Task(FlightServerAddress flightServerAddress) {
		super();
		this.flightServerAddress = flightServerAddress;
	}

	public FlightServerAddress getFlightServerAddress() {
		return flightServerAddress;
	}

	public void setFlightServerAddress(FlightServerAddress flightServerAddress) {
		this.flightServerAddress = flightServerAddress;
	}
}
