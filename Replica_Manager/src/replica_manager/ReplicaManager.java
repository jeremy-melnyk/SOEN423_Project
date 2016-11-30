package replica_manager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReplicaManager implements Runnable {
	private final int BUFFER_SIZE = 5000;
	private final int THREAD_POOL_SIZE = Integer.MAX_VALUE;
	private final ExecutorService threadPool;
	int port;

	public ReplicaManager(int port) {
		super();
		this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		this.port = port;
	}

	@Override
	public void run() {
		serveRequests();
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
