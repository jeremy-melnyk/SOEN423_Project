package packet;

public class TransferReservationReply extends OperationParameters {
	private static final long serialVersionUID = 1L;
	private String unsuccessfulOperation; /*If operation was executed but there was a problem*/
	private int passengerId;
	private int flightId;
	private String departure;
	private String destination;
	private String lastName;
	private String firstName;
	private String date;
	private String flightClass;
	
	public TransferReservationReply(int passengerId, int flightId, String departure, String destination, 
			String lastName, String firstName, String date, String flightClass) {
		super();
		this.passengerId = passengerId;
		this.flightId = flightId;
		this.departure = departure;
		this.destination = destination;
		this.lastName = lastName;
		this.firstName = firstName;
		this.date = date;
		this.flightClass = flightClass;
		
		this.unsuccessfulOperation = "";
	}
	
	/**
	 * Used for non successful operation from replica
	 * i.e. Full flight, no available seats, there's no flight on that date, etc.
	 * 
	 * @param message
	 */
	public TransferReservationReply(String message) {
		super();
		this.unsuccessfulOperation = message;
	}
	
	public TransferReservationReply(){}
	
	public String getUnsuccessfulOperation() {
		return unsuccessfulOperation;
	}

	public void setUnsuccessfulOperation(String unsuccessfulOperation) {
		this.unsuccessfulOperation = unsuccessfulOperation;
	}

	public int getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(int passengerId) {
		this.passengerId = passengerId;
	}

	public int getFlightId() {
		return flightId;
	}

	public void setFlightId(int flightId) {
		this.flightId = flightId;
	}

	public String getDeparture() {
		return departure;
	}

	public void setDeparture(String departure) {
		this.departure = departure;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getFlightClass() {
		return flightClass;
	}

	public void setFlightClass(String flightClass) {
		this.flightClass = flightClass;
	}

	@Override
	public boolean equals(Object o){
		try{
			TransferReservationReply other = (TransferReservationReply) o;
			if(!this.unsuccessfulOperation.isEmpty()){
				return this.unsuccessfulOperation.equalsIgnoreCase(other.unsuccessfulOperation);
			}
			return (this.passengerId == other.passengerId &&
					this.flightId == other.flightId &&
					this.departure == other.departure &&
					this.destination == other.destination &&
					this.firstName.equalsIgnoreCase(other.firstName) &&
					this.lastName.equalsIgnoreCase(other.lastName) && 
					this.date.equalsIgnoreCase(other.date) &&
					this.flightClass.equalsIgnoreCase(other.flightClass))
					?
						true
					:
						false;
		}catch(ClassCastException e){
			return false;
		}
	}
	
	@Override
	public String toString(){
		if(!this.unsuccessfulOperation.isEmpty())
			return this.unsuccessfulOperation;
		StringBuilder sb = new StringBuilder();
		sb.append(this.passengerId); sb.append(" | ");
		sb.append(this.flightId); sb.append(" | ");
		sb.append(this.departure + " ---> " + this.destination + " | ");
		sb.append(this.date + " | ");
		sb.append(this.lastName.toUpperCase() +", " + this.firstName.toUpperCase() + " | ");
		sb.append("Class: "+ this.flightClass);
		return sb.toString();
	}
}
