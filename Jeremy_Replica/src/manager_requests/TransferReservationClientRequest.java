package manager_requests;

<<<<<<< refs/remotes/origin/master
import global.Constants;

public class TransferReservationClientRequest {
=======
public class TransferReservationClientRequest {
	private final String DELIMITER = "|";
	private final String DELIMITER_ESCAPE = "\\" + DELIMITER;
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
	private String managerId;
	private int flightReservationId;

	public TransferReservationClientRequest(String transferReservationRequest) {
		super();
<<<<<<< refs/remotes/origin/master
		String tokens[] = transferReservationRequest.split(Constants.DELIMITER_ESCAPE);
=======
		String tokens[] = transferReservationRequest.split(DELIMITER_ESCAPE);
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
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
<<<<<<< refs/remotes/origin/master
		return managerId + Constants.DELIMITER + flightReservationId;
=======
		return managerId + DELIMITER + flightReservationId;
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
	}
}
