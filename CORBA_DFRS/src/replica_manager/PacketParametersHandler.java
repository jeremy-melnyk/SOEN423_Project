package replica_manager;

import java.net.InetAddress;

import packet.OperationParameters;

public abstract class PacketParametersHandler {
	protected final int BUFFER_SIZE = 5000;
	protected final InetAddress address;
	protected final int port;
	protected final OperationParameters operationParameters;

	public PacketParametersHandler(InetAddress address, int port, OperationParameters operationParameters) {
		super();
		this.address = address;
		this.port = port;
		this.operationParameters = operationParameters;
	}

	public abstract void execute();
}
