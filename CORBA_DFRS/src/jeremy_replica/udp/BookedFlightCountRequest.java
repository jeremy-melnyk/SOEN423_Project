package jeremy_replica.udp;

import jeremy_replica.enums.FlightClass;
import jeremy_replica.enums.UdpRequestType;

public class BookedFlightCountRequest extends Request {
	private static final long serialVersionUID = 1L;
	private FlightClass flightClass;

	public BookedFlightCountRequest(UdpRequestType requestType, FlightClass flightClass) {
		super(requestType);
		this.setFlightClass(flightClass);
	}

	public FlightClass getFlightClass() {
		return flightClass;
	}

	public void setFlightClass(FlightClass flightClass) {
		this.flightClass = flightClass;
	}
}
