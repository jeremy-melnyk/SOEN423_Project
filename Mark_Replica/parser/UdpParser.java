package parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import enums.City;
import enums.EditType;
import flight_reservation_system.FlightReservation;
import flight_reservation_system.FlightReservationHelper;
import global.Constants;
import packet.BookFlightOperation;
import packet.EditFlightRecordOperation;
import packet.GetBookedFlightCountOperation;
import packet.OperationParameters;
import packet.Packet;
import packet.ReplicaOperation;
import packet.TransferReservationOperation;
import replica_friendly_end.FlightReservationServer;
import udp_parser.UdpParserBase;

public class UdpParser extends UdpParserBase {
	private final ORB orb;

	public UdpParser(ORB orb, int port) {
		super(orb, port);
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

	protected String bookFlight(BookFlightOperation bookFlightOperation) {
		String date = null;
		try {
			date = (new SimpleDateFormat("DD/mm/YYYY"))
					.format((new SimpleDateFormat("mm/DD/YYYY").parse(bookFlightOperation.getDate())));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String originDestinationTokens[] = (bookFlightOperation.getDestination().split(Constants.DELIMITER_ESCAPE));
		String origin = originDestinationTokens[0];
		String destination = originDestinationTokens[1];
		String flightClass = bookFlightOperation.getFlightClass();
		FlightReservation server = getFlightServer(origin);
		String reply = server.bookFlight(bookFlightOperation.getFirstName(), bookFlightOperation.getLastName(),
				bookFlightOperation.getAddress(), bookFlightOperation.getPhoneNumber(), destination, date, flightClass);
		return reply;
	}

	protected String getBookedFlightCount(GetBookedFlightCountOperation getBookedFlightCountOperation) {
		String[] recordTypeTokens = getBookedFlightCountOperation.getRecordType().split(Constants.DELIMITER_ESCAPE);
		String managerId = recordTypeTokens[0].toUpperCase();
		String flightClass = null;
		if (recordTypeTokens.length == 1) {
			flightClass = "ALL";
		} else {
			flightClass = recordTypeTokens[1].toUpperCase();
		}
		String origin = managerId.substring(0, 3).toUpperCase();
		FlightReservation server = getFlightServer(origin);
		String reply = server.getBookedFlightCount(flightClass);
		return reply;
	}

	protected String editFlightRecord(EditFlightRecordOperation editFlightRecordOperation) {
		String[] recordIdTokens = editFlightRecordOperation.getRecordId().split(Constants.DELIMITER_ESCAPE);
		String[] fieldNameTokens = editFlightRecordOperation.getFieldName().split(Constants.DELIMITER_ESCAPE);
		String[] newValueTokens = editFlightRecordOperation.getNewValue().split(Constants.DELIMITER_ESCAPE);
		
		String managerID = recordIdTokens[0].toUpperCase();
		int flightRecordID = Integer.parseInt(recordIdTokens[1]);
		String fieldAction = fieldNameTokens[0].toUpperCase();
		String origin = managerID.substring(0, 3).toUpperCase();
		FlightReservation server = getFlightServer(origin);

		String reply = null;

		if (fieldAction.equalsIgnoreCase("CREATE")) {
			String destination = newValueTokens[0];
			reply = server.editFlightRecord(flightRecordID, fieldAction, destination);
		} else if (fieldAction.equalsIgnoreCase("DELETE")) {
			reply = server.editFlightRecord(flightRecordID, fieldAction, "");
		} else if (fieldAction.equalsIgnoreCase("EDIT")) {
			String newValue = newValueTokens[0];
			reply = server.editFlightRecord(flightRecordID, fieldAction, newValue);
		}

		return reply;

	}

	protected String transferReservation(TransferReservationOperation transferReservation) {
		String passengerIDTokens[] = transferReservation.getPassengerId().split(Constants.DELIMITER_ESCAPE);
		int passengerID = Integer.parseInt(passengerIDTokens[1]);
		String currentCity = transferReservation.getCurrentCity();
		String otherCity = transferReservation.getOtherCity();
		
		FlightReservation server = getFlightServer(currentCity);
		String reply = null;
		reply = server.transferReservation(passengerID, currentCity, otherCity);
		return reply;
	}

	private FlightReservation getFlightServer(String city) {
		org.omg.CORBA.Object objRef = null;
		FlightReservation server = null;
		try {
			objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			server = (FlightReservation) FlightReservationHelper.narrow(ncRef.resolve_str(city));
		} catch (InvalidName e) {
			e.printStackTrace();
		} catch (NotFound e) {
			e.printStackTrace();
		} catch (CannotProceed e) {
			e.printStackTrace();
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			e.printStackTrace();
		}
		return server;
	}

}
