package json;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONReader {

	public static void main(String[] args) {
		
		JSONParser parser = new JSONParser();
		
		try {
			JSONArray replicaManagers = (JSONArray) parser.parse(new FileReader("CORBA_DFRS/src/port_config.json"));
			
			for (Object r : replicaManagers) {
				JSONObject rm = (JSONObject) r;
				String name = (String) rm.get("name");
				long port = (long) rm.get("port");
				System.out.println("RM: " + name);
				
				JSONArray cities = (JSONArray) rm.get("cities");
				
				System.out.println("Cities: ");
				for (Object c : cities) {
					JSONObject city = (JSONObject) c;
					name = (String) city.get("name");
					port = (long) city.get("port");
					System.out.println("-" + name + ": " + port);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}

