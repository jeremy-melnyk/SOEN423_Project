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
	
	public ReplicaManager(int port, String replicaPath, ILogger logger) {
		super();
		this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		this.replicaPath = replicaPath;
		this.port = port;
		this.logger = logger;
		this.replica = null;
	}

	@Override
	public void run() {
		initReplica();
		serveRequests();
	}
	
	private void initReplica(){		
		try {
			Runtime runtime = Runtime.getRuntime();
			replica = runtime.exec(replicaPath);
			logger.log(TAG, "REPLICA_INITIALIZED", "Replica: " + replicaPath + " was initialized.");
			runtime.addShutdownHook(new Thread(() -> {
				try {
					replica.destroy();
					logger.log(TAG, "REPLICA_DESTROYED", "Replica: " + replicaPath + " was destroyed by JVM shutdown hook.");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.log(TAG, "REPLICA_DESTROY_FAIL", e.getMessage());
					e.printStackTrace();
				} finally {
					replica = null;	
				}
			}));
		} catch (IOException e) {
			logger.log(TAG, "REPLICA_INIT_ERROR", e.getMessage());
			e.printStackTrace();
		}
	}

	private void serveRequests() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(port);
			while (true) {
				byte[] buffer = new byte[BUFFER_SIZE];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				threadPool.execute(new ReplicaManagerPacketDispatcher(packet));
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