package data_structures;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CustomHashMap<K, V> extends HashMap<K, V> {
	private static final long serialVersionUID = 1L;
	private ReadWriteLock lock;

	public CustomHashMap() {
		super();
		this.lock = new ReentrantReadWriteLock(true);
	}

	public CustomHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public CustomHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	public CustomHashMap(Map<? extends K, ? extends V> m) {
		super(m);
	}
	
	@Override
	public boolean containsKey(Object key){
		lock.readLock().lock();
		try{
			 return super.containsKey(key);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public V get(Object key){
		lock.readLock().lock();
		try{
			 return super.get(key);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	@Override
	public V put(K key, V value){
		lock.writeLock().lock();
		try{
			 return super.put(key, value);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public V remove(Object key){
		lock.writeLock().lock();
		try{
			 return super.remove(key);
		} finally {
			lock.writeLock().unlock();
		}
	}
}
