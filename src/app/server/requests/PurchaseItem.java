package app.server.requests;

import packet.MethodRequest;

public class PurchaseItem extends MethodRequest<RetailStoreRemoteMethod> {
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
