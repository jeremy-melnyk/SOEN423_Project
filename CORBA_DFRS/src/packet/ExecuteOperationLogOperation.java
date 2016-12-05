package packet;

import java.util.ArrayList;

public class ExecuteOperationLogOperation extends OperationParameters {
	private static final long serialVersionUID = 1L;
	private ArrayList<Packet> operationLog;

	public ExecuteOperationLogOperation(ArrayList<Packet> operationLog) {
		super();
		this.operationLog = operationLog;
	}

	public ArrayList<Packet> getOperationLog() {
		return operationLog;
	}

	public void setOperationLog(ArrayList<Packet> operationLog) {
		this.operationLog = operationLog;
	}
}
