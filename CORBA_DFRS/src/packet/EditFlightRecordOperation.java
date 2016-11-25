package packet;

import patterns.Builder;

public class EditFlightRecordOperation extends OperationParameters {
	private String recordId;
	private String fieldName;
	private String newValue;
	
	public EditFlightRecordOperation(String recordId, String fieldName, String newValue){
		this.recordId = recordId;
		this.fieldName = fieldName;
		this.newValue = newValue;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	
	public static class BuilderImpl implements Builder<EditFlightRecordOperation> {
		private final String recordId;
		private String fieldName;
		private String newValue;

		public BuilderImpl(String recordId) {
			this.recordId = recordId;
		}

		public BuilderImpl fieldName(String fieldName) {
			this.fieldName = fieldName;
			return this;
		}

		public BuilderImpl newValue(String newValue) {
			this.newValue = newValue;
			return this;
		}

		@Override
		public EditFlightRecordOperation build() {
			return new EditFlightRecordOperation(this.recordId, this.fieldName, this.newValue);
		}
	}
}
