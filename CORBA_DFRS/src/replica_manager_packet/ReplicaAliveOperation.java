package replica_manager_packet;

public class ReplicaAliveOperation extends PacketParameters {
	private static final long serialVersionUID = 1L;
	int portToCheck;

	public ReplicaAliveOperation(int portToCheck) {
		super();
		this.portToCheck = portToCheck;
	}

	public int getPortToCheck() {
		return portToCheck;
	}

	public void setPortToCheck(int portToCheck) {
		this.portToCheck = portToCheck;
	}
}
