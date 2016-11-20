package front_end;

import packet.Packet;

public class FrontEndTransfer extends Thread {
	private String correctReply;
	private Packet packet;
	private String sequencerAdress;
	private String[] group;
	public FrontEndTransfer(Packet p, String[] group, String sequencer) {
		correctReply = null;
		this.packet = p;
		this.group = group;
		this.sequencerAdress =  sequencer;
	}

	@Override
	public void run() {
	}
	
	public String getCorrectReply(){
		return correctReply;
	}
}
