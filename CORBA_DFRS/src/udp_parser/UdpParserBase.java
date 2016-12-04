package udp_parser;

import org.omg.CORBA.ORB;

import packet.BookFlightOperation;
import packet.BookFlightReply;
import packet.EditFlightRecordOperation;
import packet.EditFlightRecordReply;
import packet.GetBookedFlightCountOperation;
import packet.GetBookedFlightCountReply;
import packet.OperationParameters;
import packet.Packet;
import packet.Operation;
import packet.TransferReservationOperation;
import packet.TransferReservationReply;

public abstract class UdpParserBase {
	protected final ORB orb;

	public UdpParserBase(ORB orb) {
		super();
		this.orb = orb;
	}

	public ORB getOrb() {
		return orb;
	}

	public Packet processPacket(Packet packet) {
		Operation replicaOperation = packet.getReplicaOperation();
		OperationParameters operationParameters = packet.getOperationParameters();
		switch (replicaOperation) {
		case BOOK_FLIGHT:
			BookFlightOperation bookFlightOperation = (BookFlightOperation) operationParameters;
			BookFlightReply bookFlightResult = bookFlight(bookFlightOperation);
			return new Packet(Operation.BOOK_FLIGHT, bookFlightResult);
		case BOOKED_FLIGHTCOUNT:
			GetBookedFlightCountOperation getBookedFlightCountOperation = (GetBookedFlightCountOperation) operationParameters;
			GetBookedFlightCountReply bookedFlightCountResult = getBookedFlightCount(getBookedFlightCountOperation);
			return new Packet(Operation.BOOKED_FLIGHTCOUNT, bookedFlightCountResult);
		case EDIT_FLIGHT:
			EditFlightRecordOperation editFlightRecordOperation = (EditFlightRecordOperation) operationParameters;
			EditFlightRecordReply editFlightRecordOperationResult = editFlightRecord(editFlightRecordOperation);
			return new Packet(Operation.EDIT_FLIGHT, editFlightRecordOperationResult);
		case TRANSFER_RESERVATION:
			TransferReservationOperation transferReservationOperation = (TransferReservationOperation) operationParameters;
			TransferReservationReply transferReservationOperationResult = transferReservation(transferReservationOperation);
			return new Packet(Operation.TRANSFER_RESERVATION, transferReservationOperationResult);
		default:
			break;
		}	
		return null;
	}

	protected abstract BookFlightReply bookFlight(BookFlightOperation bookFlightOperation);

	protected abstract GetBookedFlightCountReply getBookedFlightCount(GetBookedFlightCountOperation getBookedFlightCountOperation);

	protected abstract EditFlightRecordReply editFlightRecord(EditFlightRecordOperation editFlightRecordOperation);
	
	protected abstract TransferReservationReply transferReservation(TransferReservationOperation transferReservation);
}
