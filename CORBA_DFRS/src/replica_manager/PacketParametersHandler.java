package replica_manager;

import java.net.DatagramSocket;
import java.net.InetAddress;

import replica_manager_packet.PacketParameters;

public abstract class PacketParametersHandler {
	protected final int BUFFER_SIZE = 5000;
	protected final InetAddress address;
	protected final int port;
	protected final PacketParameters packetParameters;
	protected final DatagramSocket socket;

	public PacketParametersHandler(InetAddress address, int port, PacketParameters packetParameters, DatagramSocket socket) {
		super();
		this.address = address;
		this.port = port;
		this.packetParameters = packetParameters;
		this.socket = socket;
	}

	public abstract void execute();
}
