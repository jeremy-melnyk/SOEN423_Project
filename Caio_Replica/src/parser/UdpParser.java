package parser;

import java.text.SimpleDateFormat;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import FlightBookingServer.FlightServerInterface;
import FlightBookingServer.FlightServerInterfaceHelper;
import packet.BookFlightOperation;
import packet.OperationParameters;
import packet.Packet;
import packet.ReplicaOperation;

public class UdpParser {
	private final ORB orb;

	public UdpParser(ORB orb) {
		super();
		this.orb = orb;
	}

	public ORB getOrb() {
		return orb;
	}

	public void processPacket(Packet packet) {
		ReplicaOperation replicaOperation = packet.getReplicaOperation();
		OperationParameters operationParameters = packet.getOperationParameters();
		
		org.omg.CORBA.Object objRef = null;
		try {
			objRef = orb.resolve_initial_references("NameService");
		} catch (InvalidName e) {
			e.printStackTrace();
		}
		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
		FlightServerInterface server = null;
		try{
		switch (replicaOperation) {
		case BOOK_FLIGHT:
			BookFlightOperation bookFlightOperation = (BookFlightOperation) operationParameters;
			// FORMAT MTL-WST : DEPARTURE = MTL
			String date = (new SimpleDateFormat("YYYY/mm/DD")).format((new SimpleDateFormat("mm/DD/YYYY").parse(bookFlightOperation.getDate())));
			String[] dep_dest = (bookFlightOperation.getDestination().split("-"));
			String s_FlightClass = bookFlightOperation.getFlightClass();
			String flightClass = "";
			if(s_FlightClass.equalsIgnoreCase("ECONOMY")){
				flightClass = "1";
			}else if(s_FlightClass.equalsIgnoreCase("BUSINESS")){
				flightClass = "2";
			}else if(s_FlightClass.equalsIgnoreCase("FIRST")){
				flightClass = "3";
			}
			server = (FlightServerInterface) FlightServerInterfaceHelper.narrow(ncRef.resolve_str(dep_dest[0]));
			String reply = server.bookFlight(bookFlightOperation.getFirstName(), bookFlightOperation.getLastName(),
											bookFlightOperation.getAddress(), bookFlightOperation.getPhoneNumber(),
											dep_dest[1], date, flightClass);
			// TODO
			// Determine packet destination (MTL, WST, NDL)
			// Lookup city server using CORBA name service
			// Parse parameters to match FlightReservationServer IDL for my replica
			// Invoke bookFlight(...) on my replica
			break;
		case BOOKED_FLIGHTCOUNT:
			// TODO
			// Determine packet destination (MTL, WST, NDL)
			// Lookup city server using CORBA name service
			// Parse parameters to match FlightReservationServer IDL for my replica
			// Invoke getBookedFlightCount(...) on my replica
			break;
		case EDIT_FLIGHT:
			// TODO
			// Determine packet destination (MTL, WST, NDL)
			// Lookup city server using CORBA name service
			// Parse parameters to match FlightReservationServer IDL for my replica
			// Invoke editFlightRecord(...) on my replica
			break;
		case TRANSFER_RESERVATION:
			// TODO
			// Determine packet destination (MTL, WST, NDL)
			// Lookup city server using CORBA name service
			// Parse parameters to match FlightReservationServer IDL for my replica
			// Invoke transferReservation(...) on my replica
			break;
		default:
			break;
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
