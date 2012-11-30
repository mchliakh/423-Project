package packet;

public class Param<T extends Enum<T>> {
	private T type;
	private String value;
	
	public Param(T type, String value) {
		this.type = type;
		this.value = value;
	}
	
	public T getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}
}
