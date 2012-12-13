package app.server;


import udp.ObjectUDP;
import utils.LiteLogger;
import app.orb.RetailStorePackage.InsufficientQuantity;
import app.orb.RetailStorePackage.InvalidReturn;
import app.orb.RetailStorePackage.NoSuchItem;


public class RetailStoreServerFE extends RetailStoreServer {
	
	public RetailStoreServerFE(String storeCode) {
		super(storeCode);
		
		Thread receiveThread = new Thread(new ReceiveThread());
		receiveThread.start();
	}

	@Override
	public void purchaseItem(String customerID, int itemID, int numberOfItem) throws NoSuchItem, InsufficientQuantity {
		System.out.println("Received \"purchaseItem\", forwarding to leader.");
		getORBInterface(getStoreCode() + MAX_ID).purchaseItem(customerID, itemID, numberOfItem);
	}

	@Override
	public void returnItem(String customerID, int itemID, int numberOfItem) throws InvalidReturn {
		System.out.println("Received \"returnItem\", forwarding to leader.");
		getORBInterface(getStoreCode() + MAX_ID).returnItem(customerID, itemID, numberOfItem);
	}

	@Override
	public boolean transferItem(int itemID, int numberOfItem) {
		System.out.println("Received \"transferItem\", forwarding to leader.");
		return getORBInterface(getStoreCode() + MAX_ID).transferItem(itemID, numberOfItem);
	}

	@Override
	public String checkStock(int itemID) {
		System.out.println("Received \"checkStock\", forwarding to leader.");
		return getORBInterface(getStoreCode() + MAX_ID).checkStock(itemID);
	}
	
	@Override
	public void exchange(String customerID, int boughtItemID, int boughtNumber,
			int desiredItemID, int desiredNumber) throws InvalidReturn, NoSuchItem, InsufficientQuantity {
		System.out.println("Received \"exchange\", forwarding to leader.");
		getORBInterface(getStoreCode() + MAX_ID).exchange(customerID, boughtItemID, boughtNumber, desiredItemID, desiredNumber);
	}
	
	
	//-----------------------------------------------------------------------------
	private class ReceiveThread implements Runnable {
		Thread runner;
		ObjectUDP udp;
		public ReceiveThread() {
			udp = new ObjectUDP(Config.FE_LISTEN_PORT);
		}

		public void run() {
			while (true) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				LiteLogger.log("Waiting to receive new leader id...");
				Object obj = udp.receive();
								
				MAX_ID = (Integer)obj;			
			} //end while
		} //end run
	}

}
