package caio_replica.servers;


import caio_replica.models.FlightRecord;

public class NDLServer extends FlightServerInterfaceImpl {
	
	public NDLServer(int port){
		super("ndl", port);
	}
	
	public void createInitialRecord(FlightRecord f){
		f.add("ndl", "wst", "2016/10/03", 30, 10, 5);
		f.add("ndl", "mtl", "2016/10/03", 30, 10, 5);
		f.add("ndl", "wst", "2016/10/04", 30, 10, 5);
		f.add("ndl", "mtl", "2016/10/04", 30, 10, 5);
		f.add("ndl", "wst", "2016/10/05", 30, 10, 5);
		f.add("ndl", "mtl", "2016/10/05", 30, 10, 5);
		f.add("ndl", "wst", "2016/12/24", 30, 10, 5);
	}

}
