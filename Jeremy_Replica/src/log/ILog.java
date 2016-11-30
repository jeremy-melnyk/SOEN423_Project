package log;

public interface ILog {
<<<<<<< refs/remotes/origin/master
	public boolean write(String directoryName, String fileName, String logMessage);
=======
	public boolean write(String fileName, String logMessage);
>>>>>>> Added CORBA replica implementation to Jeremy_Replica
	public String readContent(String tag);
	public boolean clear(String tag);
}
