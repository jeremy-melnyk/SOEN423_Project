package mark_replica.udp_parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import mark_replica.global.Constants;
import mark_replica.flight_reservation_system.FlightReservation;
import mark_replica.flight_reservation_system.FlightReservationHelper;
import packet.BookFlightOperation;
import packet.BookFlightReply;
import packet.EditFlightRecordOperation;
import packet.EditFlightRecordReply;
import packet.GetBookedFlightCountOperation;
import packet.GetBookedFlightCountReply;
import packet.TransferReservationOperation;
import packet.TransferReservationReply;
import udp_parser.UdpParserBase;

public class UdpParser extends UdpParserBase {
	private final ORB orb;

	public UdpParser(ORB orb, int port) {
		super(orb, port);
		this.orb = orb;
	}

	protected BookFlightReply bookFlight(BookFlightOperation bookFlightOperation) {
		// FORMAT MTL-WST : DEPARTURE = MTL
		String date = "";
		try {
			date = (new SimpleDateFormat("dd.MM.yyyy"))
					.format((new SimpleDateFormat(Constants.DATE_FORMAT).parse(bookFlightOperation.getDate())));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String[] cityTokens = bookFlightOperation.getDestination().split(Constants.DELIMITER_ESCAPE);
		String s_FlightClass = bookFlightOperation.getFlightClass();
		String flightClass = null;
		if (s_FlightClass.equalsIgnoreCase("ECONOMY")) {
			flightClass = "ECON";
		} else if (s_FlightClass.equalsIgnoreCase("BUSINESS")) {
			flightClass = "BUS";
		} else if (s_FlightClass.equalsIgnoreCase("FIRST")) {
			flightClass = "FIRST";
		}
		String origin = cityTokens[0].toUpperCase();
		String destination = cityTokens[1].toUpperCase();
		FlightReservation server = getFlightServer(origin);
		String reply = server.bookFlight(bookFlightOperation.getFirstName(), bookFlightOperation.getLastName(),
				bookFlightOperation.getAddress(), bookFlightOperation.getPhoneNumber(), destination, date,
				flightClass);
		
		// TODO : Convert string reply back into the reply object model
		/*
		BookFlightReply bookFlightReply = new BookFlightReply(int passengerId, int flightId, String departure, String destination, 
				String lastName, String firstName, String date, String flightClass);
				*/
		return null;
	}

	protected GetBookedFlightCountReply getBookedFlightCount(GetBookedFlightCountOperation getBookedFlightCountOperation) {
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
		
		// TODO : Convert string reply back into the reply object model
		/*
		GetBookedFlightCountReply getBookedFlightCountReply = new GetBookedFlightCountReply(int mTL, int wST, int nDL);
		*/
		return null;
	}

	protected EditFlightRecordReply editFlightRecord(EditFlightRecordOperation editFlightRecordOperation) {
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
		
		// TODO : Convert string reply back into the reply object model
		/*
		EditFlightRecordReply editFlightRecordReply = new EditFlightRecordReply(String message);
		*/
		return null;
	}

	protected TransferReservationReply transferReservation(TransferReservationOperation transferReservation) {
		String passengerIDTokens[] = transferReservation.getPassengerId().split(Constants.DELIMITER_ESCAPE);
		int passengerID = Integer.parseInt(passengerIDTokens[1]);
		String currentCity = transferReservation.getCurrentCity();
		String otherCity = transferReservation.getOtherCity();
		
		FlightReservation server = getFlightServer(currentCity);
		String reply = server.transferReservation(passengerID, currentCity, otherCity);
		
		// TODO : Convert string reply back into the reply object model
		/*
		TransferReservationReply transferReservationReply = new TransferReservationReply(int passengerId, int flightId, String departure, String destination, 
		String lastName, String firstName, String date, String flightClass);
		*/
		return null;
	}
	
	private FlightReservation getFlightServer(String serverIdentifier) {
		org.omg.CORBA.Object nameServiceRef;
		try {
			nameServiceRef = orb.resolve_initial_references("NameService");
			NamingContextExt namingContextRef = NamingContextExtHelper.narrow(nameServiceRef);
			org.omg.CORBA.Object flightReservationServerRef = namingContextRef.resolve_str(serverIdentifier);
			FlightReservation flightReservation = FlightReservationHelper.narrow(flightReservationServerRef);
			return flightReservation;
		} catch (InvalidName e) {
			e.printStackTrace();
		} catch (NotFound e) {
			e.printStackTrace();
		} catch (CannotProceed e) {
			e.printStackTrace();
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			e.printStackTrace();
		}
		return null;
	}
}
