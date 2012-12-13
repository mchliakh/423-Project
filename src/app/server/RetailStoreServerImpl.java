
package app.server;

import java.util.*;
import java.io.*;

import packet.BasicPacket;
import packet.StatusPacket;
import udp.FIFOObjectUDP;
import udp.ObjectUDP;
import app.orb.RetailStorePackage.InsufficientQuantity;
import app.orb.RetailStorePackage.InvalidReturn;
import app.orb.RetailStorePackage.NoSuchItem;
import app.server.request.*;
import app.server.response.Response;
import app.server.response.ReturnStatus;
import app.server.udpservlet.*;
import utils.LiteLogger;

public class RetailStoreServerImpl extends RetailStoreServer {
	private final int INVENTORY_SIZE = 10;
	private final int ITEM_ID_OFFSET = 1000;
	private final int ITEM_MAX_QUANTITY = 40;
	

	private ElectionState electionState = ElectionState.IDLE;

	private Hashtable<Integer, Integer> inventory = new Hashtable<Integer, Integer>();
	private ArrayList<String> proximityList = new ArrayList<String>();
	private HashMap<Integer, GroupMember> groupMap = new HashMap<Integer, GroupMember>();
	
	private FIFOObjectUDP udp;
	private ObjectUDP udpSender;
	private FIFOObjectUDP udpFIFOSender;
	
	public class GroupMember {
		private String host;
		private boolean isAlive = true;
		private boolean isLeader = false;
		
		public GroupMember(String host) {
			this.host = host;
		}
		
		public GroupMember(String host, boolean isLeader) {
			this(host);
			this.isLeader = isLeader;
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
		
		public void setIsLeader(boolean isLeader) {
			this.isLeader = isLeader;
		}
	}
	
	private Thread dispatchServer;
	private Thread failureDetectionServer;
	private Thread electionReceiveServer;
	
	private int id;
	private int counter = 1;
	
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
		groupMap.put(1, new GroupMember(Config.SLAVE1_NAME));
		groupMap.put(2, new GroupMember(Config.SLAVE2_NAME));
		groupMap.put(3, new GroupMember(Config.LEADER_NAME, true));
		
		// launch dispatch server
		if (!isLeader) { // only if not the leader
			LiteLogger.log("Creating Slave for id", id);			
			dispatchServer = new Thread(new DispatchServlet(Config.SLAVES_LISTEN_PORT, this));
			udpSender      = new ObjectUDP(Config.SLAVES_UDP_SENDER_PORT);
			udpFIFOSender  = new FIFOObjectUDP(Config.SLAVES_UDP_SENDER_PORT + 10);
			dispatchServer.start();
			
//			FailureDetectorObjectUDPServer fd = new FailureDetectorObjectUDPServer(Config.IM_ALIVE_PORT, this);
//			fd.addServer(1);
//			fd.addServer(2);
//			fd.addServer(3);
//			failureDetectionServer = new Thread(fd);
//			failureDetectionServer.start();
		} else {
			LiteLogger.log("Creating Leader for id", id);
			udp = new FIFOObjectUDP(Config.LEADER_LISTEN_PORT);
			udpFIFOSender  = new FIFOObjectUDP(Config.SLAVES_UDP_SENDER_PORT + 11);
			
//			FailureDetectorObjectUDPServer fd = new FailureDetectorObjectUDPServer(Config.IM_ALIVE_PORT, this);
//			fd.addServer(1);
//			fd.addServer(2);
//			fd.addServer(3);
//			failureDetectionServer = new Thread(fd);
//			failureDetectionServer.start();
		}
		
		FailureDetectorObjectUDPServer fd = new FailureDetectorObjectUDPServer(Config.IM_ALIVE_PORT, this);
		fd.addServer(1);
		fd.addServer(2);
		fd.addServer(3);
		failureDetectionServer = new Thread(fd);
		failureDetectionServer.start();
		
		electionReceiveServer = new Thread(new ElectionReceiveServlet(Config.ELECTION_RECEIVE_LISTEN_PORT, this));
		electionReceiveServer.start();
		 // seed inventory with random stock
//		 Random generator = new Random();
//		 for (int i = 0; i < INVENTORY_SIZE; i++) {
//			inventory.put(ITEM_ID_OFFSET + i, generator.nextInt(ITEM_MAX_QUANTITY));
//		 }
		
