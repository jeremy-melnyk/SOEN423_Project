package jeremy_replica.log;

import java.util.Date;

public class CustomLogger implements ILogger {
	private final String BASE_PATH = "logs";
	private final String EXTENSION = ".txt";
	ILog log;
	
	public CustomLogger(ILog log) {
		super();
		this.log = log;
	}

	@Override
	public boolean log(String tag, String operation, String message)
	{
		String logMessage = String.format("%s : %s : %s : Timestamp : %s" + System.lineSeparator(), tag, operation, message, new Date().toString());
		String directoryName = BASE_PATH;
		String fileName = tag + EXTENSION;
		return this.log.write(directoryName, fileName, logMessage);
	}

	@Override
	public boolean clearLog(String tag)
	{
		return this.log.clear(tag);
	}
}
