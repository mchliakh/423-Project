package udp;

public abstract class ObjectUDPServer<T> extends ObjectUDP implements Runnable {
	private T owner;
	
	public ObjectUDPServer(int port, T owner) {
		super(port);
		this.owner = owner;
	}
	
	public abstract void run();

	public T getOwner() {
		return owner;
	}
}
