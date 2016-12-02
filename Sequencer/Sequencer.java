package Sequencer;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;


public class Sequencer {

	private int sequencerport=5678;
	private int sequencernumber=1;
	private ArrayList<Packet> sequencerlog=new ArrayList<Packet>();
	
	public Sequencer(String message){
	}
	
	public int getSequencernumber() {
		return sequencernumber;
	}

	public synchronized void setSequencernumber(int sequencernumber) {
		this.sequencernumber = sequencernumber;
	}

	
	public void multicastToGroup(Packet packet){

		
		MulticastSocket aSocket=null;
		try {
			//piggybacks sequencer number to packet 
			packet.setSequencernumber(this.sequencernumber);
			
			//creates MulticastSocket with InetAddress and ServerPort
			aSocket=new MulticastSocket();
			InetAddress aGroup=InetAddress.getByName("localhost");
			int  serverPort=9876;
			
			//join group
			aSocket.joinGroup(aGroup);
			
			//converts packet to send to group
			byte[] m=packet.getByteArray();
			DatagramPacket request =new DatagramPacket(m,  packet.getByteArray().length, aGroup, serverPort);
			
			synchronized(this){
				//sends packet
				aSocket.send(request);
				//sequencer logs serialized packet 
				this.logPacket(packet);
				
				//sequencer increments number
				this.setSequencernumber(this.getSequencernumber()+1);
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {if(aSocket != null) aSocket.close();}
	}
	
	public void logPacket(Packet packet){
		sequencerlog.add(packet);
	}
	
	//sends the log of all operations to the request replica once it has received the replica port
	public void sendLog(int replicaPort){
		DatagramSocket aSocket=null;
		try{
			//create socket
			aSocket = new DatagramSocket(); 
			
			//convert arraylist to serializable object
			ByteArrayOutputStream bao= new ByteArrayOutputStream();
			ObjectOutputStream oos= new ObjectOutputStream(bao);
			oos.writeObject(sequencerlog);
			oos.close();
			byte [] m =bao.toByteArray();
			
			
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket sendsequencerlogs= new DatagramPacket(m,bao.toByteArray().length,aHost,replicaPort);
		}
		catch (Exception e){
			
		}
	}
	
	public void UDPServer(){
		DatagramSocket aSocket = null;
		try{
	    	aSocket = new DatagramSocket(sequencerport);
			
	    	while (true){
	    		//create packet and receive message
	    		byte[] buffer = new byte[1000];
 				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
  				aSocket.receive(request);
  				
  				//message sent to sequencer to do things is a string
  				Packet frontendpacket;
  				
  				ByteArrayInputStream bis = new ByteArrayInputStream(request.getData());
  				ObjectInput in = null;
  				try {
  				  in = new ObjectInputStream(bis);
  				  frontendpacket = (Packet) in.readObject(); 
  				} catch (ClassCastException e){
  					
  				} 
  				
  				finally {
  				  try {
  				    if (in != null) {
  				      in.close();
  				    }
  				  } catch (IOException ex) {
  				    // ignore close exception
  				  }
  				}
  				
  				
	    	}
	    	
		}
		catch (Exception e){
			
		}
	}
	
	
}
