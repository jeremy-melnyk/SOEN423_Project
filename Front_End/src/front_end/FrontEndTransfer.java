package front_end;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;

import failure_tracker.FailureTracker;
import packet.Packet;

public class FrontEndTransfer extends Thread {
	private DatagramSocket socket;
	private String correctReply;
	private Packet packet;
	private String sequencerAdress;
	private String[] group;
	private FailureTracker failuretracker;
	
	public FrontEndTransfer(DatagramSocket socket, Packet p, String[] group, String sequencer, FailureTracker failureTracker) {
		this.socket = socket;
		this.correctReply = null;
		this.packet = p;
		this.group = group;
		this.sequencerAdress =  sequencer;
		this.failuretracker = failureTracker;
	}
	
	// For single Sender (RETRANSMITION)
	public FrontEndTransfer(DatagramSocket socket, Packet p, String receiver, String sequencer, FailureTracker failureTracker) {
		this.socket = socket;
		this.correctReply = null;
		this.packet = p;
		this.group = (new String[1]);
		this.group[0] = receiver;
		this.sequencerAdress =  sequencer;
		this.failuretracker = failureTracker;
	}

	@Override
	public void run() {
		
		try {
			byte[] packetBytes = packet.getByteArray();
			URL url = new URL(sequencerAdress);
			InetAddress host = InetAddress.getByName(url.getHost());
			DatagramPacket seq = new DatagramPacket(packetBytes, packetBytes.length, host, url.getPort());
			// TODO Send Group to sequencer too
			long timerStart = System.currentTimeMillis();
			// Send to Sequencer
			socket.send(seq);
			byte buffer[] = new byte[100];
			DatagramPacket p = new DatagramPacket(buffer, buffer.length);
			// TIMEOUT RETRANSMIT
			socket.receive(p);
			String seqACK = (new String(p.getData())).trim();
			// First Timeout 5 secs
			socket.setSoTimeout(5000);
			if(true/*  GOOD */){
				HashMap<String, Integer> replies = new HashMap<String, Integer>();
				// Missing timer
				int counter = group.length;
				while(counter > 0 /* All replicas */){
					try{
						buffer = new byte[1000];
						DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
						socket.receive(reply);
						// Check TYPE for same operation or RM reply
						// if RM
							// if true (Replica alive) resend
							// if false, couter-- and move on
						// else if TYPE == Sent TYPE
						long timeReceived = System.currentTimeMillis();
						// TODO Response Packet
						String serverReply = (new String(reply.getData()));
						if(replies.containsKey(serverReply)){
							int newVal = replies.replace(serverReply, replies.get(serverReply)+1);
							if(newVal == 2)
								correctReply = serverReply;
						} else{
							// If correct reply was found
							if(this.hasCorrectReply()){
								int numberFailures = failuretracker.insertFailure(reply.getAddress(), reply.getPort());
								if(numberFailures >= 3){
									// SEND TO INCORRECT TO RM
										// Get incorrect replica's adrress
										// Send to RM
								}
							}
							replies.put(serverReply, 1);
						}
						counter--;
						// Timeout set for 2x the lastest packet
						socket.setSoTimeout((int)(timeReceived - timerStart* 2));
					}catch(SocketTimeoutException e){
						// SEND TO RM THAT REPLICA MIGHT HAVE CRASHED
							// Get Crashed Address
							// Send to RM
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
