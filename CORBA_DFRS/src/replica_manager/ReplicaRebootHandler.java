package replica_manager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import replica_manager_packet.PacketParameters;
import replica_manager_packet.ReplicaRebootReply;
import udp.UdpHelper;

public class ReplicaRebootHandler extends PacketParametersHandler {

	public ReplicaRebootHandler(InetAddress address, int port, PacketParameters packetParameters) {
		super(address, port, packetParameters);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() {
		DatagramSocket newSocket = null;
		try {
			newSocket = new DatagramSocket();
			
			// TODO : Reboot replica
			ReplicaRebootReply replicaRebootReaply = new ReplicaRebootReply(true);
			
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
