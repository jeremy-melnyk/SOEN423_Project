package replica_manager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import packet.OperationParameters;
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
			
			boolean result = replicaManager.rebootReplica();
			ReplicaRebootReply replicaRebootReaply = new ReplicaRebootReply(result);
			
			byte[] message = UdpHelper.getByteArray(replicaRebootReaply);
			DatagramPacket reply = new DatagramPacket(message, message.length, address, port);
			newSocket.send(reply);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (newSocket != null) {
				newSocket.close();
			}
		}
	}
}
