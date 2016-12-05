package tam_replica.udp_parser;

	import java.text.SimpleDateFormat;

	import org.omg.CORBA.ORB;
	import org.omg.CORBA.ORBPackage.InvalidName;
	import org.omg.CosNaming.NamingContextExt;
	import org.omg.CosNaming.NamingContextExtHelper;
	import org.omg.CosNaming.NamingContextPackage.CannotProceed;
	import org.omg.CosNaming.NamingContextPackage.NotFound;

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

	public class UdpParserTam extends UdpParserBase{
		private NamingContextExt ncRef;
		
		public UdpParserTam(ORB orb, int port) {
			super(orb, port);
			
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
			ServerIDL server = null;
			// FORMAT MTL|WST : DEPARTURE = MTL
			try{
				String date = (new SimpleDateFormat("YYYY/mm/DD")).format((new SimpleDateFormat("mm/DD/YYYY").parse(bookFlightOperation.getDate())));
				String[] dest = (bookFlightOperation.getDestination().split("|"));
				String s_FlightClass = bookFlightOperation.getFlightClass();
				server = (ServerIDL) ServerIDLHelper.narrow(ncRef.resolve_str(dest[0]));
				
				String reply = server.bookFlight(bookFlightOperation.getFirstName(), bookFlightOperation.getLastName(),
												bookFlightOperation.getAddress(), bookFlightOperation.getPhoneNumber(),
												dest[1], date, s_FlightClass);
				// Create packet and return;
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected GetBookedFlightCountReply getBookedFlightCount(GetBookedFlightCountOperation getBookedFlightCountOperation) {
			// recordType: "MTL1111|FIRST" (managerId | flightClass)
			ServerIDL server = null;
			String recordType[] = getBookedFlightCountOperation.getRecordType().split("|");
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
			// Construct reply packet and reply
			return null;
		}

		@Override
		protected EditFlightRecordReply editFlightRecord(EditFlightRecordOperation editFlightRecordOperation) {
			ServerIDL server = null;
			String recordID=editFlightRecordOperation.getRecordId();
			String recordId[] = editFlightRecordOperation.getRecordId().split("|");
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
			// Create response packet and return
			return null;
		}

		@Override
		protected TransferReservationReply transferReservation(TransferReservationOperation transferReservation) {
			ServerIDL server = null;
			String recordId[] = transferReservation.getPassengerId().split("|");
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
			// Create packet and return
			return null;
		}

	}

