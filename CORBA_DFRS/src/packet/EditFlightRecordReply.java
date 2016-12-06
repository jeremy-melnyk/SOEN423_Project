package packet;

import global.Constants;

public class EditFlightRecordReply extends OperationParameters {
	private static final long serialVersionUID = 1L;
	private String error;
	private String flightId;
	private String origin;
	private String destination;
	private String date;
	private String economySeats;
	private String businessSeats;
	private String firstSeats;
	
	public EditFlightRecordReply(String flightId, String origin, String destination, String date,
			String economySeats, String businessSeats, String firstSeats) {
		super();
		this.error = "";
		this.flightId = flightId;
		this.origin = origin;
		this.destination = destination;
		this.date = date;
		this.economySeats = economySeats;
		this.businessSeats = businessSeats;
		this.firstSeats = firstSeats;
	}
	
	public EditFlightRecordReply(String error) {
		super();
		this.error = error;
		this.flightId = "";
		this.origin = "";
		this.destination = "";
		this.date = "";
		this.economySeats = "";
		this.businessSeats = "";
		this.firstSeats = "";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EditFlightRecordReply other = (EditFlightRecordReply) obj;
		if (businessSeats == null) {
			if (other.businessSeats != null)
				return false;
		} else if (!businessSeats.equalsIgnoreCase(other.businessSeats))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equalsIgnoreCase(other.date))
			return false;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equalsIgnoreCase(other.destination))
			return false;
		if (economySeats == null) {
			if (other.economySeats != null)
				return false;
		} else if (!economySeats.equalsIgnoreCase(other.economySeats))
			return false;
		if (error == null) {
			if (other.error != null)
				return false;
		} else if (!error.equalsIgnoreCase(other.error))
			return false;
		if (firstSeats == null) {
			if (other.firstSeats != null)
				return false;
		} else if (!firstSeats.equalsIgnoreCase(other.firstSeats))
			return false;
		if (flightId == null) {
			if (other.flightId != null)
				return false;
		} else if (!flightId.equalsIgnoreCase(other.flightId))
			return false;
		if (origin == null) {
			if (other.origin != null)
				return false;
		} else if (!origin.equalsIgnoreCase(other.origin))
			return false;
		return true;
	}

	@Override
	public String toString() {
		if(!error.isEmpty()){
			return error;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(flightId + Constants.DELIMITER + origin.toUpperCase() + Constants.DELIMITER + destination.toUpperCase() + Constants.DELIMITER + date);
		sb.append(Constants.DELIMITER + "E" + economySeats);
		sb.append(Constants.DELIMITER + "B" + businessSeats);
		sb.append(Constants.DELIMITER + "F" + firstSeats);
		return sb.toString();
	}
}
