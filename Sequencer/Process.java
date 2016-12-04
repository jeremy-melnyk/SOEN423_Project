package Sequencer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import packet.Packet;

public class Process {

	private int lastreceivednumber=0;
	private Set<Integer> setofreceivednumbers=new HashSet<Integer>();
	private PriorityQueue<Packet> holdbackqueue=new PriorityQueue<Packet>(new PQSort());
	private int replicaportnumber;
	private final int groupportnumber=9876;
	
	
	public int getReplicaportnumber() {
		return replicaportnumber;
	}
	public void setReplicaportnumber(int replicaportnumber) {
		this.replicaportnumber = replicaportnumber;
	}
	public int getLastreceivednumber() {
		return lastreceivednumber;
	}
	public synchronized void setLastreceivednumber(int lastreceivednumber) {
		this.lastreceivednumber = lastreceivednumber;
	}
	
public void multicastToGroup(Packet packet){

		
		MulticastSocket aSocket=null;
		try {
			
			//creates MulticastSocket with InetAddress and ServerPort
			aSocket=new MulticastSocket();
			InetAddress aGroup=InetAddress.getByName("localhost");
			
			//join group
			aSocket.joinGroup(aGroup);
			
			//converts packet to send to group
			byte[] m=UdpHelper.getByteArray(packet);
			DatagramPacket request =new DatagramPacket(m,  UdpHelper.getByteArray(packet).length, aGroup, groupportnumber);
			
			//sends packet
			aSocket.send(request);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {if(aSocket != null) aSocket.close();}
	}
	
	public void deliverMulticast(Packet receivedpacket){
		
			
			//check if this is a duplicate packet
			int receivedseqnumber=receivedpacket.getSequencernumber();
			
			if(this.isDuplicate(receivedseqnumber)==false){
				
				//delivers to the rest of the group to ensure multicast reliability
				this.multicastToGroup(receivedpacket);
				
				
				//adds to holdbackqueue
				holdbackqueue.offer(receivedpacket);
				
				//continuously delivers until holdback queue is empty or there is a missing packet
				while (this.checkHoldBackQueue()==true){
					
					//pops the latest packet from the queue and increments the last received number
					Packet deliveredpacket=holdbackqueue.poll();
					this.setLastreceivednumber(deliveredpacket.getSequencernumber());
					
					///INSERT DELIVERY OR EXECUTION OF deliverpacket HERE///
				}
				
				
				
				
				
			}
			
			
		}
		
	
	public boolean isDuplicate(int seqnumber){
		if (setofreceivednumbers.contains(seqnumber)){
			return true;
		}
		else {
			setofreceivednumbers.add(seqnumber);
			return false;
		}
	}
	
	//checks if the next correct packet is within the holdbackqueue
	public boolean checkHoldBackQueue(){
		int receivedseqnumber=holdbackqueue.peek().getSequencernumber();
		if (receivedseqnumber==this.getLastreceivednumber()){
			return true;
		}
		else{
			return false;
		}
	}
	//for comparing packets in the priority queue
	static class PQSort implements Comparator<Packet> {
		
		public int compare(Packet one, Packet two){
			return one.getSequencernumber()-two.getSequencernumber();
		}
	}
	
	//receives
	@SuppressWarnings("unchecked")
	public void requestSequencerLog(){
		DatagramSocket aSocket= null;
		
		try{
			//creates socket to receive sequencer log of packets
			aSocket=new DatagramSocket(replicaportnumber);
			byte[] buffer=new byte[1000];
			DatagramPacket request=new DatagramPacket(buffer,buffer.length);
			
			//receive sequencer log of packets
			aSocket.receive(request);
			
			
			//converts to List<packet> from byte array
			List<Packet>receivedseqlog=null;
			
			ByteArrayInputStream bis = new ByteArrayInputStream(request.getData());
			ObjectInput in = null;
			try {
			  in = new ObjectInputStream(bis);
			  receivedseqlog = (List<Packet>) in.readObject(); 
			} finally {
			  try {
			    if (in != null) {
			      in.close();
			    }
			  } catch (IOException ex) {
			    // ignore close exception
			  }
			}
			
			//adds list<packet> to priority holdback queue
			for (int i=0;i<receivedseqlog.size();i++){
				holdbackqueue.offer(receivedseqlog.get(i));
			}
			
			//sends replicamanager an ACK that it has received the sequencer logs and has successfully added them to the holdback queue
			request.setData("ACK".getBytes());
			
			DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), 
	    				request.getAddress(), request.getPort());
	    	aSocket.send(reply);
			
			
			
		}
		catch (Exception e){
			
		}
		
		
	}
	
	public void UDPServer(){
	
		DatagramSocket aSocket=null;
		MulticastSocket mSocket=null;
		try{
			aSocket= new DatagramSocket(replicaportnumber);
			
			mSocket= new MulticastSocket(groupportnumber);
			InetAddress aGroup=InetAddress.getByName("localhost");
			mSocket.joinGroup(aGroup);
			
			
			aSocket.setSoTimeout(5000);
			while (true){
				
				byte[] buffer= new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				
				mSocket.receive(request);
				
				
				//unserialize byte array and transform back into a Packet class
				Packet receivedpacket;
				
				ByteArrayInputStream bis = new ByteArrayInputStream(request.getData());
				ObjectInput in = null;
				try {
				  in = new ObjectInputStream(bis);
				  receivedpacket = (Packet) in.readObject(); 
				} finally {
				  try {
				    if (in != null) {
				      in.close();
				    }
				  } catch (IOException ex) {
				    // ignore close exception
				  }
				}
				
				//delivers packet
				this.deliverMulticast(receivedpacket);
				
				
				
			}
		}
		catch (Exception e){
			
		}
	}
	
	
}
