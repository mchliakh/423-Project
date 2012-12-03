package udp;

import java.util.ArrayList;
import java.util.Iterator;

public class FIFOObjectUDP extends ObjectUDP {
	
	private class Message {
		private int sequenceNumber;
		private int senderID;
		private Object obj;
		
		public Message(int sequenceNumber, int senderID, Object obj) {
			this.sequenceNumber = sequenceNumber;
			this.senderID = senderID;
			this.obj = obj;
		}
		
		public int getSequenceNumber() {
			return sequenceNumber;
		}
		
		public int getSenderID() {
			return senderID;
		}
		
		public Object getObject() {
			return obj;
		}
	}
	
	private int counter = 0;
	private ArrayList<ArrayList<Object>> queue 		 = new ArrayList<ArrayList<Object>>();
	private ArrayList<Iterator<Object>> iteratorList = new ArrayList<Iterator<Object>>();

	public FIFOObjectUDP(int port) {
		super(port);
		for (ArrayList<Object> process : queue) {
			iteratorList.add(process.iterator());
		}
	}
	
	public void FIFOSend(String host, int port, Object obj, int senderID) {
		Message message = new Message(counter++, senderID, obj);
		super.send(host, port, message);
	}
	
	public Object FIFOReceive() {
		Object obj = null;
		while (obj == null) {
			for (ArrayList<Object> process : queue) {
				// receive next message
				Message message = (Message) super.receive();
				queue.get(message.getSenderID()).add(message.getSequenceNumber(), message.getObject());
				
				// return object if in FIFO order
				if (iteratorList.get(queue.indexOf(process)).hasNext())
					obj = iteratorList.get(queue.indexOf(process)).next();
				
			}
		}
		return obj;
	}
}
