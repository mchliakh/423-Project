package requests;

import packet.MethodRequest;

public class CheckStock extends MethodRequest<RetailStoreRemoteMethod> {
	int itemID;	
	
	public CheckStock(String customerID, int itemID, int numberOfItem) {
		super(RetailStoreRemoteMethod.CHECK_STOCK);		
		this.itemID = itemID;	
	}

	public int getItemID() {
		return itemID;
	}

}
