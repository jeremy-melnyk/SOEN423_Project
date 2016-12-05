package sequencer;

import java.net.DatagramPacket;

import jeremy_replica.udp.UdpHelper;
import packet.Operation;
import packet.OperationParameters;
import packet.Packet;

public class SequencerPacketDispatcher implements Runnable {
	private final DatagramPacket packet;
	private final Sequencer sequencer;

	public SequencerPacketDispatcher(DatagramPacket packet, Sequencer sequencer) {
		super();
		this.packet = packet;
		this.sequencer = sequencer;
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
		case OPERATION_LOG:
			new OperationLogHandler(packet.getAddress(), packet.getPort(), operationParameters, sequencer).execute();
			break;
		default:
			break;
		}
	}
}
