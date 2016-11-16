package packet;

import patterns.Builder;

public class BookFlightOperation extends OperationParameters {
	private String firstName;
	private String lastName;
	private String address;
	private String phoneNumber;
	private String destination;
	private String date;
	private String flightClass;

	private BookFlightOperation(String firstName, String lastName, String address, String phoneNumber,
			String destination, String date, String flightClass) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.destination = destination;
		this.date = date;
		this.flightClass = flightClass;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getAddress() {
		return address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getDestination() {
		return destination;
	}

	public String getDate() {
		return date;
	}

	public String getFlightClass() {
		return flightClass;
	}

	public static class BuilderImpl implements Builder<BookFlightOperation> {
		private final String firstName;
		private String lastName;
		private String address;
		private String phoneNumber;
		private String destination;
		private String date;
		private String flightClass;

		public BuilderImpl(String firstName) {
			this.firstName = firstName;
		}

		public BuilderImpl lastName(String lastName) {
			this.lastName = lastName;
			return this;
		}

		public BuilderImpl address(String address) {
			this.address = address;
			return this;
		}

		public BuilderImpl phoneNumber(String phoneNumber) {
			this.phoneNumber = phoneNumber;
			return this;
		}

		public BuilderImpl destination(String destination) {
			this.destination = destination;
			return this;
		}

		public BuilderImpl date(String date) {
			this.date = date;
			return this;
		}

		public BuilderImpl flightClass(String flightClass) {
			this.flightClass = flightClass;
			return this;
		}

		@Override
		public BookFlightOperation build() {
			return new BookFlightOperation(firstName, lastName, address, phoneNumber, destination, date, flightClass);
		}
	}
}
