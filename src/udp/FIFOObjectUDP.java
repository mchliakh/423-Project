package udp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class FIFOObjectUDP extends ObjectUDP implements Serializable {
	
	private static final long serialVersionUID = -5234768430440092486L;

	private class Message implements Serializable {

		private static final long serialVersionUID = -6214114249577926570L;
		
		private int sequenceNumber;
		private int senderID;
		private Serializable obj;
		
		public Message(int sequenceNumber, int senderID, Serializable obj) {
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
		
		public Serializable getObject() {
			return obj;
		}
	}
	
	private int counter = 0;
	private Hashtable<Integer, Object[]> processes = new Hashtable<Integer, Object[]>();	

	public FIFOObjectUDP(int port) {
		super(port);		
	}
	
	public void addProcess(int id) {
		Object[] queue = new Object[1000];
		queue[0] = 0; //counter
		processes.put(id, queue);		
	}
	
	public void FIFOSend(String host, int port, Serializable obj, int senderID) {
		Message message = new Message(counter++, senderID, obj);
		super.send(host, port, message);
	}
	
	public Object FIFOReceive() {
		
		//empty non processed queues
		for (Object[] queue : processes.values()) {
			int counter    = (Integer)queue[0];
			Object message = queue[counter];
			if (message != null) {
				queue[counter] = (Object)(counter + 1);
				return message;
			}
		}
		
		//wait on receive 
		Object result = null;			
		while (result == null) {
			Message message = (Message) super.receive();
			
			if (processes.containsKey(message.getSenderID())) {
				System.out.println(message.sequenceNumber);
				Object[] queue = processes.get(message.getSenderID());
				queue[message.getSequenceNumber()] = message.getObject();
				
				int counter = (Integer)queue[0];
				result = queue[counter];
				
				if (result != null) { queue[0] = (Object)(counter + 1); }
			}
			else {
				System.out.println("Key missing, check key:" + message.getSenderID());
			}			
		}	
		return result;
	}
		
//	public Object FIFOReceive() {
//		Object obj = null;
//		while (obj == null) {
//			for (ArrayList<Object> process : queue) {
//				// receive next message
//				Message message = (Message) super.receive();
//				System.out.println(message.sequenceNumber);
//				queue.get(message.getSenderID()).add(message.getSequenceNumber(), message.getObject());
//				
//				// return object if in FIFO order
//				if (iteratorList.get(queue.indexOf(process)).hasNext())
//					obj = iteratorList.get(queue.indexOf(process)).next();
//				
//			}
//		}
//		return obj;
//	}
}
