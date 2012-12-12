package packet;

public class AliveRequest extends BasicPacket {

	private static final long serialVersionUID = 8889129756127565302L;
	
	private int id;
	
	public AliveRequest() {
		super();		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
