package app.server.request;

import packet.StatusPacket;

public class ReturnItem extends StatusPacket<RetailStoreRemoteMethod> {
	String customerID;
	int itemID;
	int numberOfItem;
	
	public ReturnItem(String customerID, int itemID, int numberOfItem) {
		super(RetailStoreRemoteMethod.RETURN_ITEM);
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
