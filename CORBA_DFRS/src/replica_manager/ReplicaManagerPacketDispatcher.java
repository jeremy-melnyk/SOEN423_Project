package replica_manager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import jeremy_replica.udp.UdpHelper;
import packet.Operation;
import packet.OperationParameters;
import packet.Packet;

public class ReplicaManagerPacketDispatcher implements Runnable {
	private final DatagramPacket packet;
	private final ReplicaManager replicaManager;
	private final DatagramSocket socket;

	public ReplicaManagerPacketDispatcher(DatagramSocket socket, DatagramPacket packet, ReplicaManager replicaManager) {
		super();
		this.socket = socket;
		this.packet = packet;
		this.replicaManager = replicaManager;
	}

	@Override
	public void run() {
		handlePacket();
	}

	private void handlePacket() {
		Packet replicaPacket = (Packet) UdpHelper.getObjectFromByteArray(packet.getData());
		Operation operation = replicaPacket.getReplicaOperation();
		OperationParameters operationParameters = replicaPacket.getOperationParameters();
		switch (operation) {
		case REPLICA_ALIVE:
			new ReplicaAliveHandler(socket, packet.getAddress(), packet.getPort(), operationParameters, replicaManager).execute();
			break;
		case REPLICA_REBOOT:
			new ReplicaRebootHandler(socket, packet.getAddress(), packet.getPort(), operationParameters, replicaManager).execute();
			break;
		case REPLICA_CRASH:
			new ReplicaCrashHandler(socket, packet.getAddress(), packet.getPort(), operationParameters, replicaManager).execute();
			break;
		case REPLICA_KILL:
			new ReplicaKillHandler(socket, packet.getAddress(), packet.getPort(), operationParameters, replicaManager).execute();
			break;
		default:
			break;
		}
	}
}
