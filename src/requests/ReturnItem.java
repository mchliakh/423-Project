package requests;

import packet.MethodRequest;

public class ReturnItem extends MethodRequest<RetailStoreRemoteMethod> {
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
	
	public int numberOfItem() {
		return numberOfItem;
	}
}
