package tam_replica.udp_parser;

import java.text.SimpleDateFormat;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import global.Constants;
import packet.BookFlightOperation;
import packet.BookFlightReply;
import packet.EditFlightRecordOperation;
import packet.EditFlightRecordReply;
import packet.GetBookedFlightCountOperation;
import packet.GetBookedFlightCountReply;
import packet.TransferReservationOperation;
import packet.TransferReservationReply;
import tam_replica.ServerInterfaceIDL.ServerIDL;
import tam_replica.ServerInterfaceIDL.ServerIDLHelper;
import udp_parser.UdpParserBase;

public class UdpParserTam extends UdpParserBase {
	private NamingContextExt ncRef;

	public UdpParserTam(ORB orb, int port) {
		super(orb, port);

		// CORBA
		org.omg.CORBA.Object objRef = null;
		try {
			objRef = orb.resolve_initial_references("NameService");
		} catch (InvalidName e) {
			e.printStackTrace();
		}
		ncRef = NamingContextExtHelper.narrow(objRef);
	}

	@Override
	protected BookFlightReply bookFlight(BookFlightOperation bookFlightOperation) {
		ServerIDL server = null;
		// FORMAT MTL|WST : DEPARTURE = MTL
		try {
			String date = (new SimpleDateFormat("MM/dd/yyyy"))
					.format((new SimpleDateFormat("MM/dd/yyyy").parse(bookFlightOperation.getDate())));
			String[] dest = (bookFlightOperation.getDestination().split(Constants.DELIMITER_ESCAPE));
			String s_FlightClass = bookFlightOperation.getFlightClass();
			server = (ServerIDL) ServerIDLHelper.narrow(ncRef.resolve_str(dest[0]));

			String reply = server.bookFlight(bookFlightOperation.getFirstName(), bookFlightOperation.getLastName(),
					bookFlightOperation.getAddress(), bookFlightOperation.getPhoneNumber(), dest[1], date,
					s_FlightClass);

			// Modified to be similar to Mark's implementation
			// Typical reply would look like this: "Flight successfully booked
			// for
			// passenger:|...|...|..."
			// It follows the format below:
			// BookFlightReply(int passengerId, int flightId, String departure,
			// String destination, String lastName, String firstName, String
			// date,
			// String flightClass)

			String formattedReply = reply.replaceAll("Flight successfully booked for passenger:|", "");
			// Format to remove the first part

			String[] replyTokens = formattedReply.split(Constants.DELIMITER_ESCAPE);

			int passengerID = Integer.parseInt(replyTokens[0]);
			int flightID = Integer.parseInt(replyTokens[1]);

			BookFlightReply bookFlightReply = new BookFlightReply(passengerID, flightID, replyTokens[2], replyTokens[3],
					replyTokens[4], replyTokens[5], replyTokens[6], replyTokens[7]);

			return bookFlightReply;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new BookFlightReply("Error");
	}

	@Override
	protected GetBookedFlightCountReply getBookedFlightCount(
			GetBookedFlightCountOperation getBookedFlightCountOperation) {
		// recordType: "MTL1111|FIRST" (managerId | flightClass)
		ServerIDL server = null;
		String recordType[] = getBookedFlightCountOperation.getRecordType().split(Constants.DELIMITER_ESCAPE);
		String manager = recordType[0];
		String flightClass = recordType[1];

		String managerLocation = manager.substring(0, 3);
		try {
			server = (ServerIDL) ServerIDLHelper.narrow(ncRef.resolve_str(managerLocation));
		} catch (NotFound e) {
			e.printStackTrace();
		} catch (CannotProceed e) {
			e.printStackTrace();
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			e.printStackTrace();
		}
		String reply = server.getBookedFlightCount(flightClass);

		// Modified to be similar to Mark's implementation
		// Typical reply would look like this: "MTL|1|WST|2|NDL|3"

		String[] replyTokens = reply.split(Constants.DELIMITER_ESCAPE);

		int mtlCount = 0;
		int wstCount = 0;
		int ndlCount = 0;

		for (int i = 0; i < 5; i += 2) {
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

	@Override
	protected EditFlightRecordReply editFlightRecord(EditFlightRecordOperation editFlightRecordOperation) {
		ServerIDL server = null;
		String recordID = editFlightRecordOperation.getRecordId();
		String recordId[] = editFlightRecordOperation.getRecordId().split(Constants.DELIMITER_ESCAPE);
		String manager = recordId[0];
		String fieldName = editFlightRecordOperation.getFieldName();
		String newValues = editFlightRecordOperation.getNewValue();

		String managerLocation = manager.substring(0, 3);
		try {
			server = (ServerIDL) ServerIDLHelper.narrow(ncRef.resolve_str(managerLocation));
		} catch (NotFound e) {
			e.printStackTrace();
		} catch (CannotProceed e) {
			e.printStackTrace();
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			e.printStackTrace();
		}
		String reply = server.editFlightRecord(recordID, fieldName, newValues);
		
		// Need agreed upon standardized reply message?
		EditFlightRecordReply editFlightRecordReply = new EditFlightRecordReply(reply);
		return editFlightRecordReply;
	}

	@Override
	protected TransferReservationReply transferReservation(TransferReservationOperation transferReservation) {
		ServerIDL server = null;
		String recordId[] = transferReservation.getPassengerId().split(Constants.DELIMITER_ESCAPE);
		String managerLocation = recordId[0].substring(0, 3);
		String currentCity = transferReservation.getCurrentCity();
		String otherCity = transferReservation.getOtherCity();
		try {
			server = (ServerIDL) ServerIDLHelper.narrow(ncRef.resolve_str(managerLocation));
		} catch (NotFound e) {
			e.printStackTrace();
		} catch (CannotProceed e) {
			e.printStackTrace();
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			e.printStackTrace();
		}
		String reply = server.transferReservation(recordId[1], currentCity, otherCity);
		// TO-DO: Create packet and return
		return new TransferReservationReply(reply);
	}
}
