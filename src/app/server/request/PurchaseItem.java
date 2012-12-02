package app.server.request;

import packet.StatusPacket;

public class PurchaseItem extends StatusPacket<RetailStoreRemoteMethod> {
	String customerID;
	int itemID;
	int numberOfItem;
	
	public PurchaseItem(String customerID, int itemID, int numberOfItem) {
		super(RetailStoreRemoteMethod.PURCHASE_ITEM);
		this.customerID = customerID;
		this.itemID = itemID;
		this.numberOfItem = numberOfItem;
	}
	
	public String getCustomerID() {
		return customerID;
	}
	
	public int getItemID() {
		return itemID;
	}
	
	public int getNumberOfItem() {
		return numberOfItem;
	}
}
