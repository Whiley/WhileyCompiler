// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyil.io;

import java.math.BigInteger;
import java.util.*;

import wyil.jvm.attributes.*;
import wyil.*;
import wyil.lang.*;
import wyjvm.attributes.*;
import wyjvm.lang.*;
import wyjvm.lang.Modifier;
import wyjvm.util.DeadCodeElimination;
import static wyil.util.SyntaxError.*;
import static wyjvm.lang.JvmTypes.*;

/**
 * The purpose of the class file builder is to construct a jvm class file from a
 * given WhileyFile.
 * 
 * @author djp
 */
public class ClassFileBuilder {
	protected int CLASS_VERSION = 49;
	protected int WHILEY_MINOR_VERSION;
	protected int WHILEY_MAJOR_VERSION;
	protected ModuleLoader loader;	
	
	public ClassFileBuilder(ModuleLoader loader, int whileyMajorVersion, int whileyMinorVersion) {
		this.loader = loader;
		this.WHILEY_MINOR_VERSION = whileyMinorVersion;
		this.WHILEY_MAJOR_VERSION = whileyMajorVersion;
	}

	public ClassFile build(ResolvedWhileyFile wf) {
		JvmType.Clazz type = new JvmType.Clazz(wf.id().pkg().toString(),wf.id().module().toString());
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);
		modifiers.add(Modifier.ACC_FINAL);
		ClassFile cf = new ClassFile(49, type, JAVA_LANG_OBJECT,
				new ArrayList<JvmType.Clazz>(), modifiers);
		
		boolean addMainLauncher = false;
		HashMap<Triple<String,Type,Type.Fun>,List<FunDecl>> methodCases = new HashMap();
		
		for(Decl d : wf.declarations()) {
			if(d instanceof FunDecl) {
				FunDecl fd = (FunDecl) d;				
				if(fd.name().equals("main")) { 
					addMainLauncher = true;
				}
				Type.Fun ft = (Type.Fun) fd.type();
				Type pt = fd.receiver() == null ? null : fd.receiver().type();
				Triple<String,Type,Type.Fun> key = new Triple(fd.name(),pt,ft);
				List<FunDecl> cases = methodCases.get(key);
				if(cases == null) {
					cases = new ArrayList<FunDecl>();
					methodCases.put(key,cases);
				}
				cases.add(fd);				
			} else if(d instanceof TypeDecl) {
				TypeDecl td = (TypeDecl) d;
				Type t = td.type();
				WhileyDefine wd = new WhileyDefine(td.name(),t,null);
				cf.attributes().add(wd);
			} else if(d instanceof ConstDecl) {
				ConstDecl cd = (ConstDecl) d;				
				WhileyDefine wd = new WhileyDefine(cd.name(),null,cd.constant());
				cf.attributes().add(wd);
			}
		}

		for(Triple<String,Type,Type.Fun> c : methodCases.keySet()) {
			List<FunDecl> cases = methodCases.get(c);			
			if(cases.size() == 1) {
				FunDecl fd = cases.get(0);				
				ClassFile.Method m = build(0,fd,wf);				
				cf.methods().add(m);				
			} else {
				int idx = 0;
				for(FunDecl fd : cases) {					
					ClassFile.Method m = build(++idx,fd,wf);					
					cf.methods().add(m);					
				}
				cf.methods().add(
						buildCaseDispatch(c.first(), c.second(), c.third(),
								cases, wf.id()));
			}
		}
		
		if(addMainLauncher) {
			cf.methods().add(buildMainLauncher(type));
		}
		
		cf.attributes().add(
				new WhileyVersion(WHILEY_MAJOR_VERSION, WHILEY_MINOR_VERSION));
		
		// Finally, we need to eliminate any dead code that was introduced.
		new DeadCodeElimination().apply(cf);
		
