package tam_replica.ServerInterfaceIDL;

/**
 * Interface definition: ServerIDL.
 * 
 * @author OpenORB Compiler
 */
public interface ServerIDLOperations
{
    /**
     * Operation bookFlight
     */
    public String bookFlight(String firstName, String lastName, String Address, String phone, String destination, String date, String flightclass);

    /**
     * Operation getBookedFlightCount
     */
    public String getBookedFlightCount(String recordtype);

    /**
     * Operation editFlightRecord
     */
    public String editFlightRecord(String recordID, String fieldname, String newValue);

    /**
     * Operation transferReservation
     */
    public String transferReservation(String PassengerID, String currentcity, String newcity);

}
