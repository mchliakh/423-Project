package app.server.udpservlet;

import app.orb.RetailStorePackage.NoSuchItem;
import app.server.RetailStoreServerImpl;
import udp.FIFOObjectUDPServlet;

public class DispatchServlet extends FIFOObjectUDPServlet<RetailStoreServerImpl> {

	public DispatchServlet(int port, RetailStoreServerImpl owner) {
		super(port, owner);
	}
	
	public void run() {
		while (true) {
			Object obj = FIFOReceive();
			try {
				getOwner().dispatch(obj);
			} catch (NoSuchItem e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
