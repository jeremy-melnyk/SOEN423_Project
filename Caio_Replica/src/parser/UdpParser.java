package parser;

import java.text.SimpleDateFormat;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import FlightBookingServer.FlightServerInterface;
import FlightBookingServer.FlightServerInterfaceHelper;
import packet.BookFlightOperation;
import packet.EditFlightRecordOperation;
import packet.GetBookedFlightCountOperation;
import packet.TransferReservationOperation;
import udp_parser.UdpParserBase;

public class UdpParser extends UdpParserBase{
	private NamingContextExt ncRef;
	
	public UdpParser(ORB orb) {
		super(orb);
		
		//CORBA
		org.omg.CORBA.Object objRef = null;
		try {
			objRef = orb.resolve_initial_references("NameService");
		} catch (InvalidName e) {
			e.printStackTrace();
		}
		ncRef = NamingContextExtHelper.narrow(objRef);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	protected String bookFlight(BookFlightOperation bookFlightOperation) {
		FlightServerInterface server = null;
		// FORMAT MTL|WST : DEPARTURE = MTL
		try{
			String date = (new SimpleDateFormat("YYYY/mm/DD")).format((new SimpleDateFormat("mm/DD/YYYY").parse(bookFlightOperation.getDate())));
			String[] dep_dest = (bookFlightOperation.getDestination().split("|"));
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
			// Create packet and return;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected String getBookedFlightCount(GetBookedFlightCountOperation getBookedFlightCountOperation) {
		// recordType: "MTL1111|FIRST" (managerId | flightClass)
		FlightServerInterface server = null;
		String recordType[] = getBookedFlightCountOperation.getRecordType().split("|");
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
		String managerLocation = manager.substring(0, 3);
		try {
			server = (FlightServerInterface) FlightServerInterfaceHelper.narrow(ncRef.resolve_str(managerLocation));
		} catch (NotFound e) {
			e.printStackTrace();
		} catch (CannotProceed e) {
			e.printStackTrace();
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			e.printStackTrace();
		}
		String reply = server.getBookedFlightCount(flightClassInteger);
		// Construct reply packet and reply
		return null;
	}

	@Override
	protected String editFlightRecord(EditFlightRecordOperation editFlightRecordOperation) {
		FlightServerInterface server = null;
		String recordId[] = editFlightRecordOperation.getRecordId().split("|");
		String manager = recordId[0];
		String fieldName = editFlightRecordOperation.getFieldName();
		String newValues = editFlightRecordOperation.getNewValue();
		
		String parsedRecordId = "";
		String parsedFieldName = "";
		String parsedNewValues = "";
		if(fieldName.equalsIgnoreCase("CREATE")){
			parsedRecordId = recordId[0] + "-" + recordId[1];
			parsedFieldName = "add";
			parsedNewValues = newValues.replace('|', '&');
		}else if(fieldName.equalsIgnoreCase("DELETE")){
			parsedRecordId = recordId[0] + "-" + recordId[1];
			parsedFieldName = "delete";			
		}else if(fieldName.contains("EDIT")){
			parsedRecordId = recordId[0] + "-" + recordId[1];
			parsedFieldName = "edit?";
			String changedField = fieldName.split("|")[1];
			if(changedField.equalsIgnoreCase("ECONOMY"))
				parsedFieldName += "econ";
			else if(changedField.equalsIgnoreCase("BUSINESS"))
				parsedFieldName += "bus";
			else if(changedField.equalsIgnoreCase("DESTINATION"))
				parsedFieldName += "dest";
			else
				parsedFieldName += changedField.toLowerCase();
			parsedNewValues = newValues;
		}
		String managerLocation = manager.substring(0, 3);
		try {
			server = (FlightServerInterface) FlightServerInterfaceHelper.narrow(ncRef.resolve_str(managerLocation));
		} catch (NotFound e) {
			e.printStackTrace();
		} catch (CannotProceed e) {
			e.printStackTrace();
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			e.printStackTrace();
		}
		String reply = server.editFlightRecord(parsedRecordId, parsedFieldName, parsedNewValues);
		// Create response packet and return
		return null;
	}

	@Override
	protected String transferReservation(TransferReservationOperation transferReservation) {
		FlightServerInterface server = null;
		String recordId[] = transferReservation.getPassengerId().split("|");
		String managerLocation = recordId[0].substring(0, 3);
		String currentCity = transferReservation.getCurrentCity();
		String otherCity = transferReservation.getOtherCity();
		try {
			server = (FlightServerInterface) FlightServerInterfaceHelper.narrow(ncRef.resolve_str(managerLocation));
		} catch (NotFound e) {
			e.printStackTrace();
		} catch (CannotProceed e) {
			e.printStackTrace();
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			e.printStackTrace();
		}
		String reply = server.transferReservation(recordId[1], currentCity, otherCity);
		// Create packet and return
		return null;
	}

}
