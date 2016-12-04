package udp_parser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import packet.OperationParameters;
import packet.Packet;
import udp.UdpHelper;

public class UdpParserPacketDispatcher implements Runnable {
	private final UdpParserBase udpParser;
	private final DatagramPacket packet;

	public UdpParserPacketDispatcher(UdpParserBase udpParser, DatagramPacket packet) {
		super();
		this.udpParser = udpParser;
		this.packet = packet;
	}

	@Override
	public void run() {
		handlePacket();
	}
	
	private void handlePacket() {
		Packet replicaPacket = null;
		replicaPacket = (Packet)UdpHelper.getObjectFromByteArray(this.packet.getData());
		if(replicaPacket != null){
			OperationParameters operationParameters = this.udpParser.processPacket(replicaPacket);
			sendReply(operationParameters);
		}
	}
	
	private void sendReply(OperationParameters operationParameters){
		DatagramSocket newSocket = null;
		try {
			newSocket = new DatagramSocket();
			byte[] message = UdpHelper.getByteArray(operationParameters);
			DatagramPacket reply = new DatagramPacket(message, message.length, packet.getAddress(), packet.getPort());
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
