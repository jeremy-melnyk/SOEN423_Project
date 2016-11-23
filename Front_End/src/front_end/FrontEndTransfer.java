package front_end;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import packet.Packet;

public class FrontEndTransfer extends Thread {
	private DatagramSocket socket;
	private String correctReply;
	private Packet packet;
	private int sequencerAdress;
	private String[] group;
	
	public FrontEndTransfer(DatagramSocket socket, Packet p, String[] group, int sequencer) {
		this.socket = socket;
		this.correctReply = null;
		this.packet = p;
		this.group = group;
		this.sequencerAdress =  sequencer;
	}

	@Override
	public void run() {
		
		try {
			byte[] packetBytes = packet.getByteArray();
			InetAddress host = InetAddress.getByName("localhost");
			DatagramPacket seq = new DatagramPacket(packetBytes, packetBytes.length, host, sequencerAdress);
			socket.send(seq);
			byte buffer[] = new byte[100];
			DatagramPacket p = new DatagramPacket(buffer, buffer.length);
			// TIMEOUT RETRANSMIT
			socket.receive(p);
			String seqACK = (new String(p.getData())).trim();
			if(true/*  GOOD */){
				HashMap<String, Integer> replies = new HashMap<String, Integer>();
				// Missing timer
				while(true /* All replicas */){
					buffer = new byte[1000];
					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
					socket.receive(reply);
					String serverReply = (new String(reply.getData()));
					// Missing RM Communication
					if(replies.containsKey(serverReply)){
						int newVal = replies.replace(serverReply, replies.get(serverReply)+1);
						if(newVal == 2)
							correctReply = serverReply;
					} else{
						// If correct reply was not found yet
						replies.put(serverReply, 1);
						// else
						// set failure to tracker
						// If return >= 3 contact RM
					}
				}
			} /*else{
				// RESEND
			}*/
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}				
	}
	
	public String getCorrectReply(){
		return correctReply;
	}
}
