package tests;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import jeremy_replica.udp.UdpHelper;
import json.JSONReader;
import packet.Operation;
import packet.Packet;
import packet.ReplicaAliveOperation;
import packet.ReplicaAliveReply;
import packet.ReplicaRebootOperation;
import packet.ReplicaRebootReply;

public class ReplicaManagerTests {
	public static int BUFFER_SIZE = 50000;

	public static void main(String[] args) {
		// Initialize ports configuration
		JSONReader jsonReader = new JSONReader();
		
		int[] ports = new int[4];
		ports[0] = jsonReader.getPortForKeys("Jeremy", "RM");
		ports[1] = jsonReader.getPortForKeys("Caio", "RM");
		ports[2] = jsonReader.getPortForKeys("Mark", "RM");
		ports[3] = jsonReader.getPortForKeys("Patrick", "RM");
		
		System.out.println("Testing: " + ports[0]);
		testOne(ports[0]);
		System.out.println("Testing: " + ports[1]);
		testTwo(ports[1]);
		System.out.println("Testing: " + ports[2]);
		testThree(ports[2]);
		System.out.println("Testing: " + ports[3]);
		testFour(ports[3]);
		
		for(int port : ports){
			System.out.println("Checking status of replica: " + port);
			testReplicaAlive(port);
		}
		
		System.out.println("Tests complete!");
	}
	
	private static void testOne(int port){
		testReplicaAlive(port);
		testKillReplica(port);
		testReplicaCrash(port);
		testReplicaAlive(port);
		testRebootReplica(port);
		testReplicaAlive(port);	
	}
	
	private static void testTwo(int port){
		testReplicaAlive(port);
		testReplicaCrash(port);
		testReplicaAlive(port);
	}
	
	private static void testThree(int port){
		testReplicaCrash(port);
		testKillReplica(port);
		testReplicaCrash(port);
		testReplicaAlive(port);
	}
	
	private static void testFour(int port){
		testRebootReplica(port);
		testReplicaCrash(port);
		testKillReplica(port);
		testReplicaCrash(port);
		testReplicaAlive(port);
	}
	
	private static void testReplicaCrash(int port) {
		// Build the action for the packet
		ReplicaAliveOperation replicaAliveOperation = new ReplicaAliveOperation();

		// Create a packet with the operation
		Packet packet = new Packet(Operation.REPLICA_CRASH, replicaAliveOperation);

		// Process the packet
		Packet result = processOperationPacket(packet, port);
		ReplicaAliveReply reply = (ReplicaAliveReply) result.getOperationParameters();
		System.out.println("Replica wasCrashed: " + reply.isAlive());
	}
	
	private static void testKillReplica(int port) {
		// Build the action for the packet
		ReplicaRebootOperation rebootReplicaOperation = new ReplicaRebootOperation();

		// Create a packet with the operation
		Packet packet = new Packet(Operation.REPLICA_KILL, rebootReplicaOperation);

		// Process the packet
		Packet result = processOperationPacket(packet, port);
		ReplicaRebootReply reply = (ReplicaRebootReply) result.getOperationParameters();
		System.out.println("Replica isKilled: " + reply.isRebooted());
	}

	private static void testRebootReplica(int port) {
		// Build the action for the packet
		ReplicaRebootOperation rebootReplicaOperation = new ReplicaRebootOperation();

		// Create a packet with the operation
		Packet packet = new Packet(Operation.REPLICA_REBOOT, rebootReplicaOperation);

		// Process the packet
		Packet result = processOperationPacket(packet, port);
		ReplicaRebootReply reply = (ReplicaRebootReply) result.getOperationParameters();
		System.out.println("Replica isRebooted: " + reply.isRebooted());
	}
	
	private static void testReplicaAlive(int port) {
		// Build the action for the packet
		ReplicaAliveOperation replicaAliveOperation = new ReplicaAliveOperation();

		// Create a packet with the operation
		Packet packet = new Packet(Operation.REPLICA_ALIVE, replicaAliveOperation);

		// Process the packet
		Packet result = processOperationPacket(packet, port);
		ReplicaAliveReply reply = (ReplicaAliveReply) result.getOperationParameters();
		System.out.println("Replica isAlive: " + reply.isAlive());
	}
	
	private static Packet processOperationPacket(Packet packet, int port) {
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
			Packet result = (Packet) UdpHelper.getObjectFromByteArray(reply.getData());
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