package udp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class UdpHelper {
	public static byte[] getByteArray(Object obj){
		try {
			ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
			objectOutputStream.writeObject(obj);
			objectOutputStream.flush();
			objectOutputStream.close();
			return byteOutputStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] intToByteArray(int data){
        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);
			dataOutputStream.writeInt(data);
	        dataOutputStream.close();
	        return byteOutputStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	public static int byteArrayToInt(byte[] data){
        try {
			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(data);
			DataInputStream dataInputStream = new DataInputStream(byteInputStream);
			int result  = dataInputStream.readInt();
			dataInputStream.close();
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
        return 0;
	}
	
	public static byte[] booleanToByteArray(boolean data){
        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);
			dataOutputStream.writeBoolean(data);
	        dataOutputStream.close();
	        return byteOutputStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	public static boolean byteArrayToBoolean(byte[] data){
        try {
			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(data);
			DataInputStream dataInputStream = new DataInputStream(byteInputStream);
			boolean result  = dataInputStream.readBoolean();
			dataInputStream.close();
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
        return false;
	}
}
