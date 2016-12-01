package udp_parser;

import org.omg.CORBA.ORB;

import packet.BookFlightOperation;
import packet.EditFlightRecordOperation;
import packet.GetBookedFlightCountOperation;
import packet.OperationParameters;
import packet.Packet;
import packet.Operation;
import packet.TransferReservationOperation;

public abstract class UdpParserBase {
	protected final ORB orb;

	public UdpParserBase(ORB orb) {
		super();
		this.orb = orb;
	}

	public ORB getOrb() {
		return orb;
	}

	public String processPacket(Packet packet) {
		Operation replicaOperation = packet.getReplicaOperation();
		OperationParameters operationParameters = packet.getOperationParameters();
		switch (replicaOperation) {
		case BOOK_FLIGHT:
			BookFlightOperation bookFlightOperation = (BookFlightOperation) operationParameters;
			String bookFlightResult = bookFlight(bookFlightOperation);
			return bookFlightResult;
			// TODO
			// Return response model
		case BOOKED_FLIGHTCOUNT:
			GetBookedFlightCountOperation getBookedFlightCountOperation = (GetBookedFlightCountOperation) operationParameters;
			String bookedFlightCountResult = getBookedFlightCount(getBookedFlightCountOperation);
			return bookedFlightCountResult;
			// TODO
			// Return response model
		case EDIT_FLIGHT:
			EditFlightRecordOperation editFlightRecordOperation = (EditFlightRecordOperation) operationParameters;
			String editFlightRecordOperationResult = editFlightRecord(editFlightRecordOperation);
			return editFlightRecordOperationResult;
			// TODO
			// Return response model
		case TRANSFER_RESERVATION:
			TransferReservationOperation transferReservationOperation = (TransferReservationOperation) operationParameters;
			String transferReservationOperationResult = transferReservation(transferReservationOperation);
			return transferReservationOperationResult;
			// TODO
			// Return response model
		default:
			break;
		}	
		return null;
	}

	protected abstract String bookFlight(BookFlightOperation bookFlightOperation);

	protected abstract String getBookedFlightCount(GetBookedFlightCountOperation getBookedFlightCountOperation);

	protected abstract String editFlightRecord(EditFlightRecordOperation editFlightRecordOperation);
	
	protected abstract String transferReservation(TransferReservationOperation transferReservation);
}
