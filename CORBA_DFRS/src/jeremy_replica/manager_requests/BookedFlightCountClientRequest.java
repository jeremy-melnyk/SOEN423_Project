package jeremy_replica.manager_requests;

import jeremy_replica.enums.FlightClass;
import jeremy_replica.global.Constants;

public class BookedFlightCountClientRequest {
	private String managerId;
	private FlightClass flightClass;
	
	public BookedFlightCountClientRequest(String managerId, FlightClass flightClass) {
		super();
		this.managerId = managerId;
		this.flightClass = flightClass;
	}
	
	public BookedFlightCountClientRequest(String bookedFlightCountRequest) {
		super();
		String tokens[] = bookedFlightCountRequest.split(Constants.DELIMITER_ESCAPE); 
		this.managerId = tokens[0].toUpperCase();
		this.flightClass = FlightClass.valueOf(tokens[1].toUpperCase());
	}

	public String getManagerId() {
		return managerId;
	}

	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}

	public FlightClass getFlightClass() {
		return flightClass;
	}

	public void setFlightClass(FlightClass flightClass) {
		this.flightClass = flightClass;
	}

	@Override
	public String toString() {
		return managerId + Constants.DELIMITER + flightClass;
	}
}
