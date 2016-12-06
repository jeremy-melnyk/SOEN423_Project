package replica_manager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import packet.Operation;
import packet.OperationParameters;
import packet.OperationParametersHandler;
import packet.Packet;
import packet.ReplicaRebootReply;
import udp.UdpHelper;

public class ReplicaKillHandler extends OperationParametersHandler {
	private ReplicaManager replicaManager;

	public ReplicaKillHandler(DatagramSocket socket, InetAddress address, int port, OperationParameters operationParameters, ReplicaManager replicaManager) {
		super(socket, address, port, operationParameters);
		this.replicaManager = replicaManager;
	}
	
	@Override
	public void execute() {
		DatagramSocket newSocket = null;
		try {
			newSocket = new DatagramSocket();
			
			// Kill replica
			boolean result = replicaManager.killReplica();	
			ReplicaRebootReply replicaRebootReply = new ReplicaRebootReply(result);
			Packet replyPacket = new Packet(socket.getInetAddress(), socket.getLocalPort(), Operation.REPLICA_KILL, replicaRebootReply);		
			byte[] message = UdpHelper.getByteArray(replyPacket);
			DatagramPacket reply = new DatagramPacket(message, message.length, address, port);
			socket.send(reply);
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
