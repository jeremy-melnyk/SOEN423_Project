package manager_requests;

import enums.FlightClass;
<<<<<<< refs/remotes/origin/master
import global.Constants;

public class BookedFlightCountClientRequest {
=======

public class BookedFlightCountClientRequest {
	private final String DELIMITER = "|";
	private final String DELIMITER_ESCAPE = "\\" + DELIMITER;
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
	private String managerId;
	private FlightClass flightClass;
	
	public BookedFlightCountClientRequest(String managerId, FlightClass flightClass) {
		super();
		this.managerId = managerId;
		this.flightClass = flightClass;
	}
	
	public BookedFlightCountClientRequest(String bookedFlightCountRequest) {
		super();
<<<<<<< refs/remotes/origin/master
		String tokens[] = bookedFlightCountRequest.split(Constants.DELIMITER_ESCAPE); 
=======
		String tokens[] = bookedFlightCountRequest.split(DELIMITER_ESCAPE); 
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
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
<<<<<<< refs/remotes/origin/master
		return managerId + Constants.DELIMITER + flightClass;
=======
		return managerId + DELIMITER + flightClass;
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
	}
}
