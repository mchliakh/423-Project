package app.client;
/*
 * The client implementation is generated by the ORB Studio.
 */

import java.io.IOException;
import java.util.Properties;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import app.orb.RetailStorePackage.InsufficientQuantity;
import app.orb.RetailStorePackage.InvalidReturn;
import app.orb.RetailStorePackage.NoSuchItem;
import app.server.Config;

public abstract class RetailStoreClientImpl implements Runnable {
	public final int INVENTORY_SIZE = 10;
	public final int ITEM_ID_OFFSET = 1000;
	public final int PURCHASE_QUANTITY = 2;
	public final int RETURN_QUANTITY = 2;
	
	private String customerID;
	private app.orb.RetailStore store = null;
	private org.omg.CORBA.ORB orb = null;

	/**
	 * Constructor for RetailStoreClientImpl
	 * 
	 * @throws IOException
	 */
	public RetailStoreClientImpl(String customerID, boolean checkStock) throws IOException {
		this(customerID, null);
	}

	/**
	 * Constructor for RetailStoreClientImpl
	 * 
	 * @throws IOException
	 * @see java.lang.Object#Object()
	 */
	public RetailStoreClientImpl(String customerID, String[] args) throws IOException {
		this.customerID = customerID;
		String storeCode = customerID.toCharArray()[0] + "0"; // cast to String
		
		initORB(storeCode, args);
	}
	
	public void purchaseItem(int itemID, int numberOfItem) throws NoSuchItem, InsufficientQuantity {
		store.purchaseItem(customerID, itemID, numberOfItem);
	}
	
	public void returnItem(int itemID, int numberOfItem) throws InvalidReturn {
		store.returnItem(customerID, itemID, numberOfItem);
	}
	
	public String checkStock(int itemID) {
		return store.checkStock(itemID);
	}
	
	public void exchange(int boughtItemID, int boughtNumber,
			int desiredItemID, int desiredNumber) throws InvalidReturn, NoSuchItem, InsufficientQuantity {
		store.exchange(customerID, boughtItemID, boughtNumber, desiredItemID, desiredNumber);
	}

	/**
	 * Initialize ORB.
	 *  
	 * @param args
	 * @throws IOException
	 */
	public void initORB(String storeCode, String[] args) throws IOException {

		Properties props = System.getProperties();
		props.setProperty("org.omg.CORBA.ORBClass", "com.sun.corba.se.internal.POA.POAORB");
		props.setProperty("org.omg.CORBA.ORBSingletonClass", "com.sun.corba.se.internal.corba.ORBSingleton");
		props.setProperty("org.omg.CORBA.ORBInitialPort", "1050");
        props.setProperty("org.omg.CORBA.ORBInitialHost", Config.FRONT_END_NAME);

		// Initialize the ORB
		orb = org.omg.CORBA.ORB.init((String[])args, props);

		// ---- Uncomment below to enable Naming Service access. ----
		org.omg.CORBA.Object ncobj = null;
		try {
			ncobj = orb.resolve_initial_references("NameService");
		} catch (InvalidName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		NamingContextExt nc = NamingContextExtHelper.narrow(ncobj);
		org.omg.CORBA.Object obj = null;
		try {
			obj = nc.resolve_str(storeCode);
		} catch (NotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotProceed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		store = app.orb.RetailStoreHelper.narrow(obj);		
	}

	/**
	 * Shutdown ORB.
	 */
	public void shutdown() {
		orb.shutdown(true);
	}

	/**
	 * Test driver for RetailStoreClientImpl.
	 */
	public abstract void run();
	
	public String getCustomerID() {
		return customerID;
	}
}
