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
		getORBInterface(getStoreCode()).purchaseItem(customerID, itemID, numberOfItem);
	}

	@Override
	public void returnItem(String customerID, int itemID, int numberOfItem) throws InvalidReturn {
		getORBInterface(getStoreCode()).returnItem(customerID, itemID, numberOfItem);
	}

	@Override
	public boolean transferItem(int itemID, int numberOfItem) {
		return getORBInterface(getStoreCode()).transferItem(itemID, numberOfItem);
	}

	@Override
	public String checkStock(int itemID) {
		return getORBInterface(getStoreCode()).checkStock(itemID);
	}
	
	@Override
	public void exchange(String customerID, int boughtItemID, int boughtNumber,
			int desiredItemID, int desiredNumber) throws InvalidReturn, NoSuchItem, InsufficientQuantity {
		getORBInterface(getStoreCode()).exchange(customerID, boughtItemID, boughtNumber, desiredItemID, desiredNumber);
	}
}
