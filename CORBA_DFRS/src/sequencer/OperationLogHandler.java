package sequencer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import packet.Operation;
import packet.OperationLogReply;
import packet.OperationParameters;
import packet.OperationParametersHandler;
import packet.Packet;
import udp.UdpHelper;

public class OperationLogHandler extends OperationParametersHandler {
	private Sequencer sequencer;

	public OperationLogHandler(DatagramSocket socket, InetAddress address, int port, OperationParameters operationParameters, Sequencer sequencer) {
		super(socket, address, port, operationParameters);
		this.sequencer = sequencer;
	}

	@Override
	public void execute() {
		DatagramSocket newSocket = null;
		try {
			newSocket = new DatagramSocket();
			
			// Get sequencer operation log
			// TODO : OperationLogOperation not really necessary
			//OperationLogOperation operationLogOperation = (OperationLogOperation) operationParameters;
			ArrayList<Packet> operationLog = sequencer.getSequencerLog();
			OperationLogReply operationLogReply = new OperationLogReply(operationLog);
			Packet replyPacket = new Packet(newSocket.getInetAddress(), newSocket.getLocalPort(), Operation.OPERATION_LOG, operationLogReply);
			
			// Reply to Replica Manager
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
