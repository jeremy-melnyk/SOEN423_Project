package replica_manager_packet;

public class ReplicaRebootReply extends PacketParameters {
	private static final long serialVersionUID = 1L;
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
