package wyil.lang;

/**
 * A Name Identifier consists of a module, and a name within that module. The
 * purpose of this is to provide a uniform way of referring to modules +
 * names throughout the compiler.
 * 
 * @author djp
 * 
 */
public class NameID {
	private ModuleID module;
	private String name;
		
	public NameID(ModuleID module, String name) {		
		this.module = module;
		this.name = name;
	}

	public String name() {
		return name;
	}
	
	public ModuleID module() {
		return module;
	}
	
	public String toString() {
		return module + ":" + name;
	}
	
	public int hashCode() {
		return name.hashCode() ^ module.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o instanceof NameID) {
			NameID u = (NameID) o;			
			return u.module.equals(module) && u.name.equals(name);						
		}
		return false;
	}
}
