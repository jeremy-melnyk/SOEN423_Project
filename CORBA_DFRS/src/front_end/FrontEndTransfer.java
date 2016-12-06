package front_end;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import caio_replica.utils.Logger;
import front_end.failure_tracker.FailureTracker;
import packet.MulticastPacket;
import packet.Operation;
import packet.OperationParameters;
import packet.Packet;
import packet.ReplicaAliveOperation;
import packet.ReplicaAliveReply;
import packet.ReplicaRebootOperation;
import udp.UdpHelper;

public class FrontEndTransfer extends Thread {
	private DatagramSocket socket;
	private String correctReply;
	private Packet packet;
	private int sequencer;
	private List<Integer> group;
	private FailureTracker failureTracker;
	private HashMap<Integer, Integer> replicaTracker;
	private Logger logger;
	
	public FrontEndTransfer(DatagramSocket socket, Packet p, List<Integer> group, int sequencer, FailureTracker failureTracker, HashMap<Integer, Integer> replicaTracker) {
		this.socket = socket;
		this.correctReply = null;
		this.packet = p;
		this.group = group;
		this.sequencer =  sequencer;
		this.failureTracker = failureTracker;
		this.replicaTracker = replicaTracker;
		logger = new Logger("./logs/FRONT_END.log");
	}
	
	// For single Sender (RETRANSMITION)
	public FrontEndTransfer(DatagramSocket socket, Packet p, int receiverPort, int sequencer, FailureTracker failureTracker, HashMap<Integer, Integer> replicaTracker) {
		this.socket = socket;
		this.correctReply = null;
		this.packet = p;
		this.group = (new ArrayList<Integer>());
		this.group.add(receiverPort);
		this.sequencer =  sequencer;
		this.failureTracker = failureTracker;
		this.replicaTracker = replicaTracker;
		logger = new Logger("./logs/FRONT_END.log");
	}

	@Override
	public void run() {
		
		try {
			// Packet with group for sequencer
			MulticastPacket multicastPacket = new MulticastPacket(packet, group);			
			byte[] packetBytes = UdpHelper.getByteArray(multicastPacket);
			// Sequencer's Address
			InetAddress host = InetAddress.getLocalHost();
			DatagramPacket seq = new DatagramPacket(packetBytes, packetBytes.length, host, sequencer);
			long timerStart = System.currentTimeMillis();
			logger.log("FRONT END TRANSFER", "sending packet to sequencer");
			// Send to Sequencer
			socket.send(seq);
			byte buffer[] = new byte[5000];
			DatagramPacket p = new DatagramPacket(buffer, buffer.length);
			String seqACK = "";
			socket.setSoTimeout(2000);
			while(seqACK.isEmpty()){
				try{
				socket.receive(p);
				seqACK = (new String(p.getData())).trim();
				}catch(SocketTimeoutException e){
					socket.send(seq);
				}
			}
			// First Timeout 2 secs
			if(seqACK.equalsIgnoreCase("ACK")){
				HashMap<String, Integer> replies = new HashMap<String, Integer>();
				int counter = group.size();
				while(counter > 0 /* All replicas */){
					try{
						buffer = new byte[1000];
						DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
						socket.receive(reply);
						Packet replyPacket = (Packet) UdpHelper.getObjectFromByteArray(reply.getData());
						OperationParameters serverReply = replyPacket.getOperationParameters();
						logger.log("FRONT END TRANSFER", "Received Packet: "+serverReply.toString());
						if(replyPacket.getReplicaOperation() == Operation.REPLICA_ALIVE){
							ReplicaAliveReply replicaAlive = (ReplicaAliveReply) serverReply;
							if(!replicaAlive.isAlive())
								counter--; // Move on, RM and sequencer will take care
							else{}
								// TODO Retransmition
						} else if(replyPacket.getReplicaOperation() == Operation.REPLICA_REBOOT){
							// Necessary?
						}else{	// Replica Reply
							group.remove(new Integer(replyPacket.getSenderPort()));
							long timeReceived = System.currentTimeMillis();
							if(replies.containsKey(serverReply.toString())){	// If same reply was received before
								int newVal = replies.replace(serverReply.toString(), replies.get(serverReply.toString())+1);
								if(++newVal == 2)	// If 2 of the same, set as correct reply
									correctReply = serverReply.toString();
								logger.log("FRONT END TRANSFER", "Correct reply found");
							} else{				// 1st time seeing reply
								// If correct reply was found, set this different as incorrect reply
								if(this.hasCorrectReply()){
									int numberFailures = failureTracker.insertFailure(replyPacket.getSenderPort());
									if(numberFailures >= 3){		// Reboot Replica
										// Get RM's Address
										logger.log("FRONT END TRANSFER", "replica failure: Communicating RM");
										int RM = 0;
										for(Map.Entry<Integer, Integer> entry: replicaTracker.entrySet()){
											if(entry.getValue() == replyPacket.getSenderPort()){
												RM = entry.getKey();
												break;
											}
										}
										// Send reboot request
										ReplicaRebootOperation rebootRequest = new ReplicaRebootOperation();
										Packet packet = new Packet(Operation.REPLICA_REBOOT, rebootRequest);
										byte[] replicaRequest = UdpHelper.getByteArray(packet);
										DatagramPacket replicaPacket = new DatagramPacket(replicaRequest, replicaRequest.length, InetAddress.getLocalHost(), RM);
										socket.send(replicaPacket);
									}
								}
								// Add to replies structure
								replies.put(serverReply.toString(), 1);
							}
							counter--;
							// Timeout set for 2x the latest packet
						}
					}catch(SocketTimeoutException e){	// Timeout
						// SEND TO RM THAT REPLICA MIGHT HAVE CRASHED
						// Every remaining replica in the group
						logger.log("FRONT END TRANSFER", "REQUESTING REPLICA ALIVE SIGNAL FROM RM");
						
						for(int replicaPort : group){
							int RM = 0;
							for(Map.Entry<Integer, Integer> entry: replicaTracker.entrySet()){
								if(entry.getValue() == replicaPort){
									RM = entry.getKey();
									break;
								}
							}
							ReplicaAliveOperation aliveRequest = new ReplicaAliveOperation(replicaPort);
							Packet packet = new Packet(Operation.REPLICA_ALIVE, aliveRequest);
							byte[] aliveBytes = UdpHelper.getByteArray(packet);
							DatagramPacket dPac = new DatagramPacket(aliveBytes, aliveBytes.length, host, RM);
							socket.send(dPac);
							
							// @Caio I made a new REPLICA_CRASH request. The RM you send it to will ask other RMs to check
							// alive status of the replica. They then reach a consensus and reboot if necessary.
							// It returns a replica alive reply with true (if replica was crashed) and false (if it was OK).
							// It triggers reboot automatically (resetting logs) if it was crashed, you'll know if it's back online with a normal replica alive ping
							// elsewhere.
							/*
							ReplicaAliveOperation aliveRequest = new ReplicaAliveOperation(replicaPort);
							Packet packet = new Packet(Operation.REPLICA_CRASH, aliveRequest);
							byte[] aliveBytes = UdpHelper.getByteArray(packet);
							DatagramPacket dPac = new DatagramPacket(aliveBytes, aliveBytes.length, host, RM);
							socket.send(dPac);
							*/
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
