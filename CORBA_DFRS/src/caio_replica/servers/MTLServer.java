package caio_replica.servers;

import caio_replica.models.FlightRecord;

public class MTLServer extends FlightServerInterfaceImpl {
	
	public MTLServer(int port){
		super("mtl", port);		
	}
	
	public void createInitialRecord(FlightRecord f){
		f.add("mtl", "iad", "2016/10/03", 10, 10, 15);
		f.add("mtl", "del", "2016/10/03", 10, 10, 15);
		f.add("mtl", "iad", "2016/10/04", 10, 10, 15);
		f.add("mtl", "del", "2016/10/04", 10, 10, 15);
		f.add("mtl", "iad", "2016/10/05", 10, 10, 15);
		f.add("mtl", "del", "2016/10/05", 10, 10, 15);
	}

}
