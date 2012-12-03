package app.server;

import app.orb.RetailStorePackage.InsufficientQuantity;
import app.orb.RetailStorePackage.InvalidReturn;
import app.orb.RetailStorePackage.NoSuchItem;

public class RetailStoreServerFE extends RetailStoreServer {
	
	public RetailStoreServerFE(String storeCode) {
		super(storeCode);
	}

	@Override
	public void purchaseItem(String customerID, int itemID, int numberOfItem) throws NoSuchItem, InsufficientQuantity {
		getORBInterface(getStoreCode() + MAX_ID).purchaseItem(customerID, itemID, numberOfItem);
	}

	@Override
	public void returnItem(String customerID, int itemID, int numberOfItem) throws InvalidReturn {
		getORBInterface(getStoreCode() + MAX_ID).returnItem(customerID, itemID, numberOfItem);
	}

	@Override
	public boolean transferItem(int itemID, int numberOfItem) {
		return getORBInterface(getStoreCode() + MAX_ID).transferItem(itemID, numberOfItem);
	}

	@Override
	public String checkStock(int itemID) {
		return getORBInterface(getStoreCode() + MAX_ID).checkStock(itemID);
	}
	
	@Override
	public void exchange(String customerID, int boughtItemID, int boughtNumber,
			int desiredItemID, int desiredNumber) throws InvalidReturn, NoSuchItem, InsufficientQuantity {
		getORBInterface(getStoreCode() + MAX_ID).exchange(customerID, boughtItemID, boughtNumber, desiredItemID, desiredNumber);
	}
}
