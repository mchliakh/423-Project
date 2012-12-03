package packet;

import java.io.Serializable;

public abstract class BasicPacket implements Serializable {

	private static final long serialVersionUID = 929654003982940349L;

	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
