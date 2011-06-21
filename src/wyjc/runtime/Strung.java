package wyjc.runtime;

public final class Strung implements Any {
	public final String string;
	
	public Strung(String value) {
		this.string = value;
	}
	
	public Strung inc() {		
		return this;
	}
	
	public void dec() { }
	
	public boolean instanceOf(Type t) {
		return false;
	}		
	
	public Any coerce(Type t) {
		return null;
	}
	
	public boolean equals(Object o) {
		if(o instanceof Strung) {
			return ((Strung)o).string.equals(string);
		}
		return false;
	}
	
	public int hashCode() {
		return string.hashCode();
	}
}
