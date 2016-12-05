package udp_parser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

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
			Packet replicaReplyPacket = this.udpParser.processPacket(replicaPacket);
			sendReply(replicaReplyPacket);
		}
	}
	
	private void sendReply(Packet replicaReplyPacket){
		DatagramSocket newSocket = null;
		try {
			newSocket = new DatagramSocket();
			byte[] message = UdpHelper.getByteArray(replicaReplyPacket);
			DatagramPacket reply = new DatagramPacket(message, message.length, packet.getAddress(), packet.getPort());
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
