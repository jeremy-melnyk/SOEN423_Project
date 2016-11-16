package udp_parser;

import org.omg.CORBA.ORB;

import packet.BookFlightOperation;
import packet.OperationParameters;
import packet.Packet;
import packet.ReplicaOperation;

public class UdpParser {
	private final ORB orb;

	public UdpParser(ORB orb) {
		super();
		this.orb = orb;
	}

	public ORB getOrb() {
		return orb;
	}

	public void processPacket(Packet packet) {
		ReplicaOperation replicaOperation = packet.getReplicaOperation();
		OperationParameters operationParameters = packet.getOperationParameters();

		switch (replicaOperation) {
		case BOOK_FLIGHT:
			BookFlightOperation bookFlightOperation = (BookFlightOperation) operationParameters;
			// TODO
			// Determine packet destination (MTL, WST, NDL)
			// Lookup city server using CORBA name service
			// Parse parameters to match FlightReservationServer IDL for my replica
			// Invoke bookFlight(...) on my replica
			break;
		case BOOKED_FLIGHTCOUNT:
			// TODO
			// Determine packet destination (MTL, WST, NDL)
			// Lookup city server using CORBA name service
			// Parse parameters to match FlightReservationServer IDL for my replica
			// Invoke getBookedFlightCount(...) on my replica
			break;
		case EDIT_FLIGHT:
			// TODO
			// Determine packet destination (MTL, WST, NDL)
			// Lookup city server using CORBA name service
			// Parse parameters to match FlightReservationServer IDL for my replica
			// Invoke editFlightRecord(...) on my replica
			break;
		case TRANSFER_RESERVATION:
			// TODO
			// Determine packet destination (MTL, WST, NDL)
			// Lookup city server using CORBA name service
			// Parse parameters to match FlightReservationServer IDL for my replica
			// Invoke transferReservation(...) on my replica
			break;
		default:
			break;
		}
	}
}
