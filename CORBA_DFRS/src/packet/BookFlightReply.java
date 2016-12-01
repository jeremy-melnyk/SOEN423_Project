package packet;

public class BookFlightReply extends OperationParameters {
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
	
	
	public BookFlightReply(int passengerId, int flightId, String departure, String destination, 
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
	public BookFlightReply(String message) {
		super();
		this.unsuccessfulOperation = message;
	}
	
	@Override
	public boolean equals(Object o){
		try{
			BookFlightReply other = (BookFlightReply) o;
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
	
}
