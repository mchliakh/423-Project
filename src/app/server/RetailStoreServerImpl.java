package app.server;

import java.util.*;
import java.io.*;
import java.net.*;
import packet.MethodRequest;
import app.orb.RetailStorePackage.InsufficientQuantity;
import app.orb.RetailStorePackage.InvalidReturn;
import app.orb.RetailStorePackage.NoSuchItem;
import app.server.StockServer;
import app.server.requests.*;

public class RetailStoreServerImpl extends RetailStoreServer {
	private final int INVENTORY_SIZE = 10;
	private final int ITEM_ID_OFFSET = 1000;
	private final int ITEM_MAX_QUANTITY = 40;
	
	private final int M_SERVER_PORT = 4445;
	private final int T_SERVER_PORT = 4446;
	private final int V_SERVER_PORT = 4447;
	
	private final int M_CLIENT_PORT = 4448;
	private final int T_CLIENT_PORT = 4449;
	private final int V_CLIENT_PORT = 4450;
	
	private String storeCode; 
	private Hashtable<Integer, Integer> inventory = new Hashtable<Integer, Integer>();
	private ArrayList<String> proximityList = new ArrayList<String>();
	private HashMap<String, Integer> portMap = new HashMap<String, Integer>();
	private Thread stockServer;
	private int clientPort;
	
	/**
	 * A new instance of RetailStoreServer creates an inventory of INVENTORY_SIZE
	 * items and randomly generates a quantity between 0 and ITEM_MAX_QUANTITY
	 * for each item. Item IDs start at ITEM_ID_OFFSET.
	 */
	public RetailStoreServerImpl(String storeCode) {
		this.storeCode = storeCode;
		
		// build port mapping
		portMap.put("M", M_SERVER_PORT);
		portMap.put("T", T_SERVER_PORT);
		portMap.put("V", V_SERVER_PORT);
		
		// build proximity list and determine client port
		switch (storeCode.toCharArray()[0]) {
			case 'M':
				proximityList.add("T");
				proximityList.add("V");
				clientPort = M_CLIENT_PORT;
				break;
			case 'T':
				proximityList.add("M");
				proximityList.add("V");
				clientPort = T_CLIENT_PORT;
				break;
			case 'V':
				proximityList.add("T");
				proximityList.add("M");
				clientPort = V_CLIENT_PORT;
				break;
		}
		
		// initialize stock server on the appropriate port
		try {
			stockServer = new Thread(new StockServer(this, portMap.get(storeCode)));
			stockServer.start();
		} catch (SocketException e) {
			System.err.println("Failed to start stock server on port " + portMap.get(storeCode) + "!");
			System.err.println("Server exception: " + e.toString());
		    e.printStackTrace();
		}
		
		// seed inventory with random stock
		Random generator = new Random();
		for (int i = 0; i < INVENTORY_SIZE; i++) {
			inventory.put(ITEM_ID_OFFSET + i, generator.nextInt(ITEM_MAX_QUANTITY));
		}
	}

	@Override
	public void purchaseItem(String customerID, int itemID, int numberOfItem) throws NoSuchItem, InsufficientQuantity {
		PurchaseItem request = new PurchaseItem(customerID, itemID, numberOfItem);		
		
		// FIFO send request over UDP
	}
	
	private synchronized void localPurchaseItem(String customerID, int itemID, int numberOfItem) throws NoSuchItem {
		if (!inventory.containsKey(itemID)) {
			throw new NoSuchItem();
		} else if (inventory.get(itemID) < numberOfItem) {
			//attemptTransfer(itemID, numberOfItem);
		} else {
			inventory.put(itemID, Integer.valueOf(inventory.get(itemID).intValue() - numberOfItem));
		}
		
		try
		{
			recordTransaction(customerID, itemID, numberOfItem);
		} catch (FileNotFoundException e) {
			System.err.println("Could not open file!");
		}
		
		System.out.println("Customer " + customerID + " purchased " + numberOfItem + " of item " + itemID + ".");
		printInventory();
	}

	@Override
	public void returnItem(String customerID, int itemID, int numberOfItem) throws InvalidReturn {
		ReturnItem request = new ReturnItem(customerID, itemID, numberOfItem);		
		
		// FIFO send request over UDP	
	}
	
	public void localReturnItem(String customerID, int itemID, int numberOfItem) {
		if (numberOfItem > getTotalPurchased(customerID)) {
//			throw new InvalidReturn(); 
		} else {
			inventory.put(itemID, Integer.valueOf(inventory.get(itemID).intValue() + numberOfItem));

			try
			{
				recordTransaction(customerID, itemID, numberOfItem * -1);
			} catch (FileNotFoundException e) {
				System.err.println("Could not open file!");
			}
			
			System.out.println("Customer " + customerID + " returned " + numberOfItem + " of item " + itemID + ".");
			printInventory();
		}
	}

	@Override
	public boolean transferItem(int itemID, int numberOfItem) {
		TransferItem request = new TransferItem(itemID, numberOfItem);		
		
		return false; //TODO: Retrun true value
		// FIFO send request over UDP	
	}
	
	public synchronized boolean localTransferItem(int itemID, int numberOfItem) {
		System.out.println("Received transfer attempt for "+ numberOfItem + " of item " + itemID + ".");
		if (!inventory.containsKey(itemID) || inventory.get(itemID) < numberOfItem) return false;
		inventory.put(itemID, Integer.valueOf(inventory.get(itemID).intValue() - numberOfItem));
		System.out.println("Transfered " + numberOfItem + " of item " + itemID + ".");
		printInventory();
		return true;
	}

