package app.server.request;

import packet.StatusPacket;

public class CheckStock extends StatusPacket<RetailStoreRemoteMethod> {
	int itemID;	
	
	public CheckStock(int itemID) {
		super(RetailStoreRemoteMethod.CHECK_STOCK);		
		this.itemID = itemID;	
	}

	public int getItemID() {
		return itemID;
	}

}
