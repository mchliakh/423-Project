package packet;


public class AnswerPacket extends BasicPacket {
	private int electionId;
	
	public AnswerPacket() {
		super();		
	}

	public int getElectionId() {
		return electionId;
	}

	public void setElectionId(int electionId) {
		this.electionId = electionId;
	}
}
