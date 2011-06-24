package wyjc.runtime;

import java.math.BigInteger;
import java.util.*;

public class Type {
	public static final byte K_VOID = 0;
	public static final byte K_ANY = 1;
	public static final byte K_META = 2;
	public static final byte K_NULL = 3;
	public static final byte K_BOOL = 4;
	public static final byte K_INT = 5;
	public static final byte K_RATIONAL = 6;
	public static final byte K_STRING = 7;
	public static final byte K_TUPLE = 8;
	public static final byte K_SET = 9;
	public static final byte K_LIST = 10;
	public static final byte K_DICTIONARY = 11;	
	public static final byte K_PROCESS = 12;
	public static final byte K_RECORD = 13;
	public static final byte K_UNION = 14;
	public static final byte K_FUNCTION = 15;
	public static final byte K_EXISTENTIAL = 16;
	public static final byte K_LEAF = 17;
	
	public final int kind;
	
	private Type(int kind) {
		this.kind = kind;
	}
	
	public static final Void VOID = new Void();
	public static final Any ANY = new Any();
	public static final Null NULL = new Null();
	public static final Bool BOOL = new Bool();
	public static final Integer INT = new Integer();
	public static final Rational REAL = new Rational();
	public static final Strung STRING = new Strung();
	
	private static final class Void extends Type { Void() {super(K_VOID);}}
	private static final class Any extends Type { Any() {super(K_ANY);}}
	private static final class Null extends Type { Null() {super(K_NULL);}}
	private static final class Bool extends Type { Bool() {super(K_BOOL);}}
	private static final class Integer extends Type { Integer() {super(K_INT);}}
	private static final class Rational extends Type { Rational() {super(K_RATIONAL);}}
	private static final class Strung extends Type { Strung() {super(K_STRING);}}
	
	public static final class List extends Type {
		public Type element;
		
		public List(Type element) {
			super(K_LIST);
			this.element = element;
		}
	}
	
	public static final class Set extends Type {
		public Type element;
		
		public Set(Type element) {
			super(K_SET);
			this.element = element;
		}
	}
	
	public static final class Dictionary extends Type {
		public Type key;
		public Type value;
		
		public Dictionary(Type key, Type value) {
			super(K_DICTIONARY);
			this.key = key;
			this.value = value;
		}
	}
	
	public static final class Record extends Type {
		public final String[] names;
		public final Type[] types;
		public Record(String[] names, Type[] types) {
			super(K_RECORD);
			this.names = names;
			this.types = types;
		}
	}
	
	public static final class Union extends Type {
		public final Type[] bounds;		
		public Union(Type... bounds) {
			super(K_UNION);
			this.bounds = bounds;
		}
	}
	
	private static final class Leaf extends Type {
		public final String name;
		public Leaf(String name) {
			super(K_LEAF);
			this.name = name;
		}
	}
	
	public static Type valueOf(String str) {
		return new TypeParser(str).parse(new HashSet<String>());
	}
	
	private static Type substitute(Type type, String var, Type root) {
		switch(type.kind) {
			case Type.K_ANY:				
			case Type.K_VOID:				
			case Type.K_NULL:				
			case Type.K_INT:				
			case Type.K_RATIONAL:				
			case Type.K_STRING:
				break;
			case Type.K_LEAF:
			{
				Type.Leaf leaf = (Type.Leaf)type;
				if(leaf.name.equals(var)) {
					return root;
				} else {
					return leaf;
				}
			}
			case Type.K_LIST:
			{
				Type.List list = (Type.List) type;
				list.element = substitute(list.element,var,root); 
				break;
			}
			case Type.K_SET:
			{
				Type.Set set = (Type.Set) type;
				set.element = substitute(set.element,var,root); 
				break;
			}
			case Type.K_DICTIONARY:
			{
				Type.Dictionary dict = (Type.Dictionary) type;
				dict.key = substitute(dict.key,var,root); 
				dict.value = substitute(dict.value,var,root);
				break;
			}
			case Type.K_RECORD:
			{
				Type.Record rec = (Type.Record) type;
				Type[] types = rec.types;
				for(int i=0;i!=types.length;++i) {
					types[i] = substitute(types[i],var,root);
				}
				break;
			}
			case Type.K_UNION:
			{
				Type.Union un = (Type.Union) type;
				Type[] types = un.bounds;
				for(int i=0;i!=types.length;++i) {
					types[i] = substitute(types[i],var,root);
				}
				break;
			}
		}			
		return type;
	}
	
