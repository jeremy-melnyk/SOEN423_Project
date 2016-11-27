package replica_manager_packet;

import java.io.Serializable;

public class ReplicaManagerPacket implements Serializable {
	private static final long serialVersionUID = 1L;
	private ReplicaManagerOperation replicaManagerOperation;
	private PacketParameters packetParameters;

	public ReplicaManagerPacket(ReplicaManagerOperation replicaManagerOperation, PacketParameters packetParameters) {
		super();
		this.replicaManagerOperation = replicaManagerOperation;
		this.packetParameters = packetParameters;
	}

	public ReplicaManagerOperation getReplicaManagerOperation() {
		return replicaManagerOperation;
	}

	public void setReplicaManagerOperation(ReplicaManagerOperation replicaManagerOperation) {
		this.replicaManagerOperation = replicaManagerOperation;
	}

	public PacketParameters getPacketParameters() {
		return packetParameters;
	}

	public void setPacketParameters(PacketParameters packetParameters) {
		this.packetParameters = packetParameters;
	}
}
