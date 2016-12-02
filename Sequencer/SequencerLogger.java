package Sequencer;

import java.io.*;

public class SequencerLogger {

	private String filelocation="SEQUENCER_LOG";
	
	public SequencerLogger(){
		
	}
	
	public synchronized void log(Packet packet){
		try {
			OutputStream file= new FileOutputStream(filelocation+".txt");
			OutputStream buffer= new BufferedOutputStream(file);
			ObjectOutput output=new ObjectOutputStream(buffer);
			
			output.writeObject(packet);
			
			output.close();
			buffer.close();
			file.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
	
}
