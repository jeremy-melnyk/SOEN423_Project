package udp_parser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
		InetAddress address = packet.getAddress();
		int port = packet.getPort();
		Packet replicaPacket = null;
		replicaPacket = (Packet)UdpHelper.getObjectFromByteArray(packet.getData());
		if(replicaPacket != null){
			Packet replicaReplyPacket = this.udpParser.processPacket(replicaPacket);
			sendReply(replicaReplyPacket, address, port);
		}
	}
	
	private void sendReply(Packet replicaReplyPacket, InetAddress address, int port){
		DatagramSocket newSocket = null;
		try {
			newSocket = new DatagramSocket();
			byte[] message = UdpHelper.getByteArray(replicaReplyPacket);
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
