package app.server.udpservlet;

import java.util.Properties;

import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import packet.CoordinatorPacket;
import packet.ElectionPacket;
import udp.FIFOObjectUDPServlet;
import utils.LiteLogger;
import app.server.Config;
import app.server.RetailStoreServerImpl;

public class ElectionServlet extends FIFOObjectUDPServlet<RetailStoreServerImpl> {

	public static int electionId = 0;
	public ElectionServlet(int port, RetailStoreServerImpl owner) {
		super(port, owner);
	}

	@Override
	public void run() {		
		LiteLogger.log("Starting ElectionServlet...");
		if (getOwner().getId() >= getOwner().getLeaderId() - 1) {
			LiteLogger.log("id = ",getOwner().getId(), " is the new leader by default");	
			CoordinatorPacket coordinator = new CoordinatorPacket();
			coordinator.setLeaderId(getOwner().getId());
			getOwner().broadcastAllNonFifo(coordinator, Config.ELECTION_RECEIVE_LISTEN_PORT);
			getOwner().setLeaderId(getOwner().getId());
			
			Properties props = System.getProperties();
			props.setProperty("org.omg.CORBA.ORBClass", "com.sun.corba.se.internal.POA.POAORB");
			props.setProperty("org.omg.CORBA.ORBSingletonClass", "com.sun.corba.se.internal.corba.ORBSingleton");
			props.setProperty("org.omg.CORBA.ORBInitialPort", "1050");
	        props.setProperty("org.omg.CORBA.ORBInitialHost", Config.FRONT_END_NAME);

			org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(new String[] {"M"}, props);
			try {
				org.omg.CORBA.Object obj = orb.resolve_initial_references("RootPOA");
				org.omg.CORBA.Object ncobj = orb.resolve_initial_references("NameService");
				NamingContextExt nc = NamingContextExtHelper.narrow(ncobj);
				nc.rebind(nc.to_name("M3"), obj);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		else {
			LiteLogger.log("id = ",getOwner().getId(), " is NOT the new leader. Broadcasting to higher ids");	
			ElectionPacket electionPacket = new ElectionPacket();
			electionId += 1;
			electionPacket.setId(electionId);
			electionPacket.setSenderId(getOwner().getId());
			
			getOwner().setElectionState(ElectionState.WAIT_FOR_REPLY);
			//getOwner().broadcastHigherId(electionPacket);									
		}
		
	}
}
