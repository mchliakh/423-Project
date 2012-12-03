package app.server.udpservlet;

import packet.CoordinatorPacket;
import packet.ElectionPacket;
import udp.FIFOObjectUDPServlet;
import app.server.RetailStoreServerImpl;

public class ElectionServlet extends FIFOObjectUDPServlet<RetailStoreServerImpl> {

	public static int electionId = 0;
	public ElectionServlet(int port, RetailStoreServerImpl owner) {
		super(port, owner);
	}

	@Override
	public void run() {						
		if (getOwner().getId() >= getOwner().getGroupMap().size()) {
			CoordinatorPacket coordinator = new CoordinatorPacket();
			coordinator.setLeaderId(getOwner().getId());
			getOwner().broadcast(coordinator);
			getOwner().setLeaderId(getOwner().getId());
		}
		else {
			ElectionPacket electionPacket = new ElectionPacket();
			electionId += 1;
			electionPacket.setId(electionId);
			electionPacket.setSenderId(getOwner().getId());
			
			getOwner().setElectionState(ElectionState.WAIT_FOR_REPLY);
			getOwner().broadcastHigherId(electionPacket);									
		}
		
	}
}
