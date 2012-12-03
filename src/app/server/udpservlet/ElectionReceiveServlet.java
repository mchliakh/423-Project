package app.server.udpservers;

import java.net.SocketTimeoutException;
import packet.AnswerPacket;
import packet.CoordinatorPacket;
import packet.ElectionPacket;
import udp.FIFOObjectUDPServlet;
import app.server.Config;
import app.server.RetailStoreServerImpl;

public class ElectionReceiveServlet extends FIFOObjectUDPServlet<RetailStoreServerImpl> {

	public static int electionId = 0;
	public ElectionReceiveServlet(int port, RetailStoreServerImpl owner) {
		super(port, owner);
	}

	@Override
	public void run() {		
		while (true) {			
			int timeout  = 50;						
			Object packet;
			try {
				packet = receiveWithTimeout(timeout);
				
				if (packet instanceof ElectionPacket) {
					ElectionPacket electionPacket = (ElectionPacket)packet;
					if (getOwner().getElectionState() == ElectionState.IDLE) {
						AnswerPacket answerPacket = new AnswerPacket();
						answerPacket.setElectionId(electionPacket.getId());
					}
				}
				else if (packet instanceof AnswerPacket) { 
					getOwner().setElectionState(ElectionState.WAIT_FOR_LEADER);
				}
				else if (packet instanceof CoordinatorPacket) {					
					getOwner().setElectionState(ElectionState.IDLE);
					getOwner().setLeaderId( CoordinatorPacket.class.cast(packet).getLeaderId());
				}
				
			} catch (SocketTimeoutException e) {
				if (getOwner().getElectionState() == ElectionState.WAIT_FOR_REPLY) {
					CoordinatorPacket coordinator = new CoordinatorPacket();
					coordinator.setLeaderId(getOwner().getId());
					getOwner().broadcast(coordinator);
					getOwner().setLeaderId(getOwner().getId());
					getOwner().setElectionState(ElectionState.IDLE);
				}
				else if (getOwner().getElectionState() == ElectionState.WAIT_FOR_LEADER) {
					(new Thread(new ElectionServlet(Config.ELECTION_IN_PORT, getOwner()))).start();					
				}
			}							
		}
	}
}
