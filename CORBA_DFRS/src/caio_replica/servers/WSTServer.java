package caio_replica.servers;

import caio_replica.models.FlightRecord;

public class WSTServer extends FlightServerInterfaceImpl {

	public WSTServer(int port){
		super("wst", port);
	}
	
	public void createInitialRecord(FlightRecord f){
		f.add("wst", "mtl", "2016/10/03", 30, 10, 5);
		f.add("wst", "ndl", "2016/10/03", 30, 10, 5);
		f.add("wst", "mtl", "2016/10/04", 30, 10, 5);
		f.add("wst", "ndl", "2016/10/04", 30, 10, 5);
		f.add("wst", "mtl", "2016/10/05", 30, 10, 5);
		f.add("wst", "ndl", "2016/10/05", 30, 10, 5);
	}

}
