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
	private static final String REPLICA_2_PATH = "java -classpath json-simple-1.1.jar;bin caio_replica.servers.CaioPublisher";
	private static final String REPLICA_3_PATH = "java -classpath json-simple-1.1.jar;bin mark_replica.server.FlightReservationServerPublisher";
	private static final String REPLICA_4_PATH = "java -classpath json-simple-1.1.jar;bin tam_replica.PublishingServer.PublishingServer";
	
	public static void main(String[] args) {
		JSONReader jsonReader = new JSONReader(JSON_CONFIG_PATH);
		
		int replica1Port = jsonReader.getPortForKeys("Jeremy", "");
		int replica2Port = jsonReader.getPortForKeys("Caio", "");
		int replica3Port = jsonReader.getPortForKeys("Mark", "");
		int replica4Port = jsonReader.getPortForKeys("Patrick", "");
		
		int rm_1_port = jsonReader.getPortForKeys("Jeremy", "RM");
		ReplicaManager rm1 = new ReplicaManager(rm_1_port, replica1Port, REPLICA_1_PATH, new CustomLogger(new TextFileLog()));
		
		int rm_2_port = jsonReader.getPortForKeys("Caio", "RM");
		ReplicaManager rm2 = new ReplicaManager(rm_2_port, replica2Port, REPLICA_2_PATH, new CustomLogger(new TextFileLog()));
		
		int rm_3_port = jsonReader.getPortForKeys("Mark", "RM");
		ReplicaManager rm3 = new ReplicaManager(rm_3_port, replica3Port, REPLICA_3_PATH, new CustomLogger(new TextFileLog()));
		
		int rm_4_port = jsonReader.getPortForKeys("Patrick", "RM");
		ReplicaManager rm4 = new ReplicaManager(rm_4_port, replica4Port, REPLICA_4_PATH, new CustomLogger(new TextFileLog()));
		
		HashMap<String, ReplicaManager> replicaManagers = new HashMap<String, ReplicaManager>();
		replicaManagers.put("RM_1", rm1);
		replicaManagers.put("RM_2", rm2);
		replicaManagers.put("RM_3", rm3);
		replicaManagers.put("RM_4", rm4);
		
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
