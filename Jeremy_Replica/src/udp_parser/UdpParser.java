package udp_parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import enums.City;
import enums.EditType;
import enums.FlightClass;
import enums.FlightRecordField;
import global.Constants;
import log.ILogger;
import packet.BookFlightOperation;
import packet.EditFlightRecordOperation;
import packet.GetBookedFlightCountOperation;
import packet.OperationParameters;
import packet.Packet;
import packet.ReplicaOperation;
import replica_friendly_end.FlightReservationServer;
import replica_friendly_end.FlightReservationServerHelper;

public class UdpParser {
	protected final String NAME_SERVICE = "NameService";
	private final ORB orb;
	private ILogger logger;

	public UdpParser(ORB orb, ILogger logger) {
		super();
		this.orb = orb;
		this.logger = logger;
	}

	public ORB getOrb() {
		return orb;
	}

	public String processPacket(Packet packet) {
		ReplicaOperation replicaOperation = packet.getReplicaOperation();
		OperationParameters operationParameters = packet.getOperationParameters();
		switch (replicaOperation) {
		case BOOK_FLIGHT:
			BookFlightOperation bookFlightOperation = (BookFlightOperation) operationParameters;
			String bookFlightResult = bookFlight(bookFlightOperation);
			return bookFlightResult;
			// TODO
			// Send result back to Front End
		case BOOKED_FLIGHTCOUNT:
			GetBookedFlightCountOperation getBookedFlightCountOperation = (GetBookedFlightCountOperation) operationParameters;
			String bookedFlightCountResult = getBookedFlightCount(getBookedFlightCountOperation);
			return bookedFlightCountResult;
			// TODO
			// Send result back to Front End
		case EDIT_FLIGHT:
			EditFlightRecordOperation editFlightRecordOperation = (EditFlightRecordOperation) operationParameters;
			String editFlightRecordOperationResult = editFlightRecord(editFlightRecordOperation);
			return editFlightRecordOperationResult;
			// TODO
			// Send result back to Front End
		case TRANSFER_RESERVATION:
			// TODO
			// Determine packet destination (MTL, WST, NDL)
			// Lookup city server using CORBA name service
			// Parse parameters to match FlightReservationServer IDL for my replica
			// Invoke transferReservation(...) on my replica
		default:
			break;
		}
		
		return "";
	}

	private String bookFlight(BookFlightOperation bookFlightOperation) {
		String[] destinationTokens = bookFlightOperation.getDestination().split(Constants.DELIMITER_ESCAPE);
		City origin = City.valueOf(destinationTokens[0].toUpperCase());
		City destination = City.valueOf(destinationTokens[1].toUpperCase());
		FlightReservationServer flightServer = getFlightServer(origin);
		String firstName = bookFlightOperation.getFirstName();
		String lastName = bookFlightOperation.getLastName();
		String address = bookFlightOperation.getAddress();
		String phoneNumber = bookFlightOperation.getPhoneNumber();
		String date = bookFlightOperation.getDate();
		try {
			Date dateObject = new SimpleDateFormat(Constants.DATE_FORMAT).parse(date);
			date = new SimpleDateFormat(Constants.DATE_FORMAT).format(dateObject);
		} catch (ParseException e) {
			logger.log("UDP_PARSER", "DATE_PARSE_FAILED", e.getMessage());
			e.printStackTrace();
		}
		FlightClass flightClass = FlightClass.valueOf(bookFlightOperation.getFlightClass().toString().toUpperCase());
		return flightServer.bookFlight(firstName, lastName, address.toString(), phoneNumber, destination.toString(),
				date, flightClass.toString());
	}

	private String getBookedFlightCount(GetBookedFlightCountOperation getBookedFlightCountOperation) {
		String[] recordTypeTokens = getBookedFlightCountOperation.getRecordType().split(Constants.DELIMITER_ESCAPE);
		String managerId = recordTypeTokens[0].toUpperCase();
		FlightClass flightClass = FlightClass.valueOf(recordTypeTokens[1].toUpperCase());
		City origin = City.valueOf(managerId.substring(0, 3).toUpperCase());
		FlightReservationServer flightServer = getFlightServer(origin);
		String bookedFlightCountRequest = managerId + Constants.DELIMITER + flightClass.toString();
		return flightServer.getBookedFlightCount(bookedFlightCountRequest);
	}