		return cf;
	}	
		
	public ClassFile.Method buildMainLauncher(JvmType.Clazz owner) {
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);
		modifiers.add(Modifier.ACC_STATIC);		
		modifiers.add(Modifier.ACC_SYNTHETIC);		
		JvmType.Function ft1 = new JvmType.Function(T_VOID, new JvmType.Array(JAVA_LANG_STRING));		
		ClassFile.Method cm = new ClassFile.Method("main",ft1,modifiers);
		JvmType.Array strArr = new JvmType.Array(JAVA_LANG_STRING);
		ArrayList<Bytecode> codes = new ArrayList<Bytecode>();
		ft1 = new JvmType.Function(WHILEYPROCESS);
		codes.add(new Bytecode.Invoke(WHILEYPROCESS,"systemProcess",ft1,Bytecode.STATIC));
		codes.add(new Bytecode.Load(0,strArr));
		JvmType.Function ft2 = new JvmType.Function(WHILEYLIST,
				new JvmType.Array(JAVA_LANG_STRING));
		codes.add(new Bytecode.Invoke(WHILEYLIST,"fromStringList",ft2,Bytecode.STATIC));
		JvmType.Function ft3 = new JvmType.Function(T_VOID, WHILEYPROCESS, WHILEYLIST);
		
		codes
				.add(new Bytecode.Invoke(
						owner,
						"main$Nwhiley.lang.System;System;P?$V[[I]]",
						// "main$Nwhiley.lang.System;System;P?$V[Nwhiley.lang.String;string;[I]]",
						ft3, Bytecode.STATIC));
		codes.add(new Bytecode.Return(null));
		
		Code code = new Code(codes,new ArrayList(),cm);
		cm.attributes().add(code);
		
		return cm;	
	}
	
	public ClassFile.Method build(Module.Method fd) {		
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		if(fd.isPublic()) {
			modifiers.add(Modifier.ACC_PUBLIC);
		}
		modifiers.add(Modifier.ACC_STATIC);		
		Type.Fun flatType = fd.type();
		String name = nameMangle(name,pt,flatType);		
		ClassFile.Method cm = new ClassFile.Method(name,ft,modifiers);		
		ArrayList<Bytecode> codes = translate(fd);
		wyjvm.attributes.Code code = new wyjvm.attributes.Code(codes,new ArrayList(),cm);
		cm.attributes().add(code);		
		WhileyType wc = new WhileyType(flatType);
		cm.attributes().add(wc);
		
		return cm;
	}
	
	public ArrayList<Bytecode> translate(FunDecl fd) {
		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
		HashMap<String, Type> environment = new HashMap<String,Type>();
		HashMap<String, Integer> slots = new HashMap<String, Integer>();		
		
		int slot = 0;
		if(fd.receiver() != null) {
			slots.put("this",slot++);
			environment.put("this",fd.receiver().type());
		}
				
		for(FunDecl.Parameter p : fd.parameters()) {
			slots.put(p.name(),slot++);
			environment.put(p.name(), p.type());
		}
		
		for(Stmt s : fd.statements()) {			
			translate(s,slots,environment,fd,bytecodes);			
		}
		
		if(fd.returnType().type() == Types.T_VOID) {			
			bytecodes.add(new Bytecode.Return(null));			
		}
		
		return bytecodes;
	}
	
	
	public void addReadConversion(Type et, ArrayList<Bytecode> bytecodes) {
		if(et instanceof BoolType) {
			bytecodes.add(new Bytecode.CheckCast(JAVA_LANG_BOOLEAN));
			JvmType.Function ftype = new JvmType.Function(T_BOOL);
			bytecodes.add(new Bytecode.Invoke(JAVA_LANG_BOOLEAN,
					"booleanValue", ftype, Bytecode.VIRTUAL));
		} else {
			bytecodes.add(new Bytecode.CheckCast(convertType(et)));
		}
	}
	
	public void addWriteConversion(Type et, ArrayList<Bytecode> bytecodes) {
		if(et instanceof BoolType) {
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_BOOLEAN,T_BOOL);
			bytecodes.add(new Bytecode.Invoke(JAVA_LANG_BOOLEAN,
					"valueOf", ftype, Bytecode.STATIC));
		} 
	}
	
	public void construct(JvmType.Clazz owner,
			HashMap<String, Integer> slots, HashMap<String, Type> environment,
			ArrayList<Bytecode> bytecodes, RVal... params) {
		bytecodes.add(new Bytecode.New(owner));		
		bytecodes.add(new Bytecode.Dup(owner));
		ArrayList<JvmType> paramTypes = new ArrayList<JvmType>();
		for(RVal e : params) {			
			translate(e,slots,environment,bytecodes);
			paramTypes.add(convertType(e.type(environment)));
		}
		JvmType.Function ftype = new JvmType.Function(T_VOID,paramTypes);
		
		// call the appropriate constructor
		bytecodes.add(new Bytecode.Invoke(owner, "<init>", ftype,
				Bytecode.SPECIAL));

	}	

	protected void convert(Type toType, RVal from,
			HashMap<String, Integer> slots, HashMap<String, Type> environment,
			ArrayList<Bytecode> bytecodes) {				
		Type fromType = from.type(environment);			
		
		convert(toType, fromType, slots, bytecodes);
	}
	
	protected void convert(Type toType, Type fromType,
			HashMap<String, Integer> slots, ArrayList<Bytecode> bytecodes) {		
		
		if(toType.equals(fromType)) {		
			// do nothing!			
		} else if (!(toType instanceof BoolType) && fromType instanceof BoolType) {
			// this is either going into a union type, or the any type
			convert(toType, (BoolType) fromType, slots, bytecodes);
		} else if(toType == Types.T_REAL(null) && fromType instanceof IntType) {			
			JvmType.Function ftype = new JvmType.Function(BIG_RATIONAL,BIG_INTEGER);
			bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "valueOf", ftype,
					Bytecode.STATIC));			
		} else if(toType instanceof ListType && fromType instanceof ListType) {
			convert((ListType) toType, (ListType) fromType, slots, bytecodes);			
		} else if(toType instanceof SetType && fromType instanceof ListType) {
			convert((SetType) toType, (ListType) fromType, slots, bytecodes);			
		} else if(toType instanceof SetType && fromType instanceof SetType) {
			convert((SetType) toType, (SetType) fromType, slots, bytecodes);			
		} else if(toType instanceof TupleType && fromType instanceof TupleType) {
			convert((TupleType) toType, (TupleType) fromType, slots, bytecodes);
		} else {
			// every other kind of conversion is either a syntax error (which
			// should have been caught by TypeChecker); or, a nop (since no
			// conversion is required on this particular platform).
		}
	}
	
	protected void convert(ListType toListType, ListType fromListType,
			HashMap<String, Integer> slots,			
			ArrayList<Bytecode> bytecodes) {
		
		if(fromListType.element() == Types.T_VOID) {
			// nothing to do, in this particular case
			return;
		}
		
		// The following piece of code implements a java for-each loop which
		// iterates every element of the input collection, and recursively
		// converts it before loading it back onto a new WhileyList. 
		String loopLabel = freshLabel();
		String exitLabel = freshLabel();
		String iter = freshVar(slots);
		String tmp = freshVar(slots);
		JvmType.Function ftype = new JvmType.Function(JAVA_UTIL_ITERATOR);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_COLLECTION, "iterator",
				ftype, Bytecode.INTERFACE));
		bytecodes.add(new Bytecode.Store(slots.get(iter),
				JAVA_UTIL_ITERATOR));
		construct(WHILEYLIST,slots,null,bytecodes);
		bytecodes.add(new Bytecode.Store(slots.get(tmp), WHILEYLIST));
		bytecodes.add(new Bytecode.Label(loopLabel));
		ftype = new JvmType.Function(T_BOOL);
		bytecodes.add(new Bytecode.Load(slots.get(iter),JAVA_UTIL_ITERATOR));
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "hasNext",
				ftype, Bytecode.INTERFACE));
		bytecodes.add(new Bytecode.If(Bytecode.If.EQ, exitLabel));
		bytecodes.add(new Bytecode.Load(slots.get(tmp),WHILEYLIST));
		bytecodes.add(new Bytecode.Load(slots.get(iter),JAVA_UTIL_ITERATOR));
		ftype = new JvmType.Function(JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "next",
				ftype, Bytecode.INTERFACE));						
		addReadConversion(fromListType.element(),bytecodes);
		convert(toListType.element(), fromListType.element(), slots,
				bytecodes);			
		ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "add",
				ftype, Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(T_BOOL));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(slots.get(tmp),WHILEYLIST));
	}
	
	protected void convert(SetType toSetType, ListType fromListType,
			HashMap<String, Integer> slots,			
			ArrayList<Bytecode> bytecodes) {
						
		if(fromListType.element() == Types.T_VOID) {
			// nothing to do, in this particular case
			return;
		}
		
		// The following piece of code implements a java for-each loop which
		// iterates every element of the input collection, and recursively
		// converts it before loading it back onto a new WhileyList. 
		String loopLabel = freshLabel();
		String exitLabel = freshLabel();
		String iter = freshVar(slots);
		String tmp = freshVar(slots);
		JvmType.Function ftype = new JvmType.Function(JAVA_UTIL_ITERATOR);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_COLLECTION, "iterator",
				ftype, Bytecode.INTERFACE));
		bytecodes.add(new Bytecode.Store(slots.get(iter),
				JAVA_UTIL_ITERATOR));
		construct(WHILEYSET,slots,null,bytecodes);
		bytecodes.add(new Bytecode.Store(slots.get(tmp), WHILEYSET));
		bytecodes.add(new Bytecode.Label(loopLabel));
		ftype = new JvmType.Function(T_BOOL);
		bytecodes.add(new Bytecode.Load(slots.get(iter),JAVA_UTIL_ITERATOR));
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "hasNext",
				ftype, Bytecode.INTERFACE));
		bytecodes.add(new Bytecode.If(Bytecode.If.EQ, exitLabel));
		bytecodes.add(new Bytecode.Load(slots.get(tmp),WHILEYSET));
		bytecodes.add(new Bytecode.Load(slots.get(iter),JAVA_UTIL_ITERATOR));
		ftype = new JvmType.Function(JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "next",
				ftype, Bytecode.INTERFACE));						
		addReadConversion(fromListType.element(),bytecodes);
		convert(toSetType.element(), fromListType.element(), slots,
				bytecodes);			
		ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "add",
				ftype, Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(T_BOOL));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(slots.get(tmp),WHILEYSET));
	}
	
	protected void convert(SetType toType, SetType fromType,
			HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes) {
		
		if(fromType.element() == Types.T_VOID) {
			// nothing to do, in this particular case
			return;
		}
		
		// The following piece of code implements a java for-each loop which
		// iterates every element of the input collection, and recursively
		// converts it before loading it back onto a new WhileyList. 
		String loopLabel = freshLabel();
		String exitLabel = freshLabel();
		String iter = freshVar(slots);
		String tmp = freshVar(slots);
		JvmType.Function ftype = new JvmType.Function(JAVA_UTIL_ITERATOR);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_COLLECTION, "iterator",
				ftype, Bytecode.INTERFACE));
		bytecodes.add(new Bytecode.Store(slots.get(iter),
				JAVA_UTIL_ITERATOR));
		construct(WHILEYSET,slots,null,bytecodes);
		bytecodes.add(new Bytecode.Store(slots.get(tmp), WHILEYSET));
		bytecodes.add(new Bytecode.Label(loopLabel));
		ftype = new JvmType.Function(T_BOOL);
		bytecodes.add(new Bytecode.Load(slots.get(iter),JAVA_UTIL_ITERATOR));
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "hasNext",
				ftype, Bytecode.INTERFACE));
		bytecodes.add(new Bytecode.If(Bytecode.If.EQ, exitLabel));
		bytecodes.add(new Bytecode.Load(slots.get(tmp),WHILEYSET));
		bytecodes.add(new Bytecode.Load(slots.get(iter),JAVA_UTIL_ITERATOR));
		ftype = new JvmType.Function(JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "next",
				ftype, Bytecode.INTERFACE));
		bytecodes.add(new Bytecode.CheckCast(convertType(fromType.element())));		
		convert(toType.element(), fromType.element(), slots,
				bytecodes);			
		ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "add",
				ftype, Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(T_BOOL));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(slots.get(tmp),WHILEYSET));
	}
	
	public void convert(TupleType toType, TupleType fromType,
			HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes) {
		slots = (HashMap) slots.clone();
		String oldtup = freshVar(slots);
		String newtup = freshVar(slots);
		bytecodes.add(new Bytecode.Store(slots.get(oldtup),WHILEYTUPLE));
		construct(WHILEYTUPLE,slots,null,bytecodes);
		bytecodes.add(new Bytecode.Store(slots.get(newtup),WHILEYTUPLE));		
				
		for(String key : toType.types().keySet()) {
			Type to = toType.types().get(key);
			Type from = fromType.types().get(key);					
			bytecodes.add(new Bytecode.Load(slots.get(newtup),WHILEYTUPLE));
			bytecodes.add(new Bytecode.LoadConst(key));
			bytecodes.add(new Bytecode.Load(slots.get(oldtup),WHILEYTUPLE));
			bytecodes.add(new Bytecode.LoadConst(key));
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);			
			bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE,"get",ftype,Bytecode.VIRTUAL));					
			bytecodes.add(new Bytecode.CheckCast(convertType(from)));
			if(!to.equals(from)) {
				// now perform recursive conversion
				convert(to,from,slots,bytecodes);
			}			
			ftype = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);			
			bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE,"put",ftype,Bytecode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		}
		bytecodes.add(new Bytecode.Load(slots.get(newtup),WHILEYTUPLE));		
	}
	
	public void convert(Type toType, BoolType fromType,
			HashMap<String, Integer> slots, ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_BOOLEAN,T_BOOL);			
		bytecodes.add(new Bytecode.Invoke(JAVA_LANG_BOOLEAN,"valueOf",ftype,Bytecode.STATIC));			
		// done deal!
	}
	
	
	public final static JvmType.Clazz WHILEYLIST = new JvmType.Clazz("wyjc.jvm.rt","WhileyList");
	public final static JvmType.Clazz WHILEYSET = new JvmType.Clazz("wyjc.jvm.rt","WhileySet");
	public final static JvmType.Clazz WHILEYTUPLE = new JvmType.Clazz("wyjc.jvm.rt","WhileyTuple");	
	public final static JvmType.Clazz WHILEYPROCESS = new JvmType.Clazz(
			"wyjc.jvm.rt", "WhileyProcess");
	public final static JvmType.Clazz BIG_INTEGER = new JvmType.Clazz("java.math","BigInteger");
	public final static JvmType.Clazz BIG_RATIONAL = new JvmType.Clazz("wyjc.jvm.rt","BigRational");
	private static final JvmType.Clazz JAVA_LANG_SYSTEM = new JvmType.Clazz("java.lang","System");
	private static final JvmType.Clazz JAVA_IO_PRINTSTREAM = new JvmType.Clazz("java.io","PrintStream");
	private static final JvmType.Clazz JAVA_LANG_RUNTIMEEXCEPTION = new JvmType.Clazz("java.lang","RuntimeException");
	private static final JvmType.Clazz JAVA_LANG_ASSERTIONERROR = new JvmType.Clazz("java.lang","AssertionError");
	private static final JvmType.Clazz JAVA_UTIL_COLLECTION = new JvmType.Clazz("java.util","Collection");	
	
	public JvmType.Function convertType(Type receiver, Type.Fun ft) {
		ArrayList<JvmType> paramTypes = new ArrayList<JvmType>();
		if(receiver != null) {
			paramTypes.add(convertType(receiver));
		}
		for(Type t : ft.parameters()) {
			paramTypes.add(convertType(t));
		}
		JvmType rt = convertType(ft.returnType());
		return new JvmType.Function(rt,paramTypes);
	}
	
	public JvmType convertType(Type t) {
		if(t == Types.T_VOID) {
			return T_VOID;
		} else if(t == Types.T_ANY) {
			return JAVA_LANG_OBJECT;
		} else if(t instanceof BoolType) {
			return T_BOOL;
		} else if(t instanceof IntType) {
			return BIG_INTEGER;
		} else if(t instanceof RealType) {
			return BIG_RATIONAL;
		} else if(t instanceof ListType) {
			return WHILEYLIST;
		} else if(t instanceof SetType) {
			return WHILEYSET;
		} else if(t instanceof TupleType) {
			return WHILEYTUPLE;
		} else if(t instanceof ProcessType) {
			return WHILEYPROCESS;
		} else if(t instanceof NamedType) {
			NamedType nt = (NamedType) t;
			return convertType(nt.type());
		} else if(t instanceof UnionType) {
			UnionType ut = (UnionType) t;
			Type c = Types.commonType(ut.types());
			if(c != null) {
				// there was some commonality between types
				return convertType(c);
			} else {
				return JAVA_LANG_OBJECT;
			}
		} else if(t instanceof RecursiveType) {
			RecursiveType rt = (RecursiveType) t;
			if(rt.type() == null) {
				return JAVA_LANG_OBJECT;
			} else {
				return convertType(rt.type());
			}
		} else {
			throw new RuntimeException("unknown type encountered: " + t);
		}		
	}	
		
	protected int var = 0;
	protected String freshVar(HashMap<String, Integer> slots) {
		// Note, I use "$$" here to avoid name clases with temp variables
        // generated by the front-end.
		String v = "$$" + var++; 
		slots.put(v, slots.size());
		return v;
	}
	
	protected int label = 0;
	protected String freshLabel() {
		return "label" + label++;
	}
	
	protected String nameMangle(String name, Type receiver, Type.Fun type) {
		type = Types.stripConstraints(type);
		if (receiver != null) {
			receiver = Types.stripConstraints(receiver);
			return name + "$" + type2str(receiver) + "$" + type2str(type);
		} else {
			return name + "$" + type2str(type);
		}
	}
	
	protected String type2str(Type t) {
		if(t == Types.T_EXISTENTIAL) {
			return "?";
		} else if(t == Types.T_ANY) {
			return "*";
		} else if(t == Types.T_VOID) {
			return "V";
		} else if(t instanceof BoolType) {
			return "B";
		} else if(t instanceof IntType) {
			return "I";
		} else if(t instanceof RealType) {
			return "R";
		} else if(t instanceof ListType) {
			ListType st = (ListType) t;
			return "[" + type2str(st.element()) + "]";
		} else if(t instanceof SetType) {
			SetType st = (SetType) t;
			return "{" + type2str(st.element()) + "}";
		} else if(t instanceof UnionType) {
			UnionType st = (UnionType) t;
			String r = "";
			boolean firstTime=true;
			for(Type b : st.types()) {
				if(!firstTime) {
					r += "|";
				}
				firstTime=false;
				r += type2str(b);
			}			
			return r;
		} else if(t instanceof TupleType) {
			TupleType st = (TupleType) t;
			ArrayList<String> keys = new ArrayList<String>(st.types().keySet());
			Collections.sort(keys);
			String r="(";
			for(String k : keys) {
				Type kt = st.types().get(k);
				r += k + ":" + type2str(kt);
			}			
			return r + ")";
		} else if(t instanceof ProcessType) {
			ProcessType st = (ProcessType) t;
			return "P" + type2str(st.element());
		} else if(t instanceof NamedType) {
			NamedType st = (NamedType) t;
			return "N" + st.module() + ";" + st.name() + ";"
					+ type2str(st.type());
		} else if(t instanceof Type.Fun) {
			Type.Fun ft = (Type.Fun) t;
			String r = type2str(ft.returnType());
			for(Type pt : ft.parameters()) {
				r += type2str(pt);
			}
			return r;
		} else if(t instanceof RecursiveType) {
			RecursiveType rt = (RecursiveType) t;
			if(rt.type() == null) {
				return rt.name();
			} else {
				return rt.name() + ":" + type2str(rt.type());
			}
		} else {
			throw new RuntimeException("unknown type encountered: " + t);
		}
	}
	

	protected Type flattern(Type t) {
		if(t instanceof NamedType) {
			return ((NamedType)t).type();
		}
		return t;
	}
}
