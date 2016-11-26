package manager_requests;

import global.Constants;

public class TransferReservationClientRequest {
	private String managerId;
	private int flightReservationId;

	public TransferReservationClientRequest(String transferReservationRequest) {
		super();
		String tokens[] = transferReservationRequest.split(Constants.DELIMITER_ESCAPE);
		this.managerId = tokens[0].toUpperCase();
		this.flightReservationId = Integer.parseInt(tokens[1]);
	}

	public String getManagerId() {
		return managerId;
	}

	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}

	public int getFlightReservationId() {
		return flightReservationId;
	}

	public void setFlightReservationId(int flightReservationId) {
		this.flightReservationId = flightReservationId;
	}

	@Override
	public String toString() {
		return managerId + Constants.DELIMITER + flightReservationId;
	}
}
