package tam_replica.ServerInterfaceIDL;
import java.rmi.*;


public interface ServerInterface extends Remote {

	public String bookFlight(String firstName, String lastName, String Address, String phone, String destination, String date, String flightclass) throws RemoteException;
	public String getBookedFlightCount(String recordtype) throws RemoteException;
	public String editFlightRecord(String recordID, String fieldname, String newValue) throws RemoteException;
	public String transferReservation(int PassengerID, String currentcity, String newcity) throws RemoteException;
}
