package packet;

public class EditFlightRecordReply extends OperationParameters {
	private static final long serialVersionUID = 1L;
	private String unsuccessfulOperation;
	private String flightId;
	private String departure;
	private String destination;
	private String date;
	private int economicSeats;
	private int businessSeats;
	private int firstSeats;
	/**
	 * @param flightId
	 * @param departure
	 * @param destination
	 * @param date
	 * @param economicSeats
	 * @param businessSeats
	 * @param firstSeats
	 */
	public EditFlightRecordReply(String flightId, String departure, String destination,
			String date, int economicSeats, int businessSeats, int firstSeats) {
		super();
		this.flightId = flightId;
		this.departure = departure;
		this.destination = destination;
		this.date = date;
		this.economicSeats = economicSeats;
		this.businessSeats = businessSeats;
		this.firstSeats = firstSeats;
	}
	
	public EditFlightRecordReply(String message) {
		super();
		this.unsuccessfulOperation = message;
	}
	
	@Override
	public boolean equals(Object o){
		try{
			EditFlightRecordReply other = (EditFlightRecordReply) o;
			if(!this.unsuccessfulOperation.isEmpty()){
				return this.unsuccessfulOperation.equalsIgnoreCase(other.unsuccessfulOperation);
			}
			return (this.flightId == other.flightId &&
					this.departure == other.departure &&
					this.destination == other.destination &&
					this.date.equalsIgnoreCase(other.date) &&
					this.economicSeats == other.economicSeats &&
					this.businessSeats == other.businessSeats &&
					this.firstSeats == other.firstSeats)
					?
						true
					:
						false;
		}catch(ClassCastException e){
			return false;
		}
	}

}
