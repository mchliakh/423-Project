package app.test;

import app.client.RetailStoreClientImpl;

public class TestCase2 {
	
	public static void main(String[] args) {
		try {
			(new Thread(new RetailStoreClientImpl("M00001", true))).start();
			(new Thread(new RetailStoreClientImpl("T00001", true))).start();
			(new Thread(new RetailStoreClientImpl("V00001", true))).start();
		} catch (Exception e) {
		    System.err.println("Client exception: " + e.toString());
		    e.printStackTrace();
		}
	}
	
}