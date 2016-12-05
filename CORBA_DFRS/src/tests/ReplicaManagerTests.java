package tests;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import jeremy_replica.udp.UdpHelper;
import json.JSONReader;
import packet.Operation;
import packet.OperationParameters;
import packet.Packet;
import packet.ReplicaRebootOperation;
import packet.ReplicaRebootReply;

public class ReplicaManagerTests {
	public static int BUFFER_SIZE = 50000;

	public static void main(String[] args) {
		// Initialize ports configuration
		JSONReader jsonReader = new JSONReader();
		jsonReader.initialize();
		
		// Choose parser to test
		String username = "Caio";
		
		int udpPort = jsonReader.getPortForKeys(username, "RM");
		
		testRebootReplica(udpPort);
	}

	private static void testRebootReplica(int port) {
		// Build the action for the packet
		ReplicaRebootOperation rebootReplicaOperation = new ReplicaRebootOperation();

		// Create a packet with the operation
		Packet packet = new Packet(Operation.REPLICA_REBOOT, rebootReplicaOperation);

		// Process the packet
		OperationParameters result = processOperationPacket(packet, port);
		ReplicaRebootReply reply = (ReplicaRebootReply) result;
		System.out.println("Replica isRebooted: " + reply.isRebooted());
	}
	
	private static OperationParameters processOperationPacket(Packet packet, int port) {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			byte[] message = UdpHelper.getByteArray(packet);
			InetAddress host = InetAddress.getByName("localhost");
			int serverPort = port;
			DatagramPacket requestPacket = new DatagramPacket(message, message.length, host, serverPort);
			socket.send(requestPacket);
			byte[] buffer = new byte[BUFFER_SIZE];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			socket.receive(reply);
			OperationParameters result = (OperationParameters) UdpHelper.getObjectFromByteArray(reply.getData());
			return result;
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			socket.close();
		}
		return null;
	}
}
