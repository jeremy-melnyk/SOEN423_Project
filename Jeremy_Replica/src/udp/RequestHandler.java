package udp;

import java.net.DatagramSocket;
import java.net.InetAddress;

import databases.DatabaseRepository;

public abstract class RequestHandler {
	protected final int BUFFER_SIZE = 1000;
	protected final InetAddress address;
	protected final int port;
	protected final Request request;
	protected final DatagramSocket socket;
	protected final DatabaseRepository databaseRepository;

	public RequestHandler(InetAddress address, int port, Request request, DatagramSocket socket,
			DatabaseRepository databaseRepository) {
		super();
		this.address = address;
		this.port = port;
		this.request = request;
		this.socket = socket;
		this.databaseRepository = databaseRepository;
	}

	public abstract void execute();
}
