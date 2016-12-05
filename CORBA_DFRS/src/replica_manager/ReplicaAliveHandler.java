package replica_manager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import packet.Operation;
import packet.OperationParameters;
import packet.OperationParametersHandler;
import packet.Packet;
import packet.ReplicaAliveOperation;
import packet.ReplicaAliveReply;
import udp.UdpHelper;

public class ReplicaAliveHandler extends OperationParametersHandler implements Runnable {
	// 2 seconds
	private final int TIMEOUT = 2000;
	private ReplicaManager replicaManager;
	
	public ReplicaAliveHandler(InetAddress address, int port, OperationParameters operationParameters, ReplicaManager replicaManager) {
		super(address, port, operationParameters);
		this.replicaManager = replicaManager;
	}
	
	@Override
	public void run() {
		execute();
	}

	@Override
	public void execute() {
		DatagramSocket newSocket = null;
		try {
			newSocket = new DatagramSocket();
			newSocket.setSoTimeout(TIMEOUT);
			
			ReplicaAliveOperation incomingReplicaAliveOperation = (ReplicaAliveOperation) operationParameters;
			int portToCheck = incomingReplicaAliveOperation.getPortToCheck();
			int portToPing = portToCheck;
			if(portToCheck == -1){
				portToPing = this.replicaManager.getReplicaPort();
			}
			
			// If in the process of rebooting
			if(replicaManager.isRebooting()){
				// Send reply back to Front End 
				ReplicaAliveReply isRebootingReplicaAliveReply = new ReplicaAliveReply(false, portToPing);
				Packet isRebootingReplyPacket = new Packet(Operation.REPLICA_ALIVE, isRebootingReplicaAliveReply);
				byte[] isRebootingReplyMessage = UdpHelper.getByteArray(isRebootingReplyPacket);
				DatagramPacket isRebootingReply = new DatagramPacket(isRebootingReplyMessage, isRebootingReplyMessage.length, address, port);
				newSocket.send(isRebootingReply);
				return;
			}
			
			// Ping UdpParser too see if replica is alive
			ReplicaAliveOperation replicaAliveOperation = new ReplicaAliveOperation(portToPing);
			Packet packet = new Packet(Operation.REPLICA_ALIVE, replicaAliveOperation);
			byte[] message = UdpHelper.getByteArray(packet);
			DatagramPacket request = new DatagramPacket(message, message.length, address, portToPing);
			newSocket.send(request);
			
			// Receive UdpParser reply, return false if timeout
			byte[] buffer = new byte[BUFFER_SIZE];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			byte[] replyMessage = null;
			try{
				newSocket.receive(reply);
				replyMessage = reply.getData();
			} catch(SocketTimeoutException e){
				ReplicaAliveReply replicaAliveReply = new ReplicaAliveReply(false, portToPing);
				Packet replyPacket = new Packet(Operation.REPLICA_ALIVE, replicaAliveReply);
				replyMessage = UdpHelper.getByteArray(replyPacket);
			}
			
			DatagramPacket finalReply = new DatagramPacket(replyMessage, replyMessage.length, address, port);
			newSocket.send(finalReply);
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
