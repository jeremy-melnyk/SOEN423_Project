package replica_manager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import replica_manager_packet.PacketParameters;
import replica_manager_packet.PacketParametersHandler;
import replica_manager_packet.ReplicaAliveOperation;
import replica_manager_packet.ReplicaAliveReply;
import udp.UdpHelper;

public class ReplicaAliveHandler extends PacketParametersHandler {

	public ReplicaAliveHandler(InetAddress address, int port, PacketParameters packetParameters,
			DatagramSocket socket) {
		super(address, port, packetParameters, socket);
	}

	@Override
	public void execute() {
		DatagramSocket newSocket = null;
		try {
			newSocket = new DatagramSocket();
			
			ReplicaAliveOperation replicaAliveOperation = (ReplicaAliveOperation) packetParameters;
			int portToCheck = replicaAliveOperation.getPortToCheck();
			
			// TODO : Confirm with Caio if port of replica is being sent, or if port of replica is being replied
			// TODO : Query alive status of replica
			ReplicaAliveReply replicaAliveReply = new ReplicaAliveReply(true, portToCheck);
			
			byte[] message = UdpHelper.getByteArray(replicaAliveReply);
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
