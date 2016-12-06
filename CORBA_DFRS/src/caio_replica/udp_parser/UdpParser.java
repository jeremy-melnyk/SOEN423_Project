package caio_replica.udp_parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import caio_replica.FlightBookingServer.FlightServerInterface;
import caio_replica.FlightBookingServer.FlightServerInterfaceHelper;
import caio_replica.utils.Logger;
import global.Constants;
import packet.BookFlightOperation;
import packet.BookFlightReply;
import packet.EditFlightRecordOperation;
import packet.EditFlightRecordReply;
import packet.GetBookedFlightCountOperation;
import packet.GetBookedFlightCountReply;
import packet.TransferReservationOperation;
import packet.TransferReservationReply;
import udp_parser.UdpParserBase;

public class UdpParser extends UdpParserBase{
	private NamingContextExt ncRef;
	private Logger logger;
	private static final String USERNAME = "Caio";
	
	public UdpParser(ORB orb, int port) {
		super(orb, port);
		logger = new Logger("./logs/CAIO_PARSER.log");
		//CORBA
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
		logger.log("BOOK FLIGHT REQUEST", "Packet received");
		FlightServerInterface server = null;
		// FORMAT MTL|WST : DEPARTURE = MTL
		BookFlightReply bookFlightReply = null;
		try{
			String date = (new SimpleDateFormat("YYYY/mm/DD")).format((new SimpleDateFormat("mm/DD/YYYY").parse(bookFlightOperation.getDate())));
			String[] dep_dest = (bookFlightOperation.getDestination().split("\\|"));
			String s_FlightClass = bookFlightOperation.getFlightClass();
			String flightClass = "";
			if(s_FlightClass.equalsIgnoreCase("ECONOMY")){
				flightClass = "1";
			}else if(s_FlightClass.equalsIgnoreCase("BUSINESS")){
				flightClass = "2";
			}else if(s_FlightClass.equalsIgnoreCase("FIRST")){
				flightClass = "3";
			}
			server = (FlightServerInterface) FlightServerInterfaceHelper.narrow(ncRef.resolve_str(USERNAME+dep_dest[0]));
			String reply = server.bookFlight(bookFlightOperation.getFirstName(), bookFlightOperation.getLastName(),
											bookFlightOperation.getAddress(), bookFlightOperation.getPhoneNumber(),
											dep_dest[1], date, flightClass);
			if(reply.contains("OKK")){
				String parsedReply[] = reply.substring(4).split("\\|");
				int passengerId = Integer.parseInt(parsedReply[0].trim());
				int flightId = Integer.parseInt(parsedReply[1].trim());
				String departure_destination[] = parsedReply[2].split("--->");
				String dep = departure_destination[0].trim();
				String dest = departure_destination[1].trim();
				date = (new SimpleDateFormat("MM/dd/yyyy")).format((new SimpleDateFormat("yyyy/MM/dd")).parse(parsedReply[3].trim()));
				String name[] = parsedReply[4].split(",");
				String fName =name[1].trim();
				String lName = name[0].trim();
				int iFlightClass = Integer.parseInt(parsedReply[5].trim());
				switch(iFlightClass){
				case 1:
					flightClass = "ECONOMY";
					break;
				case 2:
					flightClass = "BUSINESS";
					break;
				case 3:
					flightClass = "FIRST";
				}
				bookFlightReply = new BookFlightReply(passengerId, flightId, dep, dest, lName, fName, date,flightClass);
			}else if(reply.contains("ERR")){
				bookFlightReply = new BookFlightReply("There was a problem with the operation");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		logger.log("BOOK FLIGHT REQUEST", "Packet sent");
		return bookFlightReply;
	}

	@Override
	protected GetBookedFlightCountReply getBookedFlightCount(GetBookedFlightCountOperation getBookedFlightCountOperation) {
		// recordType: "MTL1111|FIRST" (managerId | flightClass)
		logger.log("GET BOOK FLIGHT COUNT REQUEST", "Packet received: "+getBookedFlightCountOperation.getRecordType());
		FlightServerInterface server = null;
		GetBookedFlightCountReply getBookedFlightCountReply = null;
		String recordType[] = getBookedFlightCountOperation.getRecordType().split("\\|");
		logger.log("GET BOOK FLIGHT COUNT REQUEST", "recordType: " + recordType[0]);
		String manager = recordType[0];
		String flightClass = recordType[1];
		String flightClassInteger = "";
		if(flightClass.equalsIgnoreCase("ECONOMY"))
			flightClassInteger = "1";
		else if(flightClass.equalsIgnoreCase("BUSINESS"))
			flightClassInteger = "2";
		else if(flightClass.equalsIgnoreCase("FIRST"))
			flightClassInteger = "3";
		else if(flightClass.equalsIgnoreCase("ALL"))
			flightClassInteger = "4";
		logger.log("GET BOOK FLIGHT COUNT REQUEST", "manager location: "+manager.substring(0, 3));
		String managerLocation = manager.substring(0, 3);
		logger.log("GET BOOK FLIGHT COUNT REQUEST", "Getting CORBA");
		try {
			server = (FlightServerInterface) FlightServerInterfaceHelper.narrow(ncRef.resolve_str(USERNAME+managerLocation));
		} catch (NotFound e) {
			e.printStackTrace();
		} catch (CannotProceed e) {
			e.printStackTrace();
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			e.printStackTrace();
		}
		String reply = server.getBookedFlightCount(flightClassInteger);
		logger.log("GET BOOK FLIGHT COUNT REQUEST", "Received reply: "+reply);
		String parsedReply[] = reply.split(" ");
		int mtl = 0, ndl = 0, wst = 0;
		logger.log("GET BOOK FLIGHT COUNT REQUEST", ""+parsedReply);
		for(int i = 0; i < parsedReply.length; i += 2){
			if(parsedReply[i].equalsIgnoreCase("mtl"))
				mtl = Integer.parseInt(parsedReply[i+1]);
			else if(parsedReply[i].equalsIgnoreCase("ndl"))
				ndl = Integer.parseInt(parsedReply[i+1]);
			else if(parsedReply[i].equalsIgnoreCase("wst"))
				wst = Integer.parseInt(parsedReply[i+1]);
		}
		logger.log("GET BOOK FLIGHT COUNT REQUEST", "Packet sent");
		getBookedFlightCountReply = new GetBookedFlightCountReply(mtl, wst, ndl);
		return getBookedFlightCountReply;
	}

	@Override
	protected EditFlightRecordReply editFlightRecord(EditFlightRecordOperation editFlightRecordOperation) {
		FlightServerInterface server = null;
		String recordId[] = editFlightRecordOperation.getRecordId().split("\\|");
		String manager = recordId[0];
		String fieldName = editFlightRecordOperation.getFieldName();
		String newValues = editFlightRecordOperation.getNewValue();
		
		String parsedRecordId = "";
		String parsedFieldName = "";
		String parsedNewValues = "";
		if(fieldName.equalsIgnoreCase("CREATE")){
			parsedRecordId = recordId[0] + "-" + recordId[1];
			parsedFieldName = "add";
			String separateValues[] = newValues.split("\\|");
			try {
				separateValues[1] = (new SimpleDateFormat("yyyy/MM/dd")).format((new SimpleDateFormat("MM/dd/yyyy")).parse(separateValues[1].trim()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(String value : separateValues){
				parsedNewValues += value+"&";
			}
		}else if(fieldName.equalsIgnoreCase("DELETE")){
			parsedRecordId = recordId[0] + "-" + recordId[1];
			parsedFieldName = "delete";			
		}else if(fieldName.contains("EDIT")){
			parsedRecordId = recordId[0] + "-" + recordId[1];
			parsedFieldName = "edit?";
			String changedField = fieldName.split("\\|")[1];
			if(changedField.equalsIgnoreCase("ECONOMY")){
				parsedFieldName += "econ";
				parsedNewValues = newValues;
			}
			else if(changedField.equalsIgnoreCase("BUSINESS")){
				parsedFieldName += "bus";
				parsedNewValues = newValues;
			}
			else if(changedField.equalsIgnoreCase("DESTINATION")){
				parsedFieldName += "dest";
				parsedNewValues = newValues;
			}
			else{
				parsedFieldName += changedField.toLowerCase();
				String separateValues[] = newValues.split("\\|");
				try {
					separateValues[1] = (new SimpleDateFormat("yyyy/MM/dd")).format((new SimpleDateFormat("MM/dd/yyyy")).parse(separateValues[1].trim()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for(String value : separateValues){
					parsedNewValues += value+"&";
				}
			}
		}
		String managerLocation = manager.substring(0, 3);
		try {
			server = (FlightServerInterface) FlightServerInterfaceHelper.narrow(ncRef.resolve_str(USERNAME+managerLocation));
		} catch (NotFound e) {
			e.printStackTrace();
		} catch (CannotProceed e) {
			e.printStackTrace();
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			e.printStackTrace();
		}
		String reply = server.editFlightRecord(parsedRecordId, parsedFieldName, parsedNewValues);
		logger.log("EDIT FLIGHT", fieldName+": Reply: "+reply.substring(4));
		EditFlightRecordReply editFlightRecordReply = null;
		if(reply.contains("OKK")){
			String parsedReply[] = reply.substring(4).split("\\|");
			String flightId = parsedReply[0].trim();
			logger.log("\tEDIT FLIGHT", fieldName+": Reply Packet: "+flightId);

			String departure_destination[] = parsedReply[1].split("-->");
			
			String dep = departure_destination[0].trim();
			String dest = departure_destination[1].trim();
			logger.log("EDIT FLIGHT", fieldName+": Reply Packet: "+dep+" "+dest);

			String date = "";
			try {
				date = (new SimpleDateFormat("MM/dd/yyyy")).format((new SimpleDateFormat("yyyy/MM/dd")).parse(parsedReply[2].trim()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.log("EDIT FLIGHT", fieldName+": Reply Packet: "+date);

			String econ = parsedReply[3].trim().substring(6);
			String bus = parsedReply[4].trim().substring(5);
			String first = parsedReply[5].trim().substring(7);
			logger.log("EDIT FLIGHT", fieldName+": Reply Packet: "+econ + " "+bus+ " "+first);
			
			editFlightRecordReply = new EditFlightRecordReply(flightId, dep, dest, date,econ, bus, first);
			logger.log("EDIT FLIGHT", fieldName+": Reply Packet: "+editFlightRecordReply.toString());
		}else if(reply.contains("ERR")){
			editFlightRecordReply = new EditFlightRecordReply("There was a problem with the operation");
		}
		return editFlightRecordReply;
	}

	@Override
	protected TransferReservationReply transferReservation(TransferReservationOperation transferReservation) {
		FlightServerInterface server = null;
		String recordId[] = transferReservation.getPassengerId().split("\\|");
		String managerLocation = recordId[0].substring(0, 3);
		String currentCity = transferReservation.getCurrentCity();
		String otherCity = transferReservation.getOtherCity();
		TransferReservationReply transferReservationReply = null;
		try {
			server = (FlightServerInterface) FlightServerInterfaceHelper.narrow(ncRef.resolve_str(USERNAME+managerLocation));
		} catch (NotFound e) {
			e.printStackTrace();
		} catch (CannotProceed e) {
			e.printStackTrace();
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			e.printStackTrace();
		}
		String reply = server.transferReservation(recordId[1], currentCity, otherCity);
		
		if(reply.contains("OKK")){
			try{
				String parsedReply[] = reply.substring(4).split("\\|");
				int passengerId = Integer.parseInt(parsedReply[0].trim());
				int flightId = Integer.parseInt(parsedReply[1].trim());
				String departure_destination[] = parsedReply[2].split("--->");
				String dep = departure_destination[0].trim();
				String dest = departure_destination[1].trim();
				String date = (new SimpleDateFormat("MM/dd/yyyy")).format((new SimpleDateFormat("yyyy/MM/dd")).parse(parsedReply[3].trim()));
				String name[] = parsedReply[4].split(",");
				String fName =name[1].trim();
				String lName = name[0].trim();
				String flightClass = "";
				int iFlightClass = Integer.parseInt(parsedReply[5].trim());
				switch(iFlightClass){
				case 1:
					flightClass = "ECONOMY";
					break;
				case 2:
					flightClass = "BUSINESS";
					break;
				case 3:
					flightClass = "FIRST";
				}
				transferReservationReply = new TransferReservationReply(passengerId, flightId, dep, dest, lName, fName, date,flightClass);
			}catch(Exception e){
				e.printStackTrace();
			}
		}else if(reply.contains("ERR")){
			transferReservationReply = new TransferReservationReply("There was a problem with the operation");
		}
		return transferReservationReply;
	}

}
