package replica_manager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import global.Constants;
import packet.Operation;
import packet.OperationParameters;
import packet.OperationParametersHandler;
import packet.Packet;
import packet.ReplicaAliveOperation;
import packet.ReplicaAliveReply;
import packet.ReplicaRebootOperation;
import packet.ReplicaRebootReply;
import udp.UdpHelper;

public class ReplicaCrashHandler extends OperationParametersHandler {
	private ReplicaManager replicaManager;
	
	public ReplicaCrashHandler(DatagramSocket socket, InetAddress address, int port, OperationParameters operationParameters, ReplicaManager replicaManager) {
		super(socket, address, port, operationParameters);
		this.replicaManager = replicaManager;
	}
	
	@Override
	public void execute() {
		DatagramSocket newSocket = null;
		DatagramSocket rebootSocket = null;
		try {
			newSocket = new DatagramSocket();
			rebootSocket = new DatagramSocket();
			newSocket.setSoTimeout(Constants.REPLICA_MANAGER_TIMEOUT);
			
			ReplicaAliveOperation incomingReplicaAliveOperation = (ReplicaAliveOperation) operationParameters;
			int portToCheck = incomingReplicaAliveOperation.getPortToCheck();
			int portToPing = portToCheck;
			if(portToCheck == -1){
				portToPing = replicaManager.getReplicaPort();
			}
			
			int[] replicaManagerPorts = replicaManager.getReplicaManagerPorts();
			ArrayList<ReplicaAliveReply> replicaAliveReplies = new ArrayList<ReplicaAliveReply>();
			// Ask all RMs for their opinion
			for(int otherReplicaManager : replicaManagerPorts){
				ReplicaAliveOperation replicaAliveOperation = new ReplicaAliveOperation(portToPing);
				Packet packet = new Packet(newSocket.getInetAddress(), newSocket.getLocalPort(), Operation.REPLICA_ALIVE, replicaAliveOperation);
				byte[] message = UdpHelper.getByteArray(packet);
				DatagramPacket request = new DatagramPacket(message, message.length, address, otherReplicaManager);
				newSocket.send(request);
				
				// Receive UdpParser reply, return false if timeout
				byte[] buffer = new byte[BUFFER_SIZE];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				ReplicaAliveReply replicaAliveReply = null;
				try{
					newSocket.receive(reply);
					Packet udpParserPacket = (Packet) UdpHelper.getObjectFromByteArray(reply.getData());
					replicaAliveReply = (ReplicaAliveReply) udpParserPacket.getOperationParameters();
				} catch(SocketTimeoutException e){
					replicaAliveReply = new ReplicaAliveReply(false, portToPing);
				}
				replicaAliveReplies.add(replicaAliveReply);
			}
			
			int aliveAgreements = 0;
			int deadAgreements = 0;
			for(ReplicaAliveReply replicaAliveReply : replicaAliveReplies){
				if(replicaAliveReply.isAlive()){
					++aliveAgreements;
				}
				if(!replicaAliveReply.isAlive()){
					++deadAgreements;
				}
			}
					
			// Send reboot request to self
			boolean shouldReboot = deadAgreements > aliveAgreements;
			System.out.println("Alive agreements? " + aliveAgreements);
			System.out.println("Dead agreements? " + deadAgreements);
			System.out.println("Should Reboot? " + shouldReboot);
			if (shouldReboot){
				int replicaManagerPort = replicaManager.getPort();
				ReplicaRebootOperation replicaRebootOperation = new ReplicaRebootOperation();
				Packet packet = new Packet(rebootSocket.getInetAddress(), rebootSocket.getLocalPort(), Operation.REPLICA_REBOOT, replicaRebootOperation);
				byte[] message = UdpHelper.getByteArray(packet);
				DatagramPacket request = new DatagramPacket(message, message.length, address, replicaManagerPort);
				rebootSocket.send(request);
				
				byte[] buffer = new byte[BUFFER_SIZE];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				rebootSocket.receive(reply);
				Packet udpParserPacket = (Packet) UdpHelper.getObjectFromByteArray(reply.getData());
				ReplicaRebootReply replicaRebootReply = (ReplicaRebootReply) udpParserPacket.getOperationParameters();
				System.out.println("Rebooted result? " + replicaRebootReply.isRebooted());
			}
			
			// Return if the replica was rebooted
			ReplicaAliveReply replicaAliveReply = new ReplicaAliveReply(shouldReboot, portToPing);
			Packet replyPacket = new Packet(socket.getInetAddress(), socket.getLocalPort(), Operation.REPLICA_ALIVE, replicaAliveReply);
			byte[] replyMessage = UdpHelper.getByteArray(replyPacket);
			DatagramPacket finalReply = new DatagramPacket(replyMessage, replyMessage.length, address, port);
			socket.send(finalReply);
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
			if (rebootSocket != null) {
				rebootSocket.close();
			}
		}
	}
}
