package Sequencer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class Process {

	private int lastreceivednumber=0;
	private Set<Integer> setofreceivednumbers=new HashSet<Integer>();
	private PriorityQueue<Packet> holdbackqueue=new PriorityQueue<Packet>(new PQSort());
	
	
	
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
			int  serverPort=9876;
			
			//join group
			aSocket.joinGroup(aGroup);
			
			//converts packet to send to group
			byte[] m=packet.getByteArray();
			DatagramPacket request =new DatagramPacket(m,  packet.getByteArray().length, aGroup, serverPort);
			
			//sends packet
			aSocket.send(request);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {if(aSocket != null) aSocket.close();}
	}
	
	public void deliverMulticast(){
		MulticastSocket aSocket=null;
		try{
			//set socket and join group
			aSocket= new MulticastSocket(9876);
			InetAddress aGroup=InetAddress.getByName("localhost");
			aSocket.joinGroup(aGroup);
			
			//create packet to receive information
			byte[] buffer = new byte[1000];
			DatagramPacket request=new DatagramPacket(buffer, buffer.length);
			
			//receives information
			aSocket.receive(request);
			
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
		catch (Exception e){
			e.printStackTrace();
		}
		finally {if(aSocket != null) aSocket.close();}
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
	
	//resends all the packets 
	public void requestsequencerlogs(){
		DatagramSocket aSocket= null;
		
		try{
			aSocket=new DatagramSocket();
			
			byte[] m=
		}
		catch (Exception e){
			
		}
		
		
	}
	
	
}
