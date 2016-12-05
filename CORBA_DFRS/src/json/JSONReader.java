package json;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONReader {

	private HashMap<String, Integer> replicaManagerPorts;
	private HashMap<String, HashMap<String, Integer>> cityPorts;

	private String filePath;

	public JSONReader() {
		replicaManagerPorts = new HashMap<String, Integer>();
		cityPorts = new HashMap<String, HashMap<String, Integer>>();

		// Default file path
		filePath = "port_config.json";
		
		initialize();
	}

	public JSONReader(String filePath) {
		replicaManagerPorts = new HashMap<String, Integer>();
		cityPorts = new HashMap<String, HashMap<String, Integer>>();
		this.filePath = filePath;
		
		initialize();
	}

	private void initialize() {
		JSONParser parser = new JSONParser();

		JSONArray replicaManagers;
		try {
			replicaManagers = (JSONArray) parser.parse(new FileReader(filePath));

			for (Object r : replicaManagers) {
				JSONObject rm = (JSONObject) r;
				String name = (String) rm.get("name");
				long port = (long) rm.get("port");
				int intPort = (int) port;

				replicaManagerPorts.put(name, intPort);

				JSONArray cities = (JSONArray) rm.get("cities");

				HashMap<String, Integer> temp = new HashMap<String, Integer>();

				for (Object c : cities) {
					JSONObject city = (JSONObject) c;
					String cityName = (String) city.get("name");
					port = (long) city.get("port");
					intPort = (int) port;
					temp.put(cityName, intPort);
				}

				cityPorts.put(name, temp);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void displayPorts() {
		System.out.println("Replica Manager: " + replicaManagerPorts);
		System.out.println("Cities: " + cityPorts);
	}

	// Takes two String parameters, the first being one of our names (i.e.
	// "Caio", "Jeremy", "Mark", or "Patrick"), the second being the
	// three-letter city code (optional)
	// To get replica manager's port, the city parameter should be left empty
	// Returns a negative value if key or key combination does not exist
	public int getPortForKeys(String username, String city) {
		
		String uppercaseCity = city.toUpperCase();

		int port = -1;

		if (city.isEmpty() && replicaManagerPorts.containsKey(username)) {
			port = replicaManagerPorts.get(username);
		} else if (!city.isEmpty() && cityPorts.containsKey(username)) {
			HashMap<String, Integer> temp;
			temp = cityPorts.get(username);
			if (temp.containsKey(uppercaseCity)) {
				port = temp.get(uppercaseCity);
			}
		}

		return port;
	}
	
	public int getSequencerPort() {
		return getPortForKeys("Sequencer", "");
	}

	// Testing
	public static void main(String[] args) {
		JSONReader reader = new JSONReader();
		reader.initialize();
		reader.displayPorts();
		System.out.println(reader.getPortForKeys("Caio", "MTL"));
		System.out.println(reader.getPortForKeys("Jeremy", "NDL"));
		System.out.println(reader.getPortForKeys("Mark", "WST"));
		System.out.println(reader.getPortForKeys("Patrick", "MTL"));
		System.out.println(reader.getSequencerPort());
	}
}
