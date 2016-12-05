package jeremy_replica.udp;

import jeremy_replica.enums.UdpRequestType;
import jeremy_replica.models.FlightReservation;

public class TransferReservationRequest extends Request {
	private static final long serialVersionUID = 1L;
	private FlightReservation flightReservation;
	
	public TransferReservationRequest(UdpRequestType requestType, FlightReservation flightReservation) {
		super(requestType);
		this.flightReservation = flightReservation;
	}
	
	public FlightReservation getFlightReservation() {
		return flightReservation;
	}
	public void setFlightReservation(FlightReservation flightReservation) {
		this.flightReservation = flightReservation;
	}
}