	private String editFlightRecord(EditFlightRecordOperation editFlightRecordOperation) {
		String[] recordIdTokens = editFlightRecordOperation.getRecordId().split(Constants.DELIMITER_ESCAPE);
		String[] fieldNameTokens = editFlightRecordOperation.getFieldName().split(Constants.DELIMITER_ESCAPE);
		String[] newValueTokens = editFlightRecordOperation.getNewValue().split(Constants.DELIMITER_ESCAPE);

		String managerId = recordIdTokens[0].toUpperCase();
		String flightRecordId = recordIdTokens[1];
		String fieldAction = fieldNameTokens[0].toUpperCase();

		City origin = City.valueOf(managerId.substring(0, 3).toUpperCase());
		FlightReservationServer flightServer = getFlightServer(origin);

		String newValue = "";
		EditType editType = EditType.EDIT;
		switch (fieldAction) {
		case "CREATE":
			editType = EditType.ADD;
			// Recognizes FIRST | BUSINESS | ECONOMY instead of ECONOMY | BUSINESS | FIRST
			newValue = origin.toString() + Constants.DELIMITER + newValueTokens[0] + Constants.DELIMITER + newValueTokens[1] + Constants.DELIMITER + newValueTokens[4] + Constants.DELIMITER
					+ newValueTokens[3] + Constants.DELIMITER + newValueTokens[2];
			break;
		case "DELETE":
			editType = EditType.REMOVE;
			newValue = "";
			break;
		case "EDIT":
			editType = EditType.EDIT;
			break;
		default:
			logger.log("UDP_PARSER", "EDIT_TYPE_PARSE_FAIL", "Failed to parse edit type for: " + fieldAction);
			break;
		}

		FlightRecordField flightRecordField = FlightRecordField.NONE;
		if (fieldNameTokens.length > 1) {
			String fieldType = fieldNameTokens[1].toUpperCase();
			switch (fieldType) {
			case "DESTINATION":
				flightRecordField = FlightRecordField.DESTINATION;
				newValue = newValueTokens[0];
				break;
			case "DATE":
				flightRecordField = FlightRecordField.DATE;
				try {
					Date dateObject = new SimpleDateFormat(Constants.DATE_FORMAT).parse(newValueTokens[0]);
					newValue = new SimpleDateFormat(Constants.DATE_FORMAT).format(dateObject);
				} catch (ParseException e) {
					logger.log("UDP_PARSER", "DATE_PARSE_FAILED", e.getMessage());
					e.printStackTrace();
				}
				break;
			case "FIRST":
				flightRecordField = FlightRecordField.SEATS;
				newValue = FlightClass.FIRST.toString() + Constants.DELIMITER + newValueTokens[0];
				break;
			case "BUSINESS":
				flightRecordField = FlightRecordField.SEATS;
				newValue = FlightClass.BUSINESS.toString() + Constants.DELIMITER + newValueTokens[0];
				break;
			case "ECONOMY":
				flightRecordField = FlightRecordField.SEATS;
				newValue = FlightClass.ECONOMY.toString() + Constants.DELIMITER + newValueTokens[0];
				break;
			default:
				logger.log("UDP_PARSER", "FIELD_TYPE_PARSE_FAIL", "Failed to parse field type for: " + fieldType);
				break;
			}
		}

		String request = managerId + Constants.DELIMITER + editType + Constants.DELIMITER + flightRecordId;
		return flightServer.editFlightRecord(request, flightRecordField.toString(), newValue);
	}

	private FlightReservationServer getFlightServer(City city) {
		org.omg.CORBA.Object nameServiceRef;
		try {
			nameServiceRef = orb.resolve_initial_references(NAME_SERVICE);
			NamingContextExt namingContextRef = NamingContextExtHelper.narrow(nameServiceRef);
			org.omg.CORBA.Object flightReservationServerRef = namingContextRef.resolve_str(city.toString());
			FlightReservationServer flightReservationServer = FlightReservationServerHelper
					.narrow(flightReservationServerRef);
			return flightReservationServer;
		} catch (InvalidName e) {
			logger.log(city.toString(), "GET_FLIGHT_SERVER_FAIL", e.getMessage());
			e.printStackTrace();
		} catch (NotFound e) {
			logger.log(city.toString(), "GET_FLIGHT_SERVER_FAIL", e.getMessage());
			e.printStackTrace();
		} catch (CannotProceed e) {
			logger.log(city.toString(), "GET_FLIGHT_SERVER_FAIL", e.getMessage());
			e.printStackTrace();
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			logger.log(city.toString(), "GET_FLIGHT_SERVER_FAIL", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
