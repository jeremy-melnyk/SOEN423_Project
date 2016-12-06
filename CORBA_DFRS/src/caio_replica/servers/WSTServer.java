package caio_replica.servers;

import caio_replica.models.FlightRecord;

public class WSTServer extends FlightServerInterfaceImpl {

	public WSTServer(int port){
		super("wst", port);
	}
	
	public void createInitialRecord(FlightRecord f){
	}

}
