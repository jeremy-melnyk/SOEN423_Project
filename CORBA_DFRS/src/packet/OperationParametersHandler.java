package packet;

import java.net.DatagramSocket;
import java.net.InetAddress;

public abstract class OperationParametersHandler {
	protected final int BUFFER_SIZE = 5000;
	protected final DatagramSocket socket;
	protected final InetAddress address;
	protected final int port;
	protected final OperationParameters operationParameters;

	public OperationParametersHandler(DatagramSocket socket, InetAddress address, int port, OperationParameters operationParameters) {
		super();
		this.socket = socket;
		this.address = address;
		this.port = port;
		this.operationParameters = operationParameters;
	}

	public abstract void execute();
}
