package packet;


public abstract class StatusPacket<T extends Enum<T>> extends BasicPacket {
	private T status;
		
	public StatusPacket(T status) {
		this.status = status;
	}	
	
	public T getStatus() {
		return status;
	}
}