	@Override
	public String checkStock(int itemID) {
		
		CheckStock request = new CheckStock(itemID);
				
		return ""; //TODO: Return valid value
		// FIFO send request over UDP	
	}
	
	public String localCheckStock(int itemID) {
		Hashtable<String, String> stock = new Hashtable<String, String>();
		String[] received;
		for (String storeCode : proximityList) {
			try {
				synchronized (this) {
					// get a datagram socket
			        DatagramSocket socket = new DatagramSocket(clientPort);
			 
			        // send request
			        byte[] buf = new byte[256];
			        buf = (itemID + "").getBytes();
			        InetAddress address = InetAddress.getByName("localhost");
			        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, portMap.get(storeCode));
			        socket.send(packet);
			     
			        // get response
			        packet = new DatagramPacket(buf, buf.length);
			        socket.receive(packet);
			 
			        // extract stock
			        received = (new String(packet.getData(), 0, packet.getLength())).split(",");
			        stock.put(received[0], received[1]);
			     
			        socket.close();
				}
			} catch (Exception e) {
				System.err.println("Tried port " + clientPort);
				System.err.println("Server exception: " + e.toString());
			    e.printStackTrace();
			}
		}
		// wrap stock hash in MyHashtable to satisfy the IDL 
		return stock.toString();
	}
	
	@Override
	public void exchange(String customerID, int boughtItemID, int boughtNumber,
			int desiredItemID, int desiredNumber) throws InvalidReturn, NoSuchItem, InsufficientQuantity {
		Exchange request = new Exchange(customerID, boughtItemID, boughtNumber, desiredItemID, desiredNumber);
				
		// FIFO send request over UDP
	}
	
	public void localExchange(String customerID, int boughtItemID, int boughtNumber,
			int desiredItemID, int desiredNumber) {
		if (desiredNumber > getTotalPurchased(customerID)) {
			//throw new InvalidReturn(); 
		} else {
			//purchaseItem(customerID, desiredItemID, desiredNumber);
		}
		//returnItem(customerID, boughtItemID, boughtNumber);
		//TODO: Handle exceptions
		System.out.println("Customer " + customerID + " exchanged " + boughtNumber + " of item " + boughtItemID + " for " +
				desiredNumber + " of item " + desiredItemID + ".");
		printInventory();
	}
	
	public void dispatch(Object obj) throws NoSuchItem {		
		@SuppressWarnings("unchecked")
		MethodRequest<RetailStoreRemoteMethod> req = MethodRequest.class.cast(obj);
		
		switch ((RetailStoreRemoteMethod) req.getRemoteMethod()) {
			case CHECK_STOCK:				
				CheckStock checkStockReq = (CheckStock) req;				
				localCheckStock(checkStockReq.getItemID());
				break;
				
			case EXCHANGE:
				Exchange exchangeReq = (Exchange) req;
				localExchange(
					exchangeReq.getCustomerID(),
					exchangeReq.getBoughtItemID(),
					exchangeReq.getBoughtNumber(),
					exchangeReq.getDesiredItemID(),
					exchangeReq.getDesiredNumber()
				);
				break;
				
			case PURCHASE_ITEM:
				PurchaseItem purchaseItemReq = (PurchaseItem) req;
				localPurchaseItem(
					purchaseItemReq.getCustomerID(),
					purchaseItemReq.getItemID(),
					purchaseItemReq.getNumberOfItem()
				);
				break;
				
			case RETURN_ITEM:
				ReturnItem returnItemReq = (ReturnItem) req;
				localReturnItem(
					returnItemReq.getCustomerID(),
					returnItemReq.getItemID(),
					returnItemReq.getNumberOfItem()
				);
				break;
				
			case TRANSFER_ITEM:
				TransferItem transferItemReq = (TransferItem) req;
				localTransferItem(
					transferItemReq.getItemID(),
					transferItemReq.getNumberOfItem()
				);
				break;
				
			default:
				break;
		}	
	
	}
	
	public String getStoreCode() {
		return storeCode;
	}
	
	public String getInventory() {
		return inventory.toString();
	}
	
	private void printInventory() {
		System.out.println("Contents of inventory are now: " + inventory);
	}
	
	public int getStock(int itemID) {
		return inventory.get(itemID);
	}
	
	private synchronized void recordTransaction(String customerID, int itemID, int numberOfItem) throws FileNotFoundException {
		PrintWriter outputStream = new PrintWriter(new FileOutputStream(storeCode + "\\" + customerID + ".txt", true));
		outputStream.println(customerID + "," + itemID + "," + numberOfItem);
		outputStream.close();
	}
	
	private synchronized int getTotalPurchased(String customerID) {
		Scanner inputStream = null;
		
		try
		{
			inputStream = new Scanner(new FileInputStream(storeCode + "\\" + customerID + ".txt"));
		} catch (FileNotFoundException e) {
			
		}
		
		String line;
		int total = 0;
		while (inputStream.hasNextLine()) {
			line = inputStream.nextLine();
			total += Integer.valueOf(line.split(",")[2]);
		}
		
		inputStream.close();
		return total;
	}
		
	private void attemptTransfer(int itemID, int numberOfItem) throws InsufficientQuantity {
		boolean success = false;
		for (String storeCode : proximityList) {
			try {
			    success = getORBInterface(storeCode).transferItem(itemID, numberOfItem);
			    if (success) break;
			} catch (Exception e) {
			    System.err.println("Client exception: " + e.toString());
			    e.printStackTrace();
			}
		}
		if (!success) throw new InsufficientQuantity();
	}
}
