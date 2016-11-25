package packet;

public class GetBookedFlightCountOperation extends OperationParameters {
	private String recordType;
	
	public GetBookedFlightCountOperation(String recordType){
		this.recordType = recordType;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	
}
