package app.server;

import app.orb.RetailStorePackage.InsufficientQuantity;
import app.orb.RetailStorePackage.InvalidReturn;
import app.orb.RetailStorePackage.NoSuchItem;

public class RetailStoreServerFE extends RetailStoreServer {
	private String leader;
	
	public RetailStoreServerFE(String leader) {
		super();
		this.leader = leader;
	}

	@Override
	public void purchaseItem(String customerID, int itemID, int numberOfItem) throws NoSuchItem, InsufficientQuantity {
		getORBInterface(leader).purchaseItem(customerID, itemID, numberOfItem);
	}

	@Override
	public void returnItem(String customerID, int itemID, int numberOfItem) throws InvalidReturn {
		getORBInterface(leader).returnItem(customerID, itemID, numberOfItem);
	}

	@Override
	public boolean transferItem(int itemID, int numberOfItem) {
		return getORBInterface(leader).transferItem(itemID, numberOfItem);
	}

	@Override
	public String checkStock(int itemID) {
		return getORBInterface(leader).checkStock(itemID);
	}
	
	@Override
	public void exchange(String customerID, int boughtItemID, int boughtNumber,
			int desiredItemID, int desiredNumber) throws InvalidReturn, NoSuchItem, InsufficientQuantity {
		getORBInterface(leader).exchange(customerID, boughtItemID, boughtNumber, desiredItemID, desiredNumber);
	}
}
