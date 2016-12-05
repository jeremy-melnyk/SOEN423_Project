package replica_manager;

import java.net.DatagramPacket;

import jeremy_replica.udp.UdpHelper;
import packet.Operation;
import packet.OperationParameters;
import packet.Packet;

public class ReplicaManagerPacketDispatcher implements Runnable {
	private final DatagramPacket packet;
	private final ReplicaManager replicaManager;

	public ReplicaManagerPacketDispatcher(DatagramPacket packet, ReplicaManager replicaManager) {
		super();
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
			new Thread(new ReplicaAliveHandler(packet.getAddress(), packet.getPort(), operationParameters, replicaManager)).start();
			break;
		case REPLICA_REBOOT:
			new Thread(new ReplicaRebootHandler(packet.getAddress(), packet.getPort(), operationParameters, replicaManager)).start();
			break;
		default:
			break;
		}
	}
}
