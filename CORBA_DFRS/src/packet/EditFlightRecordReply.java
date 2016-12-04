package packet;

public class EditFlightRecordReply extends OperationParameters {
	private static final long serialVersionUID = 1L;
	private String message;
	public EditFlightRecordReply(String message) {
		super();
		this.message = message;
	}
	
	public EditFlightRecordReply() {
		super();
	}

	public void setUnsuccessfulOperation(String message) {
		this.message = message;
	}

	@Override
	public boolean equals(Object o){
		try{
			return (this.message.equalsIgnoreCase((String) o));
		}catch(ClassCastException e){
			return false;
		}
	}
	
	@Override
	public String toString(){
		return message;
	}

}
