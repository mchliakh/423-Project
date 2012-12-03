package app.client;

import java.io.IOException;

import app.orb.RetailStorePackage.InsufficientQuantity;
import app.orb.RetailStorePackage.InvalidReturn;
import app.orb.RetailStorePackage.NoSuchItem;

public class RetailStoreClientImplDriver1 extends RetailStoreClientImpl {
	public RetailStoreClientImplDriver1(String customerID)
			throws IOException {
		super(customerID, null);
	}

	public void run() {
		for (int i = 0; i < INVENTORY_SIZE; i++) {
			try {
				// check the stock
				System.out.println(checkStock(ITEM_ID_OFFSET + i));
				// try to purchase the item
				purchaseItem(ITEM_ID_OFFSET + i, PURCHASE_QUANTITY);
			} catch (NoSuchItem e) {
				System.err.println(getCustomerID() + ": Item with ID " +  (ITEM_ID_OFFSET + i) + " does not exist!");
			} catch (InsufficientQuantity e) {
				System.err.println(getCustomerID() + ": There is not enough stock to fulfill your order!");
			} catch (Exception e) {
			    System.err.println("Purchase exception: " + e.toString());
			    e.printStackTrace();
			}
			System.out.println(getCustomerID() +  ": Successfully purchased " + PURCHASE_QUANTITY + " of item " + (ITEM_ID_OFFSET + i) + ".");
		}
		for (int i = INVENTORY_SIZE - 1; i >= 0; i--) {
			try {
				// check the stock
				System.out.println(checkStock(ITEM_ID_OFFSET + i));
				// try to return the item
				returnItem(ITEM_ID_OFFSET + i, RETURN_QUANTITY);
			} catch (InvalidReturn e) {
				System.err.println(getCustomerID() + ": Warning! You attempted to return more than you purchased.");
			} catch (Exception e) {
			    System.err.println("Return exception: " + e.toString());
			    e.printStackTrace();
			}
			System.out.println(getCustomerID() +  ": Successfully returned " + RETURN_QUANTITY + " of item " + (ITEM_ID_OFFSET + i) + ".");
		}
		try {
			// try to purchase an item
			purchaseItem(1002, PURCHASE_QUANTITY);
			System.out.println(getCustomerID() +  ": Successfully purchased " + PURCHASE_QUANTITY + " of item 1002.");
			// try to exchange the item
			exchange(1002, PURCHASE_QUANTITY, 1008, PURCHASE_QUANTITY);
		} catch (NoSuchItem e) {
			System.err.println(getCustomerID() + ": Item with ID 1008 does not exist!");
		} catch (InsufficientQuantity e) {
			System.err.println(getCustomerID() + ": There is not enough stock to fulfill your order!");
		} catch (InvalidReturn e) {
			System.err.println(getCustomerID() + ": Warning! You attempted to return more than you purchased.");
		} catch (Exception e) {
		    System.err.println("Return exception: " + e.toString());
		    e.printStackTrace();
		}
		System.out.println(getCustomerID() +  ": Successfully exchanged " + PURCHASE_QUANTITY + " of item 1002 for " +
				PURCHASE_QUANTITY + " of item 1008.");
		shutdown();
	}
}
