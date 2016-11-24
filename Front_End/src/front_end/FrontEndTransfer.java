package front_end;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;

import failure_tracker.FailureTracker;
import packet.Packet;

public class FrontEndTransfer extends Thread {
	private DatagramSocket socket;
	private String correctReply;
	private Packet packet;
	private int sequencerAdress;
	private String[] group;
	private FailureTracker failuretracker;
	
	public FrontEndTransfer(DatagramSocket socket, Packet p, String[] group, int sequencer, FailureTracker failureTracker) {
		this.socket = socket;
		this.correctReply = null;
		this.packet = p;
		this.group = group;
		this.sequencerAdress =  sequencer;
		this.failuretracker = failureTracker;
	}

	@Override
	public void run() {
		
		try {
			byte[] packetBytes = packet.getByteArray();
			InetAddress host = InetAddress.getByName("localhost");
			DatagramPacket seq = new DatagramPacket(packetBytes, packetBytes.length, host, sequencerAdress);
			// TODO Send Group to sequencer too
			long timerStart = System.currentTimeMillis();
			socket.send(seq);
			byte buffer[] = new byte[100];
			DatagramPacket p = new DatagramPacket(buffer, buffer.length);
			// TIMEOUT RETRANSMIT
			socket.receive(p);
			String seqACK = (new String(p.getData())).trim();
			socket.setSoTimeout(5000);
			if(true/*  GOOD */){
				HashMap<String, Integer> replies = new HashMap<String, Integer>();
				// Missing timer
				while(true /* All replicas */){
					try{
					buffer = new byte[1000];
					DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
					socket.receive(reply);
					long timeReceived = System.currentTimeMillis();
					String serverReply = (new String(reply.getData()));
					// Missing RM Communication
					if(replies.containsKey(serverReply)){
						int newVal = replies.replace(serverReply, replies.get(serverReply)+1);
						if(newVal == 2)
							correctReply = serverReply;
					} else{
						// If correct reply was found
						if(this.hasCorrectReply()){
							int numberFailures = failuretracker.insertFailure(reply.getAddress(), reply.getPort());
							if(numberFailures >= 3){
								// SEND TO RM
							}
						}
						replies.put(serverReply, 1);
					}
					// Timeout set for 2x the lastest packet
					socket.setSoTimeout((int)(timeReceived - timerStart* 2));
					}catch(SocketTimeoutException e){
						// SEND TO RM THAT REPLICA MIGHT HAVE CRASHED
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
	
	public boolean hasCorrectReply(){
		return (correctReply == null ? false : true);
	}
	
	public String getCorrectReply(){
		return correctReply;
	}
}
