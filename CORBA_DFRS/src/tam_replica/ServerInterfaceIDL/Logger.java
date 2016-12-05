package tam_replica.ServerInterfaceIDL;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class Logger {

	private String filelocation;
	private String user;
	
	
	public Logger (String filelocation){
		this.filelocation=filelocation.toUpperCase();
		this.user="Passenger";
	}
	public Logger (String filelocation, String user){
		this.filelocation=filelocation.toUpperCase();
		this.user=user;
	}
	
	
	
	public synchronized void write(String message, boolean isserver){
		try {
			String s="";
			if (isserver){
				s="Server_";
			}
			BufferedWriter bw= new BufferedWriter(new FileWriter(s+this.filelocation+ ".txt",true));
			PrintWriter writer= new PrintWriter(bw);
			writer.println(message);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void writetoFile(String message, String operation,boolean isserver){
		String timestamp=this.getTimeStamp();
		this.write(timestamp + "\t" + user + " performed " + operation + "\tResult:\t" + message,isserver);
	}
	
	public String getTimeStamp(){
		String pattern="MM/dd/yyyy hh:mm:ss aa";
		SimpleDateFormat format= new SimpleDateFormat(pattern);
		String timestamp= format.format(new Date());
		return timestamp;
	}
	
	public synchronized void writeToManagers(String message, String city){
		
		File directory=new File(System.getProperty("user.dir"));
		
		File[] matchingFiles = directory.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.startsWith(city.toUpperCase()) && name.endsWith("txt");
		    }
		});
		
		
		String timestamp=this.getTimeStamp();
		
		for (int i=0;i<matchingFiles.length;i++)
		{
			try {
				BufferedWriter bw= new BufferedWriter(new FileWriter(matchingFiles[i],true));
				PrintWriter writer= new PrintWriter(bw);
				writer.println(timestamp + "\t A passenger has tried to book a flight. Result:\t" + message);
				writer.close();
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
	}
	
	public void setUnknownUser(){
		this.user="INVALID USER";
	}
	
}
