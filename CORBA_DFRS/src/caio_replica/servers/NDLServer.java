package caio_replica.servers;


import caio_replica.models.FlightRecord;

public class NDLServer extends FlightServerInterfaceImpl {
	
	public NDLServer(int port){
		super("ndl", port);
	}
	
	public void createInitialRecord(FlightRecord f){
	}

}
