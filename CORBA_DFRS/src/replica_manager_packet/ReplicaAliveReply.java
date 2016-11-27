package replica_manager_packet;

public class ReplicaAliveReply extends PacketParameters {
	boolean isAlive;
	int replicaPort;

	public ReplicaAliveReply(boolean isAlive, int replicaPort) {
		super();
		this.isAlive = isAlive;
		this.replicaPort = replicaPort;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public int getReplicaPort() {
		return replicaPort;
	}

	public void setReplicaPort(int replicaPort) {
		this.replicaPort = replicaPort;
	}
}
