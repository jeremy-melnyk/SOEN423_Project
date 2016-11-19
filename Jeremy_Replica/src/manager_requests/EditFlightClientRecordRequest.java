package manager_requests;

import enums.EditType;

public class EditFlightClientRecordRequest {
	private final String DELIMITER = "|";
	private final String DELIMITER_ESCAPE = "\\" + DELIMITER;
	private String managerId;
	private EditType editType;
	private Integer flightRecordId;

	public EditFlightClientRecordRequest(String managerId, EditType editType, Integer flightRecordId) {
		super();
		this.managerId = managerId;
		this.editType = editType;
		this.flightRecordId = flightRecordId;
	}
	
	public EditFlightClientRecordRequest(String editFlightRecordRequest) {
		super();
		String tokens[] = editFlightRecordRequest.split(DELIMITER_ESCAPE); 
		this.managerId = tokens[0].toUpperCase();
		this.editType = EditType.valueOf(tokens[1].toUpperCase());
		this.flightRecordId = Integer.parseInt(tokens[2]);
	}

	public String getManagerId() {
		return managerId;
	}

	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}

	public EditType getEditType() {
		return editType;
	}

	public void setEditType(EditType editType) {
		this.editType = editType;
	}

	public Integer getFlightRecordId() {
		return flightRecordId;
	}

	public void setFlightRecordId(Integer flightRecordId) {
		this.flightRecordId = flightRecordId;
	}

	@Override
	public String toString() {
		return managerId + DELIMITER + editType + DELIMITER + flightRecordId;
	}
}
