package packet;

public abstract class MethodRequest<T extends Enum<T>> extends BasicRequest {
	private T remoteMethod;
		
	public MethodRequest(T remoteMethod) {
		this.remoteMethod = remoteMethod;
	}	
	
	public T getRemoteMethod() {
		return remoteMethod;
	}
}