		// seed inventory with stock
		inventory.put(1000, 60);
		inventory.put(1001, 10);
		inventory.put(1002, 15);
		inventory.put(1003, 10);
		inventory.put(1004, 5);
	}

	@Override
	public void purchaseItem(String customerID, int itemID, int numberOfItem) throws NoSuchItem, InsufficientQuantity {
		System.out.println("Received \"purchaseItem\", broadcating.");
		
		PurchaseItem req = new PurchaseItem(customerID, itemID, numberOfItem);
		req.setId(counter);		
		counter += 1;
		
		broadcast(req);
		
		int responseId = -2;
		Object resp = null;
		while (responseId != counter-1) {
			LiteLogger.log("\nLeader is waiting to receive answer ...\n");
			resp = udp.receive();
			LiteLogger.log("\nThe almighty has received an answer ...\n");
			responseId = ((Response) resp).getId();
		}
		switch ((ReturnStatus) ((Response) resp).getStatus()) {
			case SUCCESS:
				// return
				break;
			case INSUFFICIENT_QUANTITY:
				throw new InsufficientQuantity();
			case NO_SUCH_ITEM:
				throw new NoSuchItem();
			default:
				break;
		}
	}
	
	@Override
	public void returnItem(String customerID, int itemID, int numberOfItem) throws InvalidReturn {
		ReturnItem req = new ReturnItem(customerID, itemID, numberOfItem);
		req.setId(counter++);
		broadcast(req);
		int responseId = -2;
		Response resp = null;
		while (responseId != counter-1) {
			resp = (Response) udp.receive();
			responseId = resp.getId();
		}
		switch ((ReturnStatus) resp.getStatus()) {
			case SUCCESS:
				// return
				break;
			case INVALID_RETURN:
				throw new InvalidReturn();
			default:
				break;
		}
	}
	
	@Override
	public boolean transferItem(int itemID, int numberOfItem) {
		TransferItem req = new TransferItem(itemID, numberOfItem);		
		req.setId(counter++);
		broadcast(req);
		int responseId = -2;
		Response resp = null;
		while (responseId != counter-1) {
			resp = (Response) udp.receive();
			responseId = resp.getId();
		}
		switch ((ReturnStatus) resp.getStatus()) {
			case TRUE:
				return true;
			case FALSE:
				return false;
			default:
				return false;
		}	
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
		Exchange req = new Exchange(customerID, boughtItemID, boughtNumber, desiredItemID, desiredNumber);
		req.setId(counter++);
		broadcast(req);
		int responseId = -2;
		Response resp = null;
		while (responseId != counter-1) {
			resp = (Response) udp.receive();
			responseId = resp.getId();
		}
		switch ((ReturnStatus) resp.getStatus()) {
			case SUCCESS:
				// return
				break;
			case INSUFFICIENT_QUANTITY:
				throw new InsufficientQuantity();
			case INVALID_RETURN:
				throw new InvalidReturn();
			case NO_SUCH_ITEM:
				throw new NoSuchItem();
			default:
				break;
		}
	}
	
	private synchronized Response localPurchaseItem(String customerID, int itemID, int numberOfItem) {
		if (!inventory.containsKey(itemID)) {
			return new Response(ReturnStatus.NO_SUCH_ITEM);
		} else if (inventory.get(itemID) < numberOfItem) {
			try {
				attemptTransfer(itemID, numberOfItem);
			} catch (InsufficientQuantity e) {
				return new Response(ReturnStatus.INSUFFICIENT_QUANTITY);
			}
		} else {
			inventory.put(itemID, Integer.valueOf(inventory.get(itemID).intValue() - numberOfItem));
		}
		
		try	{
			recordTransaction(customerID, itemID, numberOfItem);
		} catch (FileNotFoundException e) {
			System.err.println("Could not open file!");
		}
			
		System.out.println("Customer " + customerID + " purchased " + numberOfItem + " of item " + itemID + ".");
		printInventory();
		return new Response(ReturnStatus.SUCCESS);
	}
	
