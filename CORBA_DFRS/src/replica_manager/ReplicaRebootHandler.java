package replica_manager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import packet.Operation;
import packet.OperationParameters;
import packet.Packet;
import packet.ReplicaRebootReply;
import udp.UdpHelper;

public class ReplicaRebootHandler extends PacketParametersHandler {
	private ReplicaManager replicaManager;

	public ReplicaRebootHandler(InetAddress address, int port, OperationParameters operationParameters, ReplicaManager replicaManager) {
		super(address, port, operationParameters);
		this.replicaManager = replicaManager;
	}

	@Override
	public void execute() {
		DatagramSocket newSocket = null;
		try {
			newSocket = new DatagramSocket();
			
			// Reboot replica and reply to Front End
			boolean result = replicaManager.rebootReplica();
			ReplicaRebootReply replicaRebootReply = new ReplicaRebootReply(result);
			Packet replyPacket = new Packet(Operation.REPLICA_REBOOT, replicaRebootReply);
			
			byte[] message = UdpHelper.getByteArray(replyPacket);
			DatagramPacket reply = new DatagramPacket(message, message.length, address, port);
			newSocket.send(reply);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (newSocket != null) {
				newSocket.close();
			}
		}
	}
}
