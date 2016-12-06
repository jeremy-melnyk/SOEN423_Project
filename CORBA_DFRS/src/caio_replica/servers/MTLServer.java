package caio_replica.servers;

import caio_replica.models.FlightRecord;

public class MTLServer extends FlightServerInterfaceImpl {
	
	public MTLServer(int port){
		super("mtl", port);		
	}
	
	public void createInitialRecord(FlightRecord f){
	}

}
