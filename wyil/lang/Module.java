package wyil.lang;

import java.util.*;
import wyil.ModuleLoader;

public abstract class Module extends ModuleLoader.Skeleton {	
	private HashMap<String,List<Method>> methods;
	private HashMap<String,TypeDef> types;
	private HashMap<String,ConstDef> constants;
	
	public Module(ModuleID mid,
			Map<String, List<Method>> methods,
			Map<String, TypeDef> types,
			Map<String, ConstDef> constants) {		
		super(mid);
		this.methods = new HashMap<String, List<Method>>(methods);
		this.types = new HashMap<String, TypeDef>(types);
		this.constants = new HashMap<String, ConstDef>(constants);
	}
	
	public TypeDef type(String name) {
		return types.get(name);
	}
	
	public ConstDef constant(String name) {
		return constants.get(name);
	}
	
	public List<Method> method(String name) {
		List<Method> ls= methods.get(name);
		if(ls == null) {
			return new ArrayList<Method>();
		} else {
			return ls;
		}
	}
	
	public List<Method> method(String name, Type.Fun ft) {
		List<Method> ls = methods.get(name);
		ArrayList<Method> rs = new ArrayList<Method>();
		if(ls != null) {
			for(Method m : ls) {
				if(m.type().equals(ft)) {
					rs.add(m);
				}
			}
		}
		return rs;
	}
	
	public boolean hasName(String name) {		
		return types.get(name) != null || constants.get(name) != null
				|| method(name).size() > 0;
	}
	
	public static class TypeDef {
		private String name;
		private Type type;

		public TypeDef(String name, Type type) {			
			this.name = name;
			this.type = type;			
		}

		public String name() {
			return name;
		}

		public Type type() {
			return type;
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
		private Type receiver;		
		private Type.Fun type;
		private ArrayList<String> paramNames;
		
		public Method(Type receiver, String name, Type.Fun type,				
				Collection<String> paramNames) {
			this.receiver = receiver;
			this.name = name;
			this.type = type;
			this.paramNames = new ArrayList<String>(paramNames);		
		}
		
		public Type receiver() {
			return receiver;
		}
		
		public String name() {
			return name;
		}
		
		public Type.Fun type() {
			return type;
		}

		public boolean isFunction() {
			return receiver == null;
		}
				
		public List<String> parameterNames() {
			return paramNames;
		}
	}	
}
