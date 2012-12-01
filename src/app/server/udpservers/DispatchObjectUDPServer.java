package app.server.udpservers;

import app.orb.RetailStorePackage.NoSuchItem;
import app.server.RetailStoreServerImpl;
import udp.ObjectUDPServer;

public class DispatchObjectUDPServer extends ObjectUDPServer<RetailStoreServerImpl> {

	public DispatchObjectUDPServer(int port, RetailStoreServerImpl owner) {
		super(port, owner);
	}
	
	public void run() {
		while (true) {
			Object obj = receive();
			try {
				getOwner().dispatch(obj);
			} catch (NoSuchItem e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
