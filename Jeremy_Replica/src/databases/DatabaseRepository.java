package databases;

public class DatabaseRepository {
	private FlightReservationDb flightReservationDb;
	private FlightRecordDb flightRecordDb;
	private PassengerRecordDb passengerRecordDb;
	private ManagerRecordDb managerRecordDb;
	
	public DatabaseRepository(FlightReservationDb flightReservationDb, FlightRecordDb flightRecordDb, PassengerRecordDb passengerRecordDb, ManagerRecordDb managerRecordDb) {
		super();
		this.flightReservationDb = flightReservationDb;
		this.flightRecordDb = flightRecordDb;
		this.passengerRecordDb = passengerRecordDb;
		this.managerRecordDb = managerRecordDb;
	}

	public FlightReservationDb getFlightReservationDb() {
		return flightReservationDb;
	}

	public void setFlightReservationDb(FlightReservationDb flightReservationDb) {
		this.flightReservationDb = flightReservationDb;
	}

	public FlightRecordDb getFlightRecordDb() {
		return flightRecordDb;
	}

	public void setFlightRecordDb(FlightRecordDb flightRecordDb) {
		this.flightRecordDb = flightRecordDb;
	}

	public PassengerRecordDb getPassengerRecordDb() {
		return passengerRecordDb;
	}

	public void setPassengerRecordDb(PassengerRecordDb passengerRecordDb) {
		this.passengerRecordDb = passengerRecordDb;
	}

	public ManagerRecordDb getManagerRecordDb() {
		return managerRecordDb;
	}

	public void setManagerRecordDb(ManagerRecordDb managerRecordDb) {
		this.managerRecordDb = managerRecordDb;
	}
}
