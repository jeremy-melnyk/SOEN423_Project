package packet;

import java.net.DatagramSocket;
import java.net.InetAddress;

public abstract class OperationParametersHandler {
	protected final int BUFFER_SIZE = 5000;
	protected final InetAddress address;
	protected final int port;
	protected final OperationParameters operationParameters;
	protected final DatagramSocket socket;

	public OperationParametersHandler(InetAddress address, int port, OperationParameters operationParameters, DatagramSocket socket) {
		super();
		this.address = address;
		this.port = port;
		this.operationParameters = operationParameters;
		this.socket = socket;
	}

	public abstract void execute();
}
