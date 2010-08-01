package wyil.lang;

import java.util.*;
import wyil.ModuleLoader;
import wyil.util.*;

public class Module extends ModuleLoader.Skeleton {	
	private final String filename;
	private HashMap<Pair<String,Type.Fun>,Method> methods;
	private HashMap<String,TypeDef> types;
	private HashMap<String,ConstDef> constants;
	
	public Module(ModuleID mid,
			String filename,
			List<Method> methods,
			List<TypeDef> types,
			List<ConstDef> constants) {		
		super(mid);
		this.filename = filename;
		
		// first, init the caches
		this.methods = new HashMap<Pair<String,Type.Fun>, Method>();
		this.types = new HashMap<String, TypeDef>();
		this.constants = new HashMap<String, ConstDef>();
		
		// second, build the caches
		for(Method m : methods) {
			Pair<String,Type.Fun> p = new Pair<String,Type.Fun>(m.name(),m.type());
			Method tmp = this.methods.get(p);
			if (tmp != null) {
				throw new IllegalArgumentException(
						"Multiple function or method definitions with the same name and type not permitted");
			}
			this.methods.put(p,m);
		}
		
		for (TypeDef t : types) {
			TypeDef tmp = this.types.get(t.name());
			if (tmp != null) {
				throw new IllegalArgumentException(
						"Multiple type definitions with the same name not permitted");
			}
			this.types.put(t.name(), t);
		}

		for (ConstDef c : constants) {
			ConstDef tmp = this.constants.get(c.name());
			if (tmp != null) {
				throw new IllegalArgumentException(
						"Multiple constant definitions with the same name not permitted");
			}
			this.constants.put(c.name(), c);
		}
	}
	
	public String filename() {
		return filename;
	}
	
	public TypeDef type(String name) {
		return types.get(name);
	}
	
	public Collection<Module.TypeDef> types() {
		return types.values();
	}
	
	public ConstDef constant(String name) {
		return constants.get(name);
	}
	
	public Collection<Module.ConstDef> constants() {
		return constants.values();
	}
	
	public List<Method> method(String name) {
		ArrayList<Method> r = new ArrayList<Method>();
		for(Pair<String,Type.Fun> p : methods.keySet()) {
			if(p.first().equals(name)) {
				r.add(methods.get(p));
			}
		}
		return r;
	}
	
	public Method method(String name, Type.Fun ft) {
		return methods.get(new Pair<String, Type.Fun>(name, ft));
	}
	
	public Collection<Module.Method> methods() {
		return methods.values();
	}
	
	public boolean hasName(String name) {		
		return types.get(name) != null || constants.get(name) != null
				|| method(name).size() > 0;
	}
	
	public static class TypeDef {
		private String name;
		private Type type;
		private Block constraint;

		public TypeDef(String name, Type type, Block constraint) {			
			this.name = name;
			this.type = type;			
			this.constraint = constraint;
		}

		public String name() {
			return name;
		}

		public Type type() {
			return type;
		}
		
		public Block constraint() {
			return constraint;
		}
	}
	
	public static class ConstDef {
		private String name;		
		private Value constant;
		
		public ConstDef(String name, Value constant) {
			this.name = name;
			this.constant = constant;
		}
		
		public String name() {
			return name;
		}
		
		public Value constant() {
			return constant;
		}
	}
	
	public static class Method {
		private String name;		
		private Type.Fun type;
		private ArrayList<String> paramNames;
		private Block body;
		
		public Method(String name, Type.Fun type,
				Collection<String> paramNames, Block body) {			
			this.name = name;
			this.type = type;
			this.paramNames = new ArrayList<String>(paramNames);
			this.body = body;
		}
		
		public String name() {
			return name;
		}
		
		public Type.Fun type() {
			return type;
		}

		public Block body() {
			return body;
		}
		
		public boolean isFunction() {
			return type.receiver == null;
		}
		
		public boolean isPublic() {
			return true;
		}
				
		public List<String> parameterNames() {
			return paramNames;
		}
	}	
}
