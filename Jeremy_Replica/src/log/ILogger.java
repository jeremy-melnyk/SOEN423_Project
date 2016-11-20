package log;

public interface ILogger {
	public boolean log(String tag, String operation, String message);
	public boolean clearLog(String tag);
}
