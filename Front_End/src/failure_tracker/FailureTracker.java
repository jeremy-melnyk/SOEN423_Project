package failure_tracker;

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
	HashMap<String, Integer> fails;
	
	public FailureTracker(){
		fails = new HashMap<String, Integer>();
	}
	
	public synchronized int insertFailure(InetAddress address, int port){
		String socketAddress =  address.toString()+port;
		if(fails.containsKey(socketAddress)){
			return fails.replace(socketAddress, fails.get(socketAddress)+1);
		}else{
			return fails.put(socketAddress, 1);
		}
	}

}
