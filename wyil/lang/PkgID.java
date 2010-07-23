package wyil.lang;

import java.util.*;
import java.io.File;

public class PkgID implements Iterable<String> {
	private final ArrayList<String> components;
	
	public PkgID(String... cs) {
		components = new ArrayList<String>();
		for(String s : cs) {
			components.add(s);
		}
	}
	
	public static PkgID fromString(String pkg) {
		String[] split = pkg.split("\\.");
		ArrayList<String> pkgs = new ArrayList<String>();
		for(int i=0;i!=split.length-1;++i) {
			pkgs.add(split[i]);
		}
		return new PkgID(pkgs);				
	}
	
	public PkgID(Collection<String> cs) {
		components = new ArrayList<String>(cs);		
	}
	
	public String get(int index) {
		return components.get(index);
	}
	
	public int size() {
		return components.size();
	}
	
	public String last() {
		return components.get(components.size()-1);
	}
	
	public PkgID subpkg(int start, int end) {
		PkgID r = new PkgID();		
		for(int i=start;i!=end;++i) {
			r.components.add(components.get(i));
		}
		return r;
	}
	
	public PkgID append(String pkg) {
		PkgID r = new PkgID(components);
		r.components.add(pkg);
		return r;
	}
	
	public Iterator<String> iterator() {
		return components.iterator();
	}
	
	public String fileName() {		
		boolean firstTime=true;
		String r = "";
		for(String s : components) {
			if(!firstTime) {
				r += File.separatorChar;
			}
			firstTime=false;
			r += s;			
		}
		return r;	
	}
	
	public boolean equals(Object o) {
		if(o instanceof PkgID) {
			PkgID p = (PkgID) o;
			return p.components.equals(components);
		}
		return false;
	}
	
	public int hashCode() {
		return components.hashCode();
	}
	
	public String toString() {
		boolean firstTime=true;
		String r = "";
		for(String s : components) {
			if(!firstTime) {
				r += ".";
			}
			firstTime=false;
			r += s;			
		}
		return r;
	}
}
