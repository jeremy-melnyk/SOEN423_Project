package packet;

import java.io.Serializable;
import java.util.List;

public class MulticastPacket implements Serializable{
	private static final long serialVersionUID = 1L;
	private Packet p;
	private List<Integer> group;
	
	
	public MulticastPacket(Packet p, List<Integer> group) {
		super();
		this.p = p;
		this.group = group;
	}

	public Packet getP() {
		return p;
	}


	public void setP(Packet p) {
		this.p = p;
	}


	public List<Integer> getGroup() {
		return group;
	}


	public void setGroup(List<Integer> group) {
		this.group = group;
	}

}
