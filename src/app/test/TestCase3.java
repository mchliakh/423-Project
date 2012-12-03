package app.test;

import app.client.RetailStoreClientImplDriver3;

public class TestCase3 {
	
	public static void main(String[] args) {
		try {
			(new Thread(new RetailStoreClientImplDriver3("M00001"))).start();
		} catch (Exception e) {
		    System.err.println("Client exception: " + e.toString());
		    e.printStackTrace();
		}
	}
	
}