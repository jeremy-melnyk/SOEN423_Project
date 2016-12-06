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
			flightClass = "ECONONOMY";
		} else if (s_FlightClass.equalsIgnoreCase("BUSINESS")) {
			flightClass = "BUSINESS";
		} else if (s_FlightClass.equalsIgnoreCase("FIRST")) {
			flightClass = "FIRST";
		}
		String origin = cityTokens[0].toUpperCase();
		String destination = cityTokens[1].toUpperCase();
		FlightReservation server = getFlightServer(origin);
		String reply = server.bookFlight(bookFlightOperation.getFirstName(), bookFlightOperation.getLastName(),
				bookFlightOperation.getAddress(), bookFlightOperation.getPhoneNumber(), destination, date, flightClass);

		// Typical reply would look like this: "Flight successfully booked for
		// passenger:|...|...|..."
		// It follows the format below:
		// BookFlightReply(int passengerId, int flightId, String departure,
		// String destination, String lastName, String firstName, String date,
		// String flightClass)

		String formattedReply = reply.replaceAll("Flight successfully booked for passenger:|", "");
		// Format to remove the first part

		String[] replyTokens = formattedReply.split(Constants.DELIMITER_ESCAPE);

		int passengerID = Integer.parseInt(replyTokens[0]);
		int flightID = Integer.parseInt(replyTokens[1]);

		try {
			date = (new SimpleDateFormat("MM/dd/yyyy"))
					.format((new SimpleDateFormat("dd/MM/yyyy")).parse(replyTokens[6]));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		BookFlightReply bookFlightReply = new BookFlightReply(passengerID, flightID, replyTokens[2], replyTokens[3],
				replyTokens[4], replyTokens[5], date, replyTokens[7]);

		return bookFlightReply;
	}

	protected GetBookedFlightCountReply getBookedFlightCount(
			GetBookedFlightCountOperation getBookedFlightCountOperation) {
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

		// Typical reply would look like this: "Booked
		// flights:|MTL|1|WST|2|NDL|3"

		String formattedReply = reply.replaceAll("Booked flights:|", "");
		// Format to "MTL|1|WST|2|NDL|3";

		String[] replyTokens = formattedReply.split(Constants.DELIMITER_ESCAPE);

		int mtlCount = 0;
		int wstCount = 0;
		int ndlCount = 0;

		for (int i = 0; i < 7; i += 2) {
			if (replyTokens[i].equalsIgnoreCase("MTL")) {
				mtlCount = Integer.parseInt(replyTokens[i + 1]);
			} else if (replyTokens[i].equalsIgnoreCase("WST")) {
				wstCount = Integer.parseInt(replyTokens[i + 1]);
			} else if (replyTokens[i].equalsIgnoreCase("NDL")) {
				ndlCount = Integer.parseInt(replyTokens[i + 1]);
			}
		}

		GetBookedFlightCountReply getBookedFlightCountReply = new GetBookedFlightCountReply(mtlCount, wstCount,
				ndlCount);

		return getBookedFlightCountReply;
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
			reply = server.editFlightRecord(flightRecordID, "add", destination);
		} else if (fieldAction.equalsIgnoreCase("DELETE")) {
			reply = server.editFlightRecord(flightRecordID, "delete", "");
		} else if (fieldAction.equalsIgnoreCase("EDIT")) {
			String newValue = newValueTokens[0];
			reply = server.editFlightRecord(flightRecordID, fieldNameTokens[1].toUpperCase(), newValue);
		}

		// Need agreed upon standardized reply message?
		EditFlightRecordReply editFlightRecordReply = new EditFlightRecordReply(reply);

		return editFlightRecordReply;
	}

	protected TransferReservationReply transferReservation(TransferReservationOperation transferReservation) {
		String passengerIDTokens[] = transferReservation.getPassengerId().split(Constants.DELIMITER_ESCAPE);
		int passengerID = Integer.parseInt(passengerIDTokens[1]);
		String currentCity = transferReservation.getCurrentCity();
		String otherCity = transferReservation.getOtherCity();

		FlightReservation server = getFlightServer(currentCity);
		String reply = server.transferReservation(passengerID, currentCity, otherCity);

		TransferReservationReply transferReservationReply = null;

		// Checking that the transfer operation was successful
		if (reply.split(Constants.DELIMITER_ESCAPE).length > 1) {
			// A successful reply has the same format as a successful flight
			// booking
			// It follows the format below:
			// BookFlightReply(int passengerId, int flightId, String departure,
			// String destination, String lastName, String firstName, String
			// date,
			// String flightClass)

			String formattedReply = reply.replaceAll("Flight successfully booked for passenger:|", "");
			// Format to remove the first part

			String[] replyTokens = formattedReply.split(Constants.DELIMITER_ESCAPE);

			passengerID = Integer.parseInt(replyTokens[0]);
			int flightID = Integer.parseInt(replyTokens[1]);
			String date = null;

			try {
				date = (new SimpleDateFormat("MM/dd/yyyy"))
						.format((new SimpleDateFormat("dd/MM/yyyy")).parse(replyTokens[6]));
			} catch (ParseException e) {
				e.printStackTrace();
			}

			transferReservationReply = new TransferReservationReply(passengerID, flightID, replyTokens[2],
					replyTokens[3], replyTokens[4], replyTokens[5], date, replyTokens[7]);
		} else {
			transferReservationReply = new TransferReservationReply(reply);
		}

		return transferReservationReply;
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
