package utils;

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
	
	

}
