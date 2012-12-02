package packet;


public class CoordinatorPacket extends BasicPacket {
	private int leaderId;
	
	public CoordinatorPacket() {
		super();		
	}
	
	public int getLeaderId() {
		return leaderId;
	}

	public void setLeaderId(int leaderId) {
		this.leaderId = leaderId;
	}
}
