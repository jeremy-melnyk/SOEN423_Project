package packet;

import java.net.InetAddress;

public abstract class OperationParametersHandler {
	protected final int BUFFER_SIZE = 5000;
	protected final InetAddress address;
	protected final int port;
	protected final OperationParameters operationParameters;

	public OperationParametersHandler(InetAddress address, int port, OperationParameters operationParameters) {
		super();
		this.address = address;
		this.port = port;
		this.operationParameters = operationParameters;
	}

	public abstract void execute();
}