	private static final class TypeParser {
		private int index;
		private String str;
		public TypeParser(String str) { 
			this.str = str;
		}
		public Type parse(HashSet<String> typeVars) {
			Type term = parseTerm(typeVars);
			skipWhiteSpace();
			while(index < str.length() && str.charAt(index) == '|') {
				// union type
				match("|");
				term = new Union(term,parse(typeVars));
				skipWhiteSpace();
			}
			return term;
		}
		public Type parseTerm(HashSet<String> typeVars) {
			skipWhiteSpace();
			char lookahead = str.charAt(index);

			switch (lookahead) {
			case 'a':
				match("any");
				return ANY;
			case 'v':
				match("void");
				return VOID;
			case 'n':
				match("null");
				return NULL;
			case 'b':
				match("bool");
				return BOOL;
			case 'i':
				match("int");
				return INT;
			case 'r':
				match("real");
				return REAL;
			case 's':
				match("string");
				return STRING;
			case '[':
			{
				match("[");
				Type elem = parse(typeVars);
				match("]");
				return new List(elem);
			}
			case '{':
			{
				match("{");
				Type elem = parse(typeVars);
				skipWhiteSpace();
				if(index < str.length() && str.charAt(index) == '-') {
					// dictionary
					match("->");
					Type value = parse(typeVars);
					match("}");
					return new Dictionary(elem,value);
				} else if(index < str.length() && str.charAt(index) != '}') {
					// record
					HashMap<String,Type> fields = new HashMap<String,Type>();					
					String id = parseIdentifier();
					fields.put(id, elem);
					skipWhiteSpace();
					while(index < str.length() && str.charAt(index) == ',') {
						match(",");
						elem = parse(typeVars);
						id = parseIdentifier();
						fields.put(id, elem);
						skipWhiteSpace();
					}
					match("}");
					
					String[] names = new String[fields.size()];
					Type[] types = new Type[fields.size()];
					ArrayList<String> tmp = new ArrayList<String>(fields.keySet());
					Collections.sort(tmp);
					for(int i=0;i!=names.length;++i) {
						String name = tmp.get(i); 
						names[i] = name;
						types[i] = fields.get(name);
					}
										
					return new Record(names,types);					
				}
				match("}");
				return new Set(elem);
			}
			default:
			{
				// this case is either a syntax error, or it's a recursive type.
				String var = parseIdentifier();
				
				if(typeVars.contains(var)) {
					return new Leaf(var);
				} else {
					typeVars = new HashSet<String>(typeVars);
					typeVars.add(var);
					match("<");
					Type t = parse(typeVars);
					match(">");
					return substitute(t,var,t);
				}				
			}
			}
		}
		private String parseIdentifier() {
			skipWhiteSpace();
			int start = index;
			while (index < str.length()
					&& Character.isJavaIdentifierPart(str.charAt(index))) {
				index++;
			}
			return str.substring(start,index);
		}
		private void skipWhiteSpace() {
			while (index < str.length()
					&& Character.isWhitespace(str.charAt(index))) {
				index++;
			}
		}		
		private void match(String match) {
			skipWhiteSpace();
			if ((str.length() - index) < match.length()
					|| !str.startsWith(match, index)) {
				throw new IllegalArgumentException("invalid type string: "
						+ str);
			}
			index += match.length();
		}		
	}
	
}
