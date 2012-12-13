package app.server;

import java.util.*;
import java.io.*;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import app.orb.RetailStorePackage.InsufficientQuantity;
import app.orb.RetailStorePackage.InvalidReturn;
import app.orb.RetailStorePackage.NoSuchItem;

public abstract class RetailStoreServer extends app.orb.RetailStorePOA {
	public int MAX_ID = 3;
	
	private String storeCode;
	
	private org.omg.CORBA.ORB orb = null;
	private NamingContextExt nc;
	
	public RetailStoreServer(String storeCode) {
		this.storeCode = storeCode;
		
		// initialize the orb
		try {
			initORB(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public abstract void purchaseItem(String customerID, int itemID, int numberOfItem) throws NoSuchItem, InsufficientQuantity;

	@Override
	public abstract void returnItem(String customerID, int itemID, int numberOfItem) throws InvalidReturn;

	@Override
	public abstract boolean transferItem(int itemID, int numberOfItem);

	@Override
	public abstract String checkStock(int itemID);
	
	@Override
	public abstract void exchange(String customerID, int boughtItemID, int boughtNumber,
			int desiredItemID, int desiredNumber) throws InvalidReturn, NoSuchItem, InsufficientQuantity;
	
	public String getStoreCode() {
		return storeCode;
	}
	
	public NamingContextExt getNamingContext() {
		return nc;
	}
	
	public app.orb.RetailStore getORBInterface(String name) {
		org.omg.CORBA.Object obj = null;
		
		try {
			obj = getNamingContext().resolve_str(name);
		} catch (NotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotProceed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return app.orb.RetailStoreHelper.narrow(obj);		
	}
	
	private void initORB(String[] args) throws IOException {
		Properties props = System.getProperties();
		props.setProperty("org.omg.CORBA.ORBClass", "com.sun.corba.se.internal.POA.POAORB");
		props.setProperty("org.omg.CORBA.ORBSingletonClass", "com.sun.corba.se.internal.corba.ORBSingleton");
		props.setProperty("org.omg.CORBA.ORBInitialPort", "1050");
        props.setProperty("org.omg.CORBA.ORBInitialHost", "localhost");

		// Initialize the ORB
		orb = org.omg.CORBA.ORB.init((String[])args, props);

		// ---- Uncomment below to enable Naming Service access. ----
		org.omg.CORBA.Object ncobj = null;
		try {
			ncobj = orb.resolve_initial_references("NameService");
		} catch (org.omg.CORBA.ORBPackage.InvalidName e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		nc = NamingContextExtHelper.narrow(ncobj);
	}
}
