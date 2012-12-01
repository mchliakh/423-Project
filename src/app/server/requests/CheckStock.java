package app.server.requests;

import packet.MethodRequest;

public class CheckStock extends MethodRequest<RetailStoreRemoteMethod> {
	int itemID;	
	
	public CheckStock(int itemID) {
		super(RetailStoreRemoteMethod.CHECK_STOCK);		
		this.itemID = itemID;	
	}

	public int getItemID() {
		return itemID;
	}

}
