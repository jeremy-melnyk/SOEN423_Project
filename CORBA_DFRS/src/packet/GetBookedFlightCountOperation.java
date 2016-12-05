package packet;

public class GetBookedFlightCountOperation extends OperationParameters {
	private static final long serialVersionUID = 1L;
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
