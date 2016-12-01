package parser;

import java.text.SimpleDateFormat;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import flight_reservation_system.FlightReservation;
import flight_reservation_system.FlightReservationHelper;
import packet.BookFlightOperation;
import packet.EditFlightRecordOperation;
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

	public String processPacket(Packet packet) {
		ReplicaOperation replicaOperation = packet.getReplicaOperation();
		OperationParameters operationParameters = packet.getOperationParameters();

		org.omg.CORBA.Object objRef = null;
		try {
			objRef = orb.resolve_initial_references("NameService");
		} catch (InvalidName e) {
			e.printStackTrace();
		}
		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
		FlightReservation server = null;
		String reply = null;
		String[] dep_dest = null;
		try {
			switch (replicaOperation) {
			case BOOK_FLIGHT:
				BookFlightOperation bookFlightOperation = (BookFlightOperation) operationParameters;
				// FORMAT MTL-WST : DEPARTURE = MTL
				String date = (new SimpleDateFormat("DD/mm/YYYY"))
						.format((new SimpleDateFormat("mm/DD/YYYY").parse(bookFlightOperation.getDate())));
				dep_dest = (bookFlightOperation.getDestination().split("-"));
				String s_FlightClass = bookFlightOperation.getFlightClass();
				String flightClass = null;
				if (s_FlightClass.equalsIgnoreCase("ECONOMY")) {
					flightClass = "ECON";
				} else if (s_FlightClass.equalsIgnoreCase("BUSINESS")) {
					flightClass = "BUS";
				} else if (s_FlightClass.equalsIgnoreCase("FIRST")) {
					flightClass = "FIRST";
				}
				server = (FlightReservation) FlightReservationHelper.narrow(ncRef.resolve_str(dep_dest[0]));
				reply = server.bookFlight(bookFlightOperation.getFirstName(), bookFlightOperation.getLastName(),
						bookFlightOperation.getAddress(), bookFlightOperation.getPhoneNumber(), dep_dest[1], date,
						flightClass);
			// TODO
			// Determine packet destination (MTL, WST, NDL)
			// Lookup city server using CORBA name service
			// Parse parameters to match FlightReservationServer IDL for my
			// replica
			// Invoke bookFlight(...) on my replica
				break;
			case BOOKED_FLIGHTCOUNT:
				server = (FlightReservation) FlightReservationHelper.narrow(ncRef.resolve_str(dep_dest[0]));
				reply = server.getBookedFlightCount("all");
				// TODO
				// Determine packet destination (MTL, WST, NDL)
				// Lookup city server using CORBA name service
				// Parse parameters to match FlightReservationServer IDL for my
				// replica
				// Invoke getBookedFlightCount(...) on my replica
				break;
			case EDIT_FLIGHT:
				EditFlightRecordOperation editFlightRecordOperation = (EditFlightRecordOperation) operationParameters;
				
				int recordID = Integer.parseInt(editFlightRecordOperation.getRecordId());
				String fieldName = editFlightRecordOperation.getFieldName();
				String newValue = editFlightRecordOperation.getNewValue();
				
				server = (FlightReservation) FlightReservationHelper.narrow(ncRef.resolve_str(dep_dest[0]));
				reply = server.editFlightRecord(recordID, fieldName, newValue);
				// TODO
				// Determine packet destination (MTL, WST, NDL)
				// Lookup city server using CORBA name service
				// Parse parameters to match FlightReservationServer IDL for my
				// replica
				// Invoke editFlightRecord(...) on my replica
				break;
			case TRANSFER_RESERVATION:
				// TODO
				// Determine packet destination (MTL, WST, NDL)
				// Lookup city server using CORBA name service
				// Parse parameters to match FlightReservationServer IDL for my
				// replica
				// Invoke transferReservation(...) on my replica
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reply;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
