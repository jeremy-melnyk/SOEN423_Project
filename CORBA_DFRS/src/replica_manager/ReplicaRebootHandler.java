package replica_manager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import global.Constants;
import packet.ExecuteOperationLogOperation;
import packet.Operation;
import packet.OperationLogOperation;
import packet.OperationLogReply;
import packet.OperationParameters;
import packet.OperationParametersHandler;
import packet.Packet;
import packet.ReplicaAliveOperation;
import packet.ReplicaRebootReply;
import udp.UdpHelper;

public class ReplicaRebootHandler extends OperationParametersHandler {
	private ReplicaManager replicaManager;

	public ReplicaRebootHandler(DatagramSocket socket, InetAddress address, int port, OperationParameters operationParameters, ReplicaManager replicaManager) {
		super(socket, address, port, operationParameters);
		this.replicaManager = replicaManager;
	}
	
	@Override
	public void execute() {
		DatagramSocket newSocket = null;
		DatagramSocket pingSocket = null;
		try {
			newSocket = new DatagramSocket();
			pingSocket = new DatagramSocket();
			pingSocket.setSoTimeout(Constants.REPLICA_MANAGER_TIMEOUT);
			
			// Reboot replica
			int replicaPort = replicaManager.getReplicaPort();
			replicaManager.setRebooting(true);
			boolean result = replicaManager.rebootReplica();
			
			//Ping UdpParser until a response is received
			boolean replicaRebooting = true;
			int attempts = 0;
			while(replicaRebooting){
				// Ping UdpParser too see if replica is alive
				ReplicaAliveOperation replicaAliveOperation = new ReplicaAliveOperation(replicaPort);
				Packet packet = new Packet(pingSocket.getInetAddress(), pingSocket.getLocalPort(), Operation.REPLICA_ALIVE, replicaAliveOperation);
				byte[] message = UdpHelper.getByteArray(packet);
				DatagramPacket request = new DatagramPacket(message, message.length, address, replicaPort);
				pingSocket.send(request);
				
				// Receive UdpParser reply, return false if timeout
				byte[] buffer = new byte[BUFFER_SIZE];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				try{
					pingSocket.receive(reply);
					replicaRebooting = false;
				} catch(SocketTimeoutException e){
					// Keep trying
					++attempts;
					System.out.println("Reboot attempts: " + attempts);
					if (attempts >= Constants.MAX_TIMEOUT_ATTEMPTS){
						// Reboot cannot complete, send failure to FE
						ReplicaRebootReply replicaRebootReply = new ReplicaRebootReply(false);
						Packet replyPacket = new Packet(socket.getInetAddress(), socket.getLocalPort(), Operation.REPLICA_REBOOT, replicaRebootReply);
						
						byte[] failMessage = UdpHelper.getByteArray(replyPacket);
						DatagramPacket failReply = new DatagramPacket(failMessage, failMessage.length, address, port);
						socket.send(failReply);
						return;
					}
				}
			}
			
			int replicaManagerSequencerPort = replicaManager.getReplicaManagerSequencerPort();
			OperationLogOperation operationLogOperation = new OperationLogOperation(replicaManager.getPort());
			Packet operationLogPacket = new Packet(newSocket.getInetAddress(), newSocket.getLocalPort(), Operation.OPERATION_LOG, operationLogOperation);
			byte[] operationLogMessage = UdpHelper.getByteArray(operationLogPacket);
			DatagramPacket requestPacket = new DatagramPacket(operationLogMessage, operationLogMessage.length, address, replicaManagerSequencerPort);
			newSocket.send(requestPacket);
			
			// Receive reply from Sequencer
			byte[] buffer = new byte[BUFFER_SIZE];
			DatagramPacket sequencerReply = new DatagramPacket(buffer, buffer.length);
			newSocket.receive(sequencerReply);
			Packet sequencerReplyPacket = (Packet) UdpHelper.getObjectFromByteArray(sequencerReply.getData());
			OperationLogReply operationLogReply = (OperationLogReply) sequencerReplyPacket.getOperationParameters();
			ArrayList<Packet> operationLog = operationLogReply.getOperationLog();
			
			// TEST
			/*
			// Build the action for the packet
			BookFlightOperation bookFlightOperation = new BookFlightOperation.BuilderImpl("John").lastName("Doe")
					.address("Address").phoneNumber("PhoneNumber").destination("MTL|NDL").date("06/05/2016")
					.flightClass("FIRST").build();

			// Create a packet with the operation
			Packet testPacket = new Packet(newSocket.getInetAddress(), newSocket.getLocalPort(), Operation.BOOK_FLIGHT, bookFlightOperation);
			operationLog.add(testPacket);
			*/
			
			// Replica re-performs all operations in the log
			ExecuteOperationLogOperation executeOperationLogOperation = new ExecuteOperationLogOperation(operationLog);
			Packet executeOperationLogOperationPacket = new Packet(newSocket.getInetAddress(), newSocket.getLocalPort(), Operation.EXECUTE_OPERATION_LOG, executeOperationLogOperation);
			byte[] operationLogPayload = UdpHelper.getByteArray(executeOperationLogOperationPacket);
			DatagramPacket operationPacket = new DatagramPacket(operationLogPayload, operationLogPayload.length, address, replicaPort);
			newSocket.send(operationPacket);
			
			byte[] operationBuffer = new byte[BUFFER_SIZE];
			DatagramPacket operationPacketReply = new DatagramPacket(operationBuffer, operationBuffer.length);
			newSocket.receive(operationPacketReply);
			
			replicaManager.setRebooting(false);
			ReplicaRebootReply replicaRebootReply = new ReplicaRebootReply(result);
			Packet replyPacket = new Packet(socket.getInetAddress(), socket.getLocalPort(), Operation.REPLICA_REBOOT, replicaRebootReply);
			
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
			if (pingSocket != null) {
				pingSocket.close();
			}
		}
	}
}
