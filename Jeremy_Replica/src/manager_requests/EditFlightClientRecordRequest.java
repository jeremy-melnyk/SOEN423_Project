package manager_requests;

import enums.EditType;
<<<<<<< refs/remotes/origin/master
import global.Constants;

public class EditFlightClientRecordRequest {
=======

public class EditFlightClientRecordRequest {
	private final String DELIMITER = "|";
	private final String DELIMITER_ESCAPE = "\\" + DELIMITER;
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
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
<<<<<<< refs/remotes/origin/master
		String tokens[] = editFlightRecordRequest.split(Constants.DELIMITER_ESCAPE); 
=======
		String tokens[] = editFlightRecordRequest.split(DELIMITER_ESCAPE); 
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
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
<<<<<<< refs/remotes/origin/master
		return managerId + Constants.DELIMITER + editType + Constants.DELIMITER + flightRecordId;
=======
		return managerId + DELIMITER + editType + DELIMITER + flightRecordId;
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
	}
}
