package app.server.udpservers;

import app.orb.RetailStorePackage.NoSuchItem;
import app.server.RetailStoreServerImpl;
import udp.ObjectUDPServer;

public class DispatchObjectUDPServer extends ObjectUDPServer<RetailStoreServerImpl> {

  private class ServerDetails {
      private int id;
      private Timestamp timestamp; 
      private int attempts;

      public void resetAttempts() { attempts = 0; }
      public void increaseAttempts() { attempts += 1; }

      public int getId() { return id; }
      public Timestamp getTimestamp { return timestamp; }
      public int getAttempts { return attemps; }
  }

  private ArrayList<ServerDetails> servers = new ArrayList<ServerDetails>();

	public DispatchObjectUDPServer(int port, RetailStoreServerImpl owner) {
		super(port, owner);
	}
	
	public void run() {
		while (true) {
			Object obj = receive();

      updateServer(obj);
       
			try {
				getOwner().dispatch(obj);
			} catch (NoSuchItem e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
