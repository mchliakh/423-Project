package app.server.request;

import packet.StatusPacket;

public class TransferItem extends StatusPacket<RetailStoreRemoteMethod> {	
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
