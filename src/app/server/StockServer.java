package app.server;

import java.io.IOException;
import java.net.*;

public class StockServer implements Runnable {
	private RetailStoreServerImpl store;
	private DatagramSocket socket = null;
	private boolean running = true;
	
	public StockServer(RetailStoreServerImpl store, int port) throws SocketException {
		this.store = store;
		socket = new DatagramSocket(port);
	}
	
	public void run() {
		while (running) {
			try {
				byte[] buf = new byte[256];
				
				// receive request
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				
				// get the item stock
				int itemID = Integer.parseInt(new String(packet.getData(), 0, packet.getLength()));
				int stock = store.getStock(itemID);
				buf = (store.getStoreCode() + "," + stock).getBytes();
				
				// send the response to the client at "address" and "port"
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
			} catch (IOException e) {
			    System.err.println("UDP exception: " + e.toString());
				e.printStackTrace();
				running = false;
			}
		}
		socket.close();
	}
}
