package replica_manager;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry;

import json.JSONReader;
import log.CustomLogger;
import log.TextFileLog;

public class ReplicaManagerPublisher {
	private static final String jsonConfigPath = "..\\port_config.json";
	private static final String REPLICA_1_PATH = "java -classpath ..\\Jeremy_Replica\\bin server.PublishingServer";
	private static final String REPLICA_2_PATH = "java -classpath ..\\Caio_Replica\\bin servers.CaioPublisher";
	private static final String REPLICA_3_PATH = "java -classpath ..\\Mark_Replica\\bin server.FlightReservationServerPublisher";
	
	public static void main(String[] args) {
		JSONReader jsonReader = new JSONReader(jsonConfigPath);
		jsonReader.initialize();
		
		HashMap<String, ReplicaManager> replicaManagers = new HashMap<String, ReplicaManager>();
		replicaManagers.put("REPLICA_1", new ReplicaManager(jsonReader.getPortForKeys("Jeremy", "RM"), REPLICA_1_PATH, new CustomLogger(new TextFileLog())));
		//replicaManagers.put("REPLICA_2", new ReplicaManager(jsonReader.getPortForKeys("Caio", "RM"), REPLICA_2_PATH, new CustomLogger(new TextFileLog())));
		//replicaManagers.put("REPLICA_3", new ReplicaManager(jsonReader.getPortForKeys("Mark", "RM"), REPLICA_3_PATH, new CustomLogger(new TextFileLog())));
		// TODO : Add remaining replica (Patrick)
		
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
