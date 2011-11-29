package wyil.lang;

/**
 * An import is a special form of regular expression.  
 * 
 * @author David J. Pearce
 * 
 */
public class Import {	
	public final PkgID pkg;		
	public final String module;	
	public final String name;
	
	public Import(PkgID pkg, String module) {
		this(pkg,module,null);
	}
	
	public Import(PkgID pkg, String module, String name) {
		this.pkg = pkg;
		this.module = module;		
		this.name = name;
	}	
		public boolean matchName(String name) {
		return this.name != null && (this.name.equals(name) || this.name.equals("*"));
	}
	
	public boolean matchModule(String module) {
		return this.module != null && (this.module.equals(module) || this.module.equals("*"));
	}
}
