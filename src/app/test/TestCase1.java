package app.test;

import app.client.RetailStoreClientImpl;

public class TestCase1 {
	
	public static void main(String[] args) {
		try {
			(new Thread(new RetailStoreClientImpl("M00001", false))).start();
			(new Thread(new RetailStoreClientImpl("M00002", false))).start();
			(new Thread(new RetailStoreClientImpl("M00003", false))).start();
			(new Thread(new RetailStoreClientImpl("M00004", false))).start();
			(new Thread(new RetailStoreClientImpl("M00005", false))).start();
			(new Thread(new RetailStoreClientImpl("M00006", false))).start();
			(new Thread(new RetailStoreClientImpl("M00007", false))).start();
			(new Thread(new RetailStoreClientImpl("M00008", false))).start();
			(new Thread(new RetailStoreClientImpl("M00009", false))).start();
			(new Thread(new RetailStoreClientImpl("M00010", false))).start();
			(new Thread(new RetailStoreClientImpl("M00011", false))).start();
			(new Thread(new RetailStoreClientImpl("M00012", false))).start();
			(new Thread(new RetailStoreClientImpl("M00013", false))).start();
			(new Thread(new RetailStoreClientImpl("M00014", false))).start();
			(new Thread(new RetailStoreClientImpl("M00015", false))).start();
		} catch (Exception e) {
		    System.err.println("Client exception: " + e.toString());
		    e.printStackTrace();
		}
	}
	
}