package packet;

public abstract class MethodRequest<T extends Enum<T>> {
	private T remoteMethod;
		
	public MethodRequest(T remoteMethod) {
		this.remoteMethod = remoteMethod;
	}	
}
