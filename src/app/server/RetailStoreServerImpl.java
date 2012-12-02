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
import app.server.udpservers.*;

public class RetailStoreServerImpl extends RetailStoreServer {
	private final int INVENTORY_SIZE = 10;
	private final int ITEM_ID_OFFSET = 1000;
	private final int ITEM_MAX_QUANTITY = 40;
	
	private final int DISPATCH_IN_PORT = 4445;
	private final int DISPATCH_OUT_PORT = 4446;

	private Hashtable<Integer, Integer> inventory = new Hashtable<Integer, Integer>();
	private ArrayList<String> proximityList = new ArrayList<String>();
	private HashMap<Integer, GroupMember> groupMap = new HashMap<Integer, GroupMember>();
	
	public class GroupMember {
		private String host;
		private boolean isAlive = true;
		private boolean isLeader = false;
		
		public GroupMember(String host) {
			this.host = host;
		}
		
		public String getHost() {
			return host;
		}
		
		public boolean isAlive() {
			return isAlive;
		}
		
		public void setToAlive() {
			isAlive = true;
		}
		
		public void setToFailed() {
			isAlive = false;
		}
		
		public boolean isLeader() { 
			return isLeader; 
		}
	}
	
	private Thread dispatchServer;
	
	private int id;
	
	private boolean isLeader = false;
	
	/**
	 * A new instance of RetailStoreServer creates an inventory of INVENTORY_SIZE
	 * items and randomly generates a quantity between 0 and ITEM_MAX_QUANTITY
	 * for each item. Item IDs start at ITEM_ID_OFFSET.
	 */
	public RetailStoreServerImpl(String storeCode, int id) {
		super(storeCode);
		this.id = id;
		
		// TESTING: assume server with highest rank is leader
		if (hasHighestRank())
			isLeader = true;
		
		// build proximity list
		switch (storeCode.toCharArray()[0]) {
			case 'M':
				proximityList.add("T");
				proximityList.add("V");
				break;
			case 'T':
				proximityList.add("M");
				proximityList.add("V");
				break;
			case 'V':
				proximityList.add("T");
				proximityList.add("M");
				break;
		}
		
		// build group map
		groupMap.put(1, new GroupMember("nurse"));
		groupMap.put(2, new GroupMember("noyori"));
		groupMap.put(3, new GroupMember("north"));
		
		// launch dispatch server
		if (!isLeader) { // only if not the leader
			dispatchServer = new Thread(new DispatchObjectUDPServer(DISPATCH_IN_PORT, this));
			dispatchServer.start();
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
	
	@Override
	public void returnItem(String customerID, int itemID, int numberOfItem) throws InvalidReturn {
		ReturnItem request = new ReturnItem(customerID, itemID, numberOfItem);		
		
		// FIFO send request over UDP	
	}
	
	@Override
	public boolean transferItem(int itemID, int numberOfItem) {
		TransferItem request = new TransferItem(itemID, numberOfItem);		
		
		return false; //TODO: Retrun true value
		// FIFO send request over UDP	
	}
	
	@Override
	public String checkStock(int itemID) {
		
		CheckStock request = new CheckStock(itemID);
				
		return ""; //TODO: Return valid value
		// FIFO send request over UDP	
	}
	
	@Override
	public void exchange(String customerID, int boughtItemID, int boughtNumber,
			int desiredItemID, int desiredNumber) throws InvalidReturn, NoSuchItem, InsufficientQuantity {
		Exchange request = new Exchange(customerID, boughtItemID, boughtNumber, desiredItemID, desiredNumber);
				
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
	
	public synchronized boolean localTransferItem(int itemID, int numberOfItem) {
		System.out.println("Received transfer attempt for "+ numberOfItem + " of item " + itemID + ".");
		
		if (!inventory.containsKey(itemID) || inventory.get(itemID) < numberOfItem) { return false; }
		
		inventory.put(itemID, Integer.valueOf(inventory.get(itemID).intValue() - numberOfItem));
		System.out.println("Transfered " + numberOfItem + " of item " + itemID + ".");
		printInventory();
		return true;
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
		MethodRequest<RetailStoreRemoteMethod> req = (MethodRequest<RetailStoreRemoteMethod>) obj;
		
		switch ((RetailStoreRemoteMethod) req.getRemoteMethod()) {
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
								
			default:
				break;
		}	
	
	}
	
		public String getStoreCode() {
		return getStoreCode();
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
		PrintWriter outputStream = new PrintWriter(new FileOutputStream(getStoreCode() + "\\" + customerID + ".txt", true));
		outputStream.println(customerID + "," + itemID + "," + numberOfItem);
		outputStream.close();
	}
	
	private synchronized int getTotalPurchased(String customerID) {
		Scanner inputStream = null;
		
		try
		{
			inputStream = new Scanner(new FileInputStream(getStoreCode() + "\\" + customerID + ".txt"));
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
	
	private boolean hasHighestRank() {
		return id == MAX_ID;
	}
	
	public int getId() { return id; }
	public  HashMap<Integer, GroupMember> getGroupMap() { return groupMap; }
	
	public void broadcast(BasicRequest req) {
		for (GroupMember member : groupMap.values()) {
			if (member.+isAlive()) { send(member.getHost(), req); }
		}
	}
}
