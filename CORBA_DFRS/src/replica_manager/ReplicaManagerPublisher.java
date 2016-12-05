package replica_manager;

import java.util.HashMap;
import java.util.Scanner;

import jeremy_replica.log.CustomLogger;
import jeremy_replica.log.TextFileLog;

import java.util.Map.Entry;

import json.JSONReader;

public class ReplicaManagerPublisher {
	private static final String JSON_CONFIG_PATH = "port_config.json";
	private static final String REPLICA_1_PATH = "java -classpath json-simple-1.1.jar;bin jeremy_replica.server.PublishingServer";
	private static final String REPLICA_2_PATH = "java -classpath json-simple-1.1.jar;bin servers.CaioPublisher";
	private static final String REPLICA_3_PATH = "java -classpath json-simple-1.1.jar;bin server.FlightReservationServerPublisher";
	
	public static void main(String[] args) {
		JSONReader jsonReader = new JSONReader(JSON_CONFIG_PATH);
		jsonReader.initialize();
		
		// TODO : Add Patrick's replica
		
		int rm_1_port = jsonReader.getPortForKeys("Jeremy", "RM");
		ReplicaManager rm1 = new ReplicaManager(rm_1_port, REPLICA_1_PATH, new CustomLogger(new TextFileLog()));
		
		int rm_2_port = jsonReader.getPortForKeys("Caio", "RM");
		ReplicaManager rm2 = new ReplicaManager(rm_2_port, REPLICA_2_PATH, new CustomLogger(new TextFileLog()));
		
		int rm_3_port = jsonReader.getPortForKeys("Mark", "RM");
		ReplicaManager rm3 = new ReplicaManager(rm_3_port, REPLICA_3_PATH, new CustomLogger(new TextFileLog()));
		
		HashMap<String, ReplicaManager> replicaManagers = new HashMap<String, ReplicaManager>();
		replicaManagers.put("RM_1", rm1);
		//replicaManagers.put("RM_2", rm2);
		//replicaManagers.put("RM_3", rm3);
		
		for(Entry<String, ReplicaManager> entry : replicaManagers.entrySet()){
			ReplicaManager replicaManager = entry.getValue();
			new Thread(replicaManager).start();
		}
		
		System.out.println("Replicas initialized.");
		
		// Exit condition to trigger shutdown hooks
		new Thread(() ->{
			Scanner keyboard = new Scanner(System.in);
			System.out.println("Enter -1 to exit:");
			String input = keyboard.next();
			while(!input.equals("-1")){
				input = keyboard.next();
			}
			keyboard.close();
			System.exit(0);
		}).start();
	}
}
