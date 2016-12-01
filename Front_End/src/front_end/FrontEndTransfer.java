package front_end;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import failure_tracker.FailureTracker;
import packet.MulticastPacket;
import packet.Packet;
import replica_manager_packet.ReplicaAliveOperation;
import replica_manager_packet.ReplicaManagerOperation;
import replica_manager_packet.ReplicaManagerPacket;
import replica_manager_packet.ReplicaRebootOperation;
import udp.UdpHelper;

public class FrontEndTransfer extends Thread {
	private DatagramSocket socket;
	private String correctReply;
	private Packet packet;
	private String sequencerAdress;
	private List<Integer> group;
	private FailureTracker failureTracker;
	private HashMap<String, String> replicaTracker;
	
	public FrontEndTransfer(DatagramSocket socket, Packet p, List<Integer> group, String sequencer, FailureTracker failureTracker, HashMap<String, String> replicaTracker) {
		this.socket = socket;
		this.correctReply = null;
		this.packet = p;
		this.group = group;
		this.sequencerAdress =  sequencer;
		this.failureTracker = failureTracker;
		this.replicaTracker = replicaTracker;
	}
	
	// For single Sender (RETRANSMITION)
	public FrontEndTransfer(DatagramSocket socket, Packet p, int receiverPort, String sequencer, FailureTracker failureTracker, HashMap<String, String> replicaTracker) {
		this.socket = socket;
		this.correctReply = null;
		this.packet = p;
		this.group = (new ArrayList<Integer>());
		this.group.add(receiverPort);
		this.sequencerAdress =  sequencer;
		this.failureTracker = failureTracker;
		this.replicaTracker = replicaTracker;
	}

	@Override
	public void run() {
		
		try {
			// Packet with group for sequencer
			MulticastPacket multicastPacket = new MulticastPacket(packet, group);			
			byte[] packetBytes = UdpHelper.getByteArray(multicastPacket);
			// Sequencer's Address
			URL url = new URL(sequencerAdress);
			InetAddress host = InetAddress.getByName(url.getHost());
			DatagramPacket seq = new DatagramPacket(packetBytes, packetBytes.length, host, url.getPort());
			long timerStart = System.currentTimeMillis();
			// Send to Sequencer
			socket.send(seq);
			byte buffer[] = new byte[100];
			DatagramPacket p = new DatagramPacket(buffer, buffer.length);
			// TODO TIMEOUT RETRANSMIT
			socket.receive(p);
			String seqACK = (new String(p.getData())).trim();
			// First Timeout 5 secs
			socket.setSoTimeout(5000);
			if(true/* TODO GOOD */){
				HashMap<String, Integer> replies = new HashMap<String, Integer>();
				int counter = group.size();
				while(counter > 0 /* All replicas */){
					try{
						buffer = new byte[1000];
						DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
						socket.receive(reply);
						// Check TYPE for same operation or RM reply
						// if RM
							// if true (Replica alive) resend
							// if false, counter-- and move on
						// else if TYPE == Sent TYPE
						//Remove from group
						group.remove(reply.getPort());
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
								int numberFailures = failureTracker.insertFailure(reply.getAddress(), reply.getPort());
								if(numberFailures >= 3){
									// Get RM's Address
									String RM = "";
									for(Map.Entry<String, String> entry: replicaTracker.entrySet()){
										if(entry.getValue().equalsIgnoreCase(reply.getAddress()+":"+reply.getPort())){
											RM = entry.getKey();
											break;
										}
									}
									URL replicaURL = new URL(RM);
									// Send reboot request
									ReplicaRebootOperation rebootRequest = new ReplicaRebootOperation();
									ReplicaManagerPacket packet = new ReplicaManagerPacket(ReplicaManagerOperation.REPLICA_REBOOT, rebootRequest);
									byte[] replicaRequest = UdpHelper.getByteArray(packet);
									DatagramPacket replicaPacket = new DatagramPacket(replicaRequest, replicaRequest.length, InetAddress.getByName(replicaURL.getHost()), replicaURL.getPort());
									socket.send(replicaPacket);
								}
							}
							// Add to replies structure
							replies.put(serverReply, 1);
						}
						counter--;
						// Timeout set for 2x the latest packet
						socket.setSoTimeout((int)(timeReceived - timerStart* 2));
					}catch(SocketTimeoutException e){
						// SEND TO RM THAT REPLICA MIGHT HAVE CRASHED
						// Every remaining replica in the group
						for(int replicaPort : group){
							ReplicaAliveOperation aliveRequest = new ReplicaAliveOperation(replicaPort);
							ReplicaManagerPacket packet = new ReplicaManagerPacket(ReplicaManagerOperation.REPLICA_ALIVE, aliveRequest);
							byte[] aliveBytes = UdpHelper.getByteArray(packet);
							DatagramPacket dPac = new DatagramPacket(aliveBytes, aliveBytes.length, host, replicaPort);
							socket.send(dPac);
						}
					}
				}
			} /*else{
				// TODO RESEND to sequencer
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
