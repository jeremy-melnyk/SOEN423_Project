package replica_manager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jeremy_replica.log.ILogger;

public class ReplicaManager implements Runnable {
	private final String TAG = "REPLICA_MANAGER";
	private final int BUFFER_SIZE = 5000;
	private final int THREAD_POOL_SIZE = Integer.MAX_VALUE;
	private final ExecutorService threadPool;
	private ILogger logger;
	private String replicaPath;
	private int port;
	Process replica;
	Thread shutdownHook;
	
	public ReplicaManager(int port, String replicaPath, ILogger logger) {
		super();
		this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		this.replicaPath = replicaPath;
		this.port = port;
		this.logger = logger;
		this.replica = null;
		this.shutdownHook = null;
	}

	@Override
	public void run() {
		initReplica();
		serveRequests();
	}
	
	public boolean rebootReplica(){
		try {
			Runtime runtime = Runtime.getRuntime();
			replica.destroy();
			replica.waitFor();
			replica = runtime.exec(replicaPath);
			logger.log(TAG, "REPLICA_REBOOTED", "Replica: " + replicaPath + " was rebooted.");
			resetShutdownHook();
			return true;
		} catch (IOException e) {
			logger.log(TAG, "REPLICA_REBOOT_ERROR", e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
	}
	
	private void initReplica(){		
		try {
			Runtime runtime = Runtime.getRuntime();
			replica = runtime.exec(replicaPath);
			logger.log(TAG, "REPLICA_INITIALIZED", "Replica: " + replicaPath + " was initialized.");
			resetShutdownHook();
		} catch (IOException e) {
			logger.log(TAG, "REPLICA_INIT_ERROR", e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void resetShutdownHook(){
		Runtime runtime = Runtime.getRuntime();
		if(shutdownHook != null){
			runtime.removeShutdownHook(shutdownHook);	
		}
		shutdownHook = new Thread(() -> {
			try {
				if(replica != null){
					replica.destroy();
					logger.log(TAG, "REPLICA_DESTROYED", "Replica: " + replicaPath + " was destroyed by JVM shutdown hook.");	
				}
			} catch (Exception e) {
				logger.log(TAG, "REPLICA_DESTROY_FAIL", e.getMessage());
				e.printStackTrace();
			} finally {
				replica = null;	
			}
		});
		runtime.addShutdownHook(shutdownHook);
	}

	private void serveRequests() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(port);
			while (true) {
				byte[] buffer = new byte[BUFFER_SIZE];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				threadPool.execute(new ReplicaManagerPacketDispatcher(packet, this));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null){
				socket.close();
			}
		}
	}
}