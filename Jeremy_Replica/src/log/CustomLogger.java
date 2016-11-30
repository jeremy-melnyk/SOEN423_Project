package log;

public class CustomLogger implements ILogger {
<<<<<<< refs/remotes/origin/master
	private final String BASE_PATH = "logs";
=======
	private final String BASE_PATH = "Logs/";
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
	private final String EXTENSION = ".txt";
	ILog log;
	
	public CustomLogger(ILog log) {
		super();
		this.log = log;
	}

	@Override
	public boolean log(String tag, String operation, String message)
	{
		String logMessage = String.format("%s : %s : %s" + System.lineSeparator(), tag, operation, message);
<<<<<<< refs/remotes/origin/master
		String directoryName = BASE_PATH;
		String fileName = tag + EXTENSION;
		return this.log.write(directoryName, fileName, logMessage);
=======
		String fileName = BASE_PATH + tag + EXTENSION;
		return this.log.write(fileName, logMessage);
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
	}

	@Override
	public boolean clearLog(String tag)
	{
		return this.log.clear(tag);
	}
}
