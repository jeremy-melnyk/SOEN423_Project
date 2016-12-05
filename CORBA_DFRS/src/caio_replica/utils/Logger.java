package caio_replica.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	private String fileName;
	
	public Logger(String fileName){
		this.fileName = fileName;
	}
	
	public void log(String a, String m){
		final String fn = this.fileName;
		final String agent = a;
		final String message = m;
		
		(new Thread(new Runnable() {
			
			@Override
			public void run() {
				FileWriter fw = null;
				String dateStamp = (new SimpleDateFormat("yyyy/MM/dd 'at' HH:mm:ss.SSS").format(new Date()));
				try {
					String path = fn.substring(2, fn.length());
					makeDirectories(path);
					fw = new FileWriter(fn, true);
					fw.write(dateStamp+ " - " + agent + " - " + message + '\n');
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					try {
						if(fw != null) fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
		})).start();
	}
	
	// Handle non existing directories
	private void makeDirectories(String path){
		String[] fnTokens = path.split("/");
		File directory = null;
		// First level
		if(fnTokens.length > 0){
			directory = new File(fnTokens[0]);	
			if (!directory.exists()){
				directory.mkdir();
			}
		}
		// Additional levels
		for(int i = 1; i < fnTokens.length - 1; ++i){
			directory = new File(fnTokens[0], fnTokens[i]);
			if (!directory.exists()){
				directory.mkdir();
			}
		}
	}
}
