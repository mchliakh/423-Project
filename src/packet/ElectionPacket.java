package packet;


public class ElectionPacket extends BasicPacket {
	private int id;
	private int senderId;
	
	public ElectionPacket() {
		super();		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getSenderId() {
		return senderId;
	}

	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}
}
