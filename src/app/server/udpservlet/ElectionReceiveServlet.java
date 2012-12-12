package app.server.udpservlet;

import java.net.SocketTimeoutException;

import packet.CoordinatorPacket;
import udp.FIFOObjectUDPServlet;
import utils.LiteLogger;
import app.server.Config;
import app.server.RetailStoreServerImpl;

public class ElectionReceiveServlet extends FIFOObjectUDPServlet<RetailStoreServerImpl> {


	private static final long serialVersionUID = 6453595686631867382L;
	public static int electionId = 0;
	
	public ElectionReceiveServlet(int port, RetailStoreServerImpl owner) {
		super(port, owner);
	}

	@Override
	public void run() {		
		while (true) {			
			int timeout  = 1000;						
			Object packet;
			try {

				getOwner().setElectionState(ElectionState.WAIT_FOR_REPLY); //TODO: to remove, for testing only
				packet = receiveWithTimeout(timeout);
				
				/*if (packet instanceof ElectionPacket) {
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
				}*/
				
			} catch (SocketTimeoutException e) {
				System.out.println(e.getMessage() + "\n" + e.getStackTrace());
				
				if (getOwner().getElectionState() == ElectionState.WAIT_FOR_REPLY) {
					LiteLogger.log("Election state = ", getOwner().getElectionState());
					
					CoordinatorPacket coordinator = new CoordinatorPacket();
					coordinator.setLeaderId(getOwner().getId());
					LiteLogger.log("New coordinator object created. Setting leader to ", getOwner().getId());
					
					//getOwner().broadcastAll(coordinator, Config.ELECTION_LISTEN_PORT);
					getOwner().setLeaderId(getOwner().getId());
					getOwner().setElectionState(ElectionState.IDLE);
				}
				else if (getOwner().getElectionState() == ElectionState.WAIT_FOR_LEADER) {
					//(new Thread(new ElectionServlet(Config.ELECTION_IN_PORT, getOwner()))).start();					
				}
			}							
		}
	}
}
