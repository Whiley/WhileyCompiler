package wyil.lang;

/**
 * An import is a special form of regular expression.  
 * 
 * @author djp
 * 
 */
public class Import {	
	private PkgID pkg;
		
	private String module;
	
	private String name;
	
	public Import(PkgID pkg, String module) {
		this(pkg,module,null);
	}
	
	public Import(PkgID pkg, String module, String name) {
		this.pkg = pkg;
		this.module = module;		
		this.name = name;
	}	
	
	public boolean match(NameID nid) {		
		if(match(nid.module())) {
			return name != null && (name.equals("*") || name.equals(nid.name()));			
		}
		return false;
	}
	
	public boolean match(ModuleID mid) {
		return match(mid.pkg()) && (module.equals(mid.module()) || module.equals("*"));		
	}
	
	public boolean match(PkgID pkg) {
		return this.pkg.equals(pkg);
	}	
}
