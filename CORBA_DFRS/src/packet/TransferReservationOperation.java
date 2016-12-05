package packet;

import patterns.Builder;

public class TransferReservationOperation extends OperationParameters {
	private static final long serialVersionUID = 1L;
	private String passengerId;
	private String currentCity;
	private String otherCity;
	
	
	public TransferReservationOperation(String passengerId, String currentCity, String otherCity) {
		super();
		this.passengerId = passengerId;
		this.currentCity = currentCity;
		this.otherCity = otherCity;
	}

	public String getPassengerId() {
		return passengerId;
	}


	public void setPassengerId(String passengerId) {
		this.passengerId = passengerId;
	}


	public String getCurrentCity() {
		return currentCity;
	}


	public void setCurrentCity(String currentCity) {
		this.currentCity = currentCity;
	}


	public String getOtherCity() {
		return otherCity;
	}


	public void setOtherCity(String otherCity) {
		this.otherCity = otherCity;
	}
	
	
	public static class BuilderImpl implements Builder<TransferReservationOperation> {
		private final String passengerId;
		private String currentCity;
		private String otherCity;
		
		
		public BuilderImpl(String passengerId) {
			this.passengerId = passengerId;
		}

		public BuilderImpl currentCity(String currentCity) {
			this.currentCity = currentCity;
			return this;
		}

		public BuilderImpl otherCity(String otherCity) {
			this.otherCity = otherCity;
			return this;
		}

		@Override
		public TransferReservationOperation build() {
			return new TransferReservationOperation(passengerId, currentCity, otherCity);
		}
	}
}
