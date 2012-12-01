package app.server.requests;

import packet.MethodRequest;

public class TransferItem extends MethodRequest<RetailStoreRemoteMethod> {	
	int itemID;
	int numberOfItem;
	
	public TransferItem(int itemID, int numberOfItem) {
		super(RetailStoreRemoteMethod.TRANSFER_ITEM);		
		this.itemID = itemID;
		this.numberOfItem = numberOfItem;
	}
	
	public int getItemID() {
		return itemID;
	}
	
	public int getNumberOfItem() {
		return numberOfItem;
	}
}
