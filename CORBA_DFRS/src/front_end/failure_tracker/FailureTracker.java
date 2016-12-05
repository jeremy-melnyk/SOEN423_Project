package front_end.failure_tracker;

import java.net.InetAddress;
import java.util.HashMap;
/**
 * 
 * @author Caio Paiva
 * 
 * Class Failure Trackers keeps track of replica's incorrect replies
 *
 */
public class FailureTracker {
	HashMap<Integer, Integer> fails;
	
	public FailureTracker(){
		fails = new HashMap<Integer, Integer>();
	}
	
	public synchronized int insertFailure(int port){
		if(fails.containsKey(port)){
			return fails.replace(port, fails.get(port)+1);
		}else{
			return fails.put(port, 1);
		}
	}

}
