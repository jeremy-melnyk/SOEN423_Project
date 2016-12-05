package jeremy_replica.log;

public interface ILog {
	public boolean write(String directoryName, String fileName, String logMessage);
	public String readContent(String tag);
	public boolean clear(String tag);
}