	public Response localReturnItem(String customerID, int itemID, int numberOfItem) {
		if (numberOfItem > getTotalPurchased(customerID)) {
			return new Response(ReturnStatus.INVALID_RETURN);
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
		return new Response(ReturnStatus.SUCCESS);
	}
	
	public synchronized Response localTransferItem(int itemID, int numberOfItem) {
		System.out.println("Received transfer attempt for "+ numberOfItem + " of item " + itemID + ".");
		
		if (!inventory.containsKey(itemID) || inventory.get(itemID) < numberOfItem) { return new Response(ReturnStatus.FALSE); }
		
		inventory.put(itemID, Integer.valueOf(inventory.get(itemID).intValue() - numberOfItem));
		System.out.println("Transfered " + numberOfItem + " of item " + itemID + ".");
		printInventory();
		return new Response(ReturnStatus.TRUE);
	}
	
//	public String localCheckStock(int itemID) {
//		Hashtable<String, String> stock = new Hashtable<String, String>();
//		String[] received;
//		for (String storeCode : proximityList) {
//			try {
//				synchronized (this) {
//					// get a datagram socket
//			        DatagramSocket socket = new DatagramSocket(clientPort);
//			 
//			        // send request
//			        byte[] buf = new byte[256];
//			        buf = (itemID + "").getBytes();
//			        InetAddress address = InetAddress.getByName("localhost");
//			        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, portMap.get(storeCode));
//			        socket.send(packet);
//			     
//			        // get response
//			        packet = new DatagramPacket(buf, buf.length);
//			        socket.receive(packet);
//			 
//			        // extract stock
//			        received = (new String(packet.getData(), 0, packet.getLength())).split(",");
//			        stock.put(received[0], received[1]);
//			     
//			        socket.close();
//				}
//			} catch (Exception e) {
//				System.err.println("Tried port " + clientPort);
//				System.err.println("Server exception: " + e.toString());
//			    e.printStackTrace();
//			}
//		}
//		// wrap stock hash in MyHashtable to satisfy the IDL 
//		return stock.toString();
//	}
	
	public Response localExchange(String customerID, int boughtItemID, int boughtNumber,
			int desiredItemID, int desiredNumber) {
		try{
			if (desiredNumber > getTotalPurchased(customerID)) {
				return new Response(ReturnStatus.INVALID_RETURN); 
			} else {
				purchaseItem(customerID, desiredItemID, desiredNumber);
			}
			returnItem(customerID, boughtItemID, boughtNumber);
			
			System.out.println("Customer " + customerID + " exchanged " + boughtNumber + " of item " + boughtItemID + " for " +
					desiredNumber + " of item " + desiredItemID + ".");
			printInventory();
		} catch (InvalidReturn e) {
			return new Response(ReturnStatus.INVALID_RETURN);
		} catch (NoSuchItem e) {
			return new Response(ReturnStatus.NO_SUCH_ITEM);
		} catch (InsufficientQuantity e) {
			return new Response(ReturnStatus.INSUFFICIENT_QUANTITY);
		}
		return new Response(ReturnStatus.SUCCESS);
	}
	
	public void dispatch(Object obj) throws NoSuchItem {		
		@SuppressWarnings("unchecked")
		StatusPacket<RetailStoreRemoteMethod> req = (StatusPacket<RetailStoreRemoteMethod>) obj;
		Response resp = null;
		
		switch ((RetailStoreRemoteMethod) req.getStatus()) {
			case PURCHASE_ITEM:
				PurchaseItem purchaseItemReq = (PurchaseItem) req;
				resp = localPurchaseItem(
					purchaseItemReq.getCustomerID(),
					purchaseItemReq.getItemID(),
					purchaseItemReq.getNumberOfItem()
				);
				break;
				
			case RETURN_ITEM:
				ReturnItem returnItemReq = (ReturnItem) req;
				resp = localReturnItem(
					returnItemReq.getCustomerID(),
					returnItemReq.getItemID(),
					returnItemReq.getNumberOfItem()
				);
				break;
				
			case TRANSFER_ITEM:
				TransferItem transferItemReq = (TransferItem) req;
				resp = localTransferItem(
					transferItemReq.getItemID(),
					transferItemReq.getNumberOfItem()
				);
				break;
				
//			case CHECK_STOCK:				
//				CheckStock checkStockReq = (CheckStock) req;				
//				localCheckStock(checkStockReq.getItemID());
//				break;
				
			case EXCHANGE:
				Exchange exchangeReq = (Exchange) req;
				resp = localExchange(
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
		LiteLogger.log("Setting response id to:", req.getId());
		resp.setId(req.getId());
		
		LiteLogger.log("Dispatching udp:", "host=", groupMap.get(getLeaderId()).getHost(), "resp=", resp);
		udpSender.send(groupMap.get(getLeaderId()).getHost(), Config.LEADER_LISTEN_PORT, resp);
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
		File file = new File(Config.DATA_FOLDER + customerID); // + getStoreCode() + "_" + customerID + ".txt");
		
		// if file doesnt exists, then create it
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		PrintWriter outputStream = new PrintWriter(new FileOutputStream(file, true));
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
	
	public boolean hasHighestRank() {
		return id == MAX_ID;
	}
	
	public int getLeaderId() {
		for (int i = 1; i <= groupMap.size(); i++) {
			if (groupMap.containsKey(i)) {
				if (groupMap.get(i).isLeader) { return i; }				
			}
		}
		
		return -1; //somethign went terrible wrong
	}
	
	public int getId() { return id; }
	public boolean getLeader() { return isLeader; }
	public  HashMap<Integer, GroupMember> getGroupMap() { return groupMap; }
	
	public ElectionState getElectionState() {
		return electionState;
	}

	public void setElectionState(ElectionState electionState) {
		LiteLogger.log("Setting election state to:", electionState);
		this.electionState = electionState;
	}
	
	public void broadcast(BasicPacket req) {
		//System.out.println("Attemping to broadcast " + ((StatusPacket) req).getStatus());
		LiteLogger.log("Attempting to broadcast...");
		for (GroupMember member : groupMap.values()) {
			if (!member.isLeader() && member.isAlive()) {
				LiteLogger.log(member.getHost(), Config.SLAVES_LISTEN_PORT, req.getId(), id);
				udp.FIFOSend(member.getHost(), Config.SLAVES_LISTEN_PORT, req, req.getId(), id);
				
			}
		}
	}
	
	public void broadcast(BasicPacket req, int port) {
		//System.out.println("Attemping to broadcast " + ((StatusPacket) req).getStatus());
		LiteLogger.log("Attempting to broadcast...");
		for (GroupMember member : groupMap.values()) {
			if (!member.isLeader() && member.isAlive()) {
				LiteLogger.log(member.getHost(), port, req.getId(), id);
				udpFIFOSender.FIFOSend(member.getHost(), port, req, req.getId(), id);
				
			}
		}
	}
	
	public void broadcastAll(BasicPacket req, int port) {
		//System.out.println("Attemping to broadcast " + ((StatusPacket) req).getStatus());
		LiteLogger.log("Attempting to broadcast...");
		for (GroupMember member : groupMap.values()) {
			if (member.isAlive()) {
				LiteLogger.log(member.getHost(), port, req.getId(), id);
				udpFIFOSender.FIFOSend(member.getHost(), port, req, req.getId(), id);
				
			}
		}
	}
	
	public void broadcastAllNonFifo(BasicPacket req, int port) {
		//System.out.println("Attemping to broadcast " + ((StatusPacket) req).getStatus());
		LiteLogger.log("Attempting to broadcast...");
		for (GroupMember member : groupMap.values()) {
			if (member.isAlive()) {
				LiteLogger.log(member.getHost(), port, req);
				udpSender.send(member.getHost(), port, req);
				
			}
		}
	}
		
//	
//	public void broadcastHigherId(BasicPacket req) {
//		for (int i = id + 1; i != groupMap.size(); i++) {
//			udp.FIFOSend(groupMap.get(i).getHost(), Config.ELECTION_IN_PORT, req, id);
//		}
//	}
//
	public void setLeaderId(int leaderId) {
		if (leaderId == id) {
			isLeader = true;
		}
		
		for (int i = 1; i <= groupMap.size(); i++) {
			groupMap.get(i).setIsLeader(false);					
		}	
		MAX_ID = leaderId; //yaya whatever
		
		groupMap.get(leaderId).setIsLeader(true);
	}
}
