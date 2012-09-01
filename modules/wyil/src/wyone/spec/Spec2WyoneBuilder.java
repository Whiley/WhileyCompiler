package wyone.spec;

import java.util.ArrayList;
import java.util.HashMap;

import wyone.core.*;
import wyone.util.Pair;

public class Spec2WyoneBuilder {
	
	public WyoneFile build(SpecFile file) {
		ArrayList<WyoneFile.Decl> declarations = new ArrayList<WyoneFile.Decl>();
		for(SpecFile.Decl d : file.declarations) {
			if(d instanceof SpecFile.TermDecl) {
				declarations.add(build((SpecFile.TermDecl)d));
			} else if(d instanceof SpecFile.ClassDecl) {
				declarations.add(build((SpecFile.ClassDecl)d));
			} else if(d instanceof SpecFile.RewriteDecl) {
				declarations.add(build((SpecFile.RewriteDecl)d));
			} else {
				// unknown?
				throw new IllegalArgumentException("Unknown SpecFile Declaration encountered");
			}
		}
		return new WyoneFile(file.filename,declarations);
	}
	
	private WyoneFile.TermDecl build(SpecFile.TermDecl d) {
		return new WyoneFile.TermDecl(d.type,d.attributes());
	}
	
	private WyoneFile.ClassDecl build(SpecFile.ClassDecl d) {
		return new WyoneFile.ClassDecl(d.name,d.children,d.attributes());
	}
	
	private WyoneFile.FunDecl build(SpecFile.RewriteDecl d) {
		Type.Ref param = typeOf(d.pattern);
		Environment environment = new Environment();
		ArrayList<Code> body = new ArrayList<Code>();
		
		// generate codes and type them.
		
		return new WyoneFile.FunDecl("rewrite", Type.T_FUN(Type.T_BOOL, param),
				environment.asList(), body, d.attributes());
	}
	
	/**
	 * Translate the declared type in a pattern construct into an actual type.
	 * Essentially, this corresponds to discarding all of the parameter(s) used
	 * for matching subcomponents of the type.
	 * 
	 * @param pattern
	 * @return
	 */
	private Type.Ref typeOf(Pattern pattern) {
		if (pattern instanceof Pattern.Leaf) {
			Pattern.Leaf pl = (Pattern.Leaf) pattern;
			return Type.T_REF(pl.type);
		} else if (pattern instanceof Pattern.Term) {
			Pattern.Term pt = (Pattern.Term) pattern;
			Type.Ref data = typeOf(pt.data);
			return Type.T_REF(Type.T_TERM(pt.name, data));
		} else if (pattern instanceof Pattern.Compound) {
			Pattern.Compound pc = (Pattern.Compound) pattern;
			// FIXME: following should be an ArrayList<Type.Ref>
			ArrayList<Type> types = new ArrayList<Type>();
			for (Pair<Pattern, String> ps : pc.elements) {
				types.add(typeOf(ps.first()));
			}
			return Type.T_REF(Type.T_COMPOUND(pc.kind, pc.unbounded, types));
		} else {
			throw new IllegalArgumentException("Unknown pattern encountered - "
					+ pattern);
		}
	}
	
	private class Environment {
		private final HashMap<String, Integer> var2idx = new HashMap<String, Integer>();
		private final ArrayList<Type> idx2type = new ArrayList<Type>();

		public int allocate(Type t) {
			int idx = idx2type.size();
			idx2type.add(t);
			return idx;
		}

		public int allocate(Type t, String v) {
			int r = allocate(t);
			var2idx.put(v, r);
			return r;
		}

		public Integer get(String v) {
			return var2idx.get(v);
		}

		public void put(int idx, String v) {
			var2idx.put(v, idx);
		}
		
		public ArrayList<Type> asList() {
			return idx2type;
		}
		
		public String toString() {
			return idx2type.toString() + "," + var2idx.toString();
		}
	}
}
