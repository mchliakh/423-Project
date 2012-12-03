package app.test;

import app.client.RetailStoreClientImplDriver1;

public class TestCase1 {
	
	public static void main(String[] args) {
		try {
			(new Thread(new RetailStoreClientImplDriver1("M00001"))).start();
			(new Thread(new RetailStoreClientImplDriver1("M00002"))).start();
			(new Thread(new RetailStoreClientImplDriver1("M00003"))).start();
			(new Thread(new RetailStoreClientImplDriver1("M00004"))).start();
			(new Thread(new RetailStoreClientImplDriver1("M00005"))).start();
			(new Thread(new RetailStoreClientImplDriver1("M00006"))).start();
			(new Thread(new RetailStoreClientImplDriver1("M00007"))).start();
			(new Thread(new RetailStoreClientImplDriver1("M00008"))).start();
			(new Thread(new RetailStoreClientImplDriver1("M00009"))).start();
			(new Thread(new RetailStoreClientImplDriver1("M00010"))).start();
			(new Thread(new RetailStoreClientImplDriver1("M00011"))).start();
			(new Thread(new RetailStoreClientImplDriver1("M00012"))).start();
			(new Thread(new RetailStoreClientImplDriver1("M00013"))).start();
			(new Thread(new RetailStoreClientImplDriver1("M00014"))).start();
			(new Thread(new RetailStoreClientImplDriver1("M00015"))).start();
		} catch (Exception e) {
		    System.err.println("Client exception: " + e.toString());
		    e.printStackTrace();
		}
	}
	
}