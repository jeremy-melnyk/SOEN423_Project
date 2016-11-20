package manager_requests;

import enums.FlightClass;

public class BookedFlightCountClientRequest {
	private final String DELIMITER = "|";
	private final String DELIMITER_ESCAPE = "\\" + DELIMITER;
	private String managerId;
	private FlightClass flightClass;
	
	public BookedFlightCountClientRequest(String managerId, FlightClass flightClass) {
		super();
		this.managerId = managerId;
		this.flightClass = flightClass;
	}
	
	public BookedFlightCountClientRequest(String bookedFlightCountRequest) {
		super();
		String tokens[] = bookedFlightCountRequest.split(DELIMITER_ESCAPE); 
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
		return managerId + DELIMITER + flightClass;
	}
}
