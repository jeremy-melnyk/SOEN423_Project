package jeremy_replica.udp_parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import jeremy_replica.enums.City;
import jeremy_replica.enums.EditType;
import jeremy_replica.enums.FlightClass;
import jeremy_replica.enums.FlightRecordField;
import jeremy_replica.friendly_end.FlightReservationServer;
import jeremy_replica.friendly_end.FlightReservationServerHelper;
import jeremy_replica.global.Constants;
import jeremy_replica.log.ILogger;
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
	protected final String USERNAME = "JEREMY_";
	protected final String NAME_SERVICE = "NameService";
	private ILogger logger;
	
	public UdpParser(ORB orb, int port, ILogger logger) {
		super(orb, port);
		this.logger = logger;
	}

	protected BookFlightReply bookFlight(BookFlightOperation bookFlightOperation) {
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
		String result = flightServer.bookFlight(firstName, lastName, address.toString(), phoneNumber, destination.toString(),
				date, flightClass.toString());
		String[] resultTokens = result.split(Constants.DELIMITER_ESCAPE);
		if (resultTokens.length == 1){
			// Must be an error
			return new BookFlightReply(result);
		}
		int resultFlightId = Integer.parseInt(resultTokens[0]);
		int resultPassengerId = Integer.parseInt(resultTokens[2]);
		String resultDate = resultTokens[8];
		try {
			Date resultDateObject = new SimpleDateFormat(Constants.DATE_FORMAT).parse(resultTokens[8]);
			resultDate = new SimpleDateFormat(Constants.DATE_FORMAT).format(resultDateObject);
		} catch (ParseException e) {
			logger.log("UDP_PARSER", "DATE_PARSE_FAILED", e.getMessage());
			e.printStackTrace();
		}
		BookFlightReply bookFlightReply = new BookFlightReply(resultPassengerId, resultFlightId, resultTokens[6], resultTokens[7], 
				resultTokens[3], resultTokens[4], resultDate, resultTokens[1]);
		return bookFlightReply;
	}

	protected GetBookedFlightCountReply getBookedFlightCount(GetBookedFlightCountOperation getBookedFlightCountOperation) {
		String[] recordTypeTokens = getBookedFlightCountOperation.getRecordType().split(Constants.DELIMITER_ESCAPE);
		String managerId = recordTypeTokens[0].toUpperCase();
		FlightClass flightClass = FlightClass.valueOf(recordTypeTokens[1].toUpperCase());
		City origin = City.valueOf(managerId.substring(0, 3).toUpperCase());
		FlightReservationServer flightServer = getFlightServer(origin);
		String bookedFlightCountRequest = managerId + Constants.DELIMITER + flightClass.toString();
		String result = flightServer.getBookedFlightCount(bookedFlightCountRequest);
		String[] resultTokens = result.split(Constants.DELIMITER_ESCAPE);
		int mtlCount = Integer.parseInt(resultTokens[1].substring(4, resultTokens[1].length()));
		int wstCount = Integer.parseInt(resultTokens[2].substring(4, resultTokens[2].length()));
		int ndlCount = Integer.parseInt(resultTokens[3].substring(4, resultTokens[3].length()));
		GetBookedFlightCountReply getBookedFlightCountReply = new GetBookedFlightCountReply(mtlCount, wstCount, ndlCount);
		return getBookedFlightCountReply;
	}

	protected EditFlightRecordReply editFlightRecord(EditFlightRecordOperation editFlightRecordOperation) {
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
			// Recognizes FIRST | BUSINESS | ECONOMY instead of ECONOMY |
			// BUSINESS | FIRST
			newValue = origin.toString() + Constants.DELIMITER + newValueTokens[0] + Constants.DELIMITER
					+ newValueTokens[1] + Constants.DELIMITER + newValueTokens[4] + Constants.DELIMITER
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
		String result = flightServer.editFlightRecord(request, flightRecordField.toString(), newValue);
		
		String[] resultTokens = result.split(Constants.DELIMITER_ESCAPE);
		if (resultTokens.length == 1){
			// Must be an error
			return new EditFlightRecordReply(global.Constants.ERROR_CODE);
		}
		
		String economySeats = resultTokens[11].substring(1, resultTokens[11].length());
		String businessSeats = resultTokens[8].substring(1, resultTokens[11].length());
		String firstSeats = resultTokens[5].substring(1, resultTokens[11].length());
		
		
		EditFlightRecordReply editFlightRecordReply = new EditFlightRecordReply(resultTokens[0], resultTokens[1], resultTokens[2],
				resultTokens[3], economySeats, businessSeats, firstSeats);
		
		return editFlightRecordReply;
		
		/*
		// Old EditFlightRecord response model
		String failMessage1 = "Flight reservation does not exist.";
		String failMessage2 = "Transfer reservation failed.";
		if(result.equals(failMessage1) || result.equals(failMessage2)){
			return new EditFlightRecordReply(result);
		}
		
		String[] resultTokens = result.split(Constants.DELIMITER_ESCAPE);
		int economicSeats = Integer.parseInt(resultTokens[11].substring(1, resultTokens[11].length()));
		int businessSeats = Integer.parseInt(resultTokens[8].substring(1, resultTokens[8].length()));
		int firstSeats = Integer.parseInt(resultTokens[5].substring(1, resultTokens[5].length()));
		
		String resultDate = resultTokens[3];
		try {
			Date resultDateObject = new SimpleDateFormat(Constants.DATE_TOSTRING_FORMAT).parse(resultTokens[3]);
			resultDate = new SimpleDateFormat(Constants.DATE_FORMAT).format(resultDateObject);
		} catch (ParseException e) {
			logger.log("UDP_PARSER", "DATE_PARSE_FAILED", e.getMessage());
			e.printStackTrace();
		}
		
		EditFlightRecordReply editFlightRecordReply = new EditFlightRecordReply(resultTokens[0], resultTokens[1], resultTokens[2],
				resultDate, economicSeats, businessSeats, firstSeats);
		
		return editFlightRecordReply;
		*/
	}
	
	@Override
	protected TransferReservationReply transferReservation(TransferReservationOperation transferReservationOperation) {
		String[] recordIdTokens = transferReservationOperation.getPassengerId().split(Constants.DELIMITER_ESCAPE);
		City origin = City.valueOf(transferReservationOperation.getCurrentCity().toUpperCase());
		City destination = City.valueOf(transferReservationOperation.getOtherCity().toUpperCase());
		FlightReservationServer flightServer = getFlightServer(origin);
		
		String transferReservationRequest = recordIdTokens[0].toUpperCase() + Constants.DELIMITER + recordIdTokens[1].toUpperCase();
		String result = flightServer.transferReservation(transferReservationRequest, origin.toString(), destination.toString());
		String[] resultTokens = result.split(Constants.DELIMITER_ESCAPE);
		if (resultTokens.length == 1){
			// Must be an error
			return new TransferReservationReply(resultTokens[0]);
		}
		int passengerId = Integer.parseInt(resultTokens[0]);
		int flightId = Integer.parseInt(resultTokens[5]);
		TransferReservationReply transferReservationReply = new TransferReservationReply(passengerId, flightId, resultTokens[6], resultTokens[7], resultTokens[3], resultTokens[4], resultTokens[8], resultTokens[1]);
		return transferReservationReply;
	}

	private FlightReservationServer getFlightServer(City city) {
		org.omg.CORBA.Object nameServiceRef;
		try {
			nameServiceRef = orb.resolve_initial_references(NAME_SERVICE);
			NamingContextExt namingContextRef = NamingContextExtHelper.narrow(nameServiceRef);
			org.omg.CORBA.Object flightReservationServerRef = namingContextRef.resolve_str(USERNAME + city.toString());
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
