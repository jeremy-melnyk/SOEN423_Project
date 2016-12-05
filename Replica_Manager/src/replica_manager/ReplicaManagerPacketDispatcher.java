package replica_manager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;

import replica_manager_packet.PacketParameters;
import replica_manager_packet.ReplicaManagerOperation;
import replica_manager_packet.ReplicaManagerPacket;

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
		ReplicaManagerPacket replicaManagerPacket = null;
		try {
			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(this.packet.getData());
			ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
			replicaManagerPacket = (ReplicaManagerPacket)objectInputStream.readObject();
			objectInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}
		ReplicaManagerOperation replicaManagerOperation = replicaManagerPacket.getReplicaManagerOperation();
		PacketParameters packetParameters = replicaManagerPacket.getPacketParameters();
		switch (replicaManagerOperation) {
		case REPLICA_ALIVE:
			new ReplicaAliveHandler(packet.getAddress(), packet.getPort(), packetParameters).execute();
			break;
		case REPLICA_REBOOT:
			new ReplicaRebootHandler(packet.getAddress(), packet.getPort(), packetParameters).execute();
			break;
		}
	}
}
