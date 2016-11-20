package manager_requests;

public class TransferReservationClientRequest {
	private final String DELIMITER = "|";
	private final String DELIMITER_ESCAPE = "\\" + DELIMITER;
	private String managerId;
	private int flightReservationId;

	public TransferReservationClientRequest(String transferReservationRequest) {
		super();
		String tokens[] = transferReservationRequest.split(DELIMITER_ESCAPE);
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
		return managerId + DELIMITER + flightReservationId;
	}
}
