package wyil.lang;

import java.io.File;
import java.util.*;

/**
 * A Module Identifier consists of a list of packages, and a
 * module name. The purpose of the UMI is to provide a uniform way of referring
 * to modules throughout the compiler.
 * 
 * @author djp
 * 
 */
public class ModuleID {
	private PkgID pkg;
	private String module;
	
	/**
     * Construct a module identifier.
     * 
     * @param pkg
     *            --- A list of packages. For example, "whiley.lang" becomes
     *            ["whiley","lang"]
     * @param module
     *            --- A module name.
     */
	public ModuleID(Collection<String> pkg, String module) {
		this.pkg = new PkgID(pkg);
		this.module = module;
	}
	
	public ModuleID(PkgID pkg, String module) {
		this.pkg = pkg;
		this.module = module;
	}
	
	public static ModuleID fromString(String pkg) {
		String[] split = pkg.split("\\.");
		ArrayList<String> pkgs = new ArrayList<String>();
		for (int i = 0; i != split.length - 1; ++i) {
			pkgs.add(split[i]);
		}
		return new ModuleID(new PkgID(pkgs), split[split.length - 1]);
	}
	
	public String module() {
		return module;
	}
	
	public PkgID pkg() {
		return pkg;
	}
	
	public String fileName() {
		if (pkg.size() == 0) {
			return module;
		} else {
			return pkg.fileName() + File.separatorChar + module;
		}
	}
	
	public String toString() {
		String r = pkg.toString();
		
		if(pkg.size() == 0) {
			return module;
		} else {
			return r + "." + module;
		}
	}
	
	public int hashCode() {
		return pkg.hashCode() ^ module.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o instanceof ModuleID) {
			ModuleID u = (ModuleID) o;			
			return u.module.equals(module) && u.pkg.equals(pkg);						
		}
		return false;
	}
}
