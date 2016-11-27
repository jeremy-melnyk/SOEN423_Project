package replica_manager_packet;

public class ReplicaRebootReply {
	boolean isRebooted;

	public ReplicaRebootReply(boolean isRebooted) {
		super();
		this.isRebooted = isRebooted;
	}

	public boolean isRebooted() {
		return isRebooted;
	}

	public void setRebooted(boolean isRebooted) {
		this.isRebooted = isRebooted;
	}
}
