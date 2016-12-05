package replica_manager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;

import packet.Operation;
import packet.OperationParameters;
import packet.Packet;

public class ReplicaManagerPacketDispatcher implements Runnable {
	private final DatagramPacket packet;

	public ReplicaManagerPacketDispatcher(DatagramPacket packet) {
		super();
		this.packet = packet;
	}

	@Override
	public void run() {
		handlePacket();
	}

	private void handlePacket() {
		Packet replicaPacket = null;
		try {
			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(this.packet.getData());
			ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
			replicaPacket = (Packet)objectInputStream.readObject();
			objectInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}
		Operation operation = replicaPacket.getReplicaOperation();
		OperationParameters operationParameters = replicaPacket.getOperationParameters();
		switch (operation) {
		case REPLICA_ALIVE:
			new ReplicaAliveHandler(packet.getAddress(), packet.getPort(), operationParameters).execute();
			break;
		case REPLICA_REBOOT:
			new ReplicaRebootHandler(packet.getAddress(), packet.getPort(), operationParameters).execute();
			break;
		default:
			break;
		}
	}
}
