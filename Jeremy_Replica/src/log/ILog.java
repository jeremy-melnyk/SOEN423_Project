package log;

public interface ILog {
	public boolean write(String fileName, String logMessage);
	public String readContent(String tag);
	public boolean clear(String tag);
}
