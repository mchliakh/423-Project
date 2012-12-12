package udp;

public abstract class FIFOObjectUDPServlet<T> extends FIFOObjectUDP implements Runnable {
	private T owner;
	
	public FIFOObjectUDPServlet(int port, T owner) {
		super(port);
		addProcess(1);
		addProcess(2);
		addProcess(3);
		this.owner = owner;
	}
	
	public abstract void run();

	public T getOwner() {
		return owner;
	}
}
