package app.client;

import java.io.IOException;

import app.orb.RetailStorePackage.InsufficientQuantity;
import app.orb.RetailStorePackage.NoSuchItem;

public class RetailStoreClientImplDriver2 extends RetailStoreClientImpl {

	public RetailStoreClientImplDriver2(String customerID)
			throws IOException {
		super(customerID, null);
	}
	
	public void run() {
		try {
			System.out.println("Attempting to purhcase 2 of 1000");
			purchaseItem(1000, 2); // should succeed
			System.out.println("Attempting to purhcase 2 of 1000");
			purchaseItem(1000, 2); // should succeed
			System.out.println("Attempting to purhcase 5 of 1001");
			purchaseItem(1001, 5); // should succeed
			System.out.println("Attempting to purhcase 10 of 1002");
			purchaseItem(1002, 10); // should succeed
			System.out.println("Attempting to purhcase 20 of 1003");
			purchaseItem(1003, 20); // should be out of stock
			
			System.out.println("Attempting to return 2 of 1000");
			returnItem(1000, 2); // should succeed
			System.out.println("Attempting to return 3 of 1000");
			returnItem(1000, 3); // should be invalid return
			
			System.out.println("Attempting to exhange 10 of 1002 for 2 of 1000");
			exchange(1002, 10, 1000, 2); // should succeed
			System.out.println("Attempting to exhange 21 of 1003 for 5 of 1001");
			exchange(1003, 21, 1001, 5); // should be invalid return
			System.out.println("Attempting to exhange 20 of 1003 for 20 of 1000");
			exchange(1003, 20, 1000, 20); // should be out of stock
		} catch (NoSuchItem e) {
			System.err.println(getCustomerID() + ": The requested item does not exist!");
		} catch (InsufficientQuantity e) {
			System.err.println(getCustomerID() + ": There is not enough stock to fulfill your order!");
		} catch (Exception e) {
		    System.err.println("Purchase exception: " + e.toString());
		    e.printStackTrace();
		}
		shutdown();
	}
}
