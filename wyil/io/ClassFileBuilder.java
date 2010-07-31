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
import wyil.jvm.rt.BigRational;
import wyil.*;
import wyil.lang.*;
import wyil.lang.Code;
import wyjvm.lang.*;
import wyjvm.util.DeadCodeElimination;
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

	public ClassFile build(Module module) {
		JvmType.Clazz type = new JvmType.Clazz(module.id().pkg().toString(),
				module.id().module().toString());
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);
		modifiers.add(Modifier.ACC_FINAL);
		ClassFile cf = new ClassFile(49, type, JAVA_LANG_OBJECT,
				new ArrayList<JvmType.Clazz>(), modifiers);
		
		boolean addMainLauncher = false;		
		
		for(Module.ConstDef d : module.constants()) {						
			WhileyDefine wd = new WhileyDefine(d.name(),d.constant());
			cf.attributes().add(wd);
		}
		for(Module.TypeDef td : module.types()) {			
			Type t = td.type();			
			WhileyDefine wd = new WhileyDefine(td.name(),t,null);
			cf.attributes().add(wd);
		}
		for(Module.Method fd : module.methods()) {				
			if(fd.name().equals("main")) { 
				addMainLauncher = true;
			}
			ClassFile.Method m = build(fd);				
			cf.methods().add(m);			
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
		
		wyjvm.attributes.Code code = new wyjvm.attributes.Code(codes,
				new ArrayList(), cm);
		cm.attributes().add(code);
		
		return cm;	
	}
	
	public ClassFile.Method build(Module.Method fd) {		
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		if(fd.isPublic()) {
			modifiers.add(Modifier.ACC_PUBLIC);
		}
		modifiers.add(Modifier.ACC_STATIC);		
		JvmType.Function ft = (JvmType.Function) convertType(fd.type());		
		String name = nameMangle(fd.name(),fd.type());			
		ClassFile.Method cm = new ClassFile.Method(name,ft,modifiers);		
		ArrayList<Bytecode> codes = translate(fd);
		wyjvm.attributes.Code code = new wyjvm.attributes.Code(codes,new ArrayList(),cm);
		cm.attributes().add(code);				
		
		return cm;
	}
	
	public ArrayList<Bytecode> translate(Module.Method fd) {
		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();		
		HashMap<String, Integer> slots = new HashMap<String, Integer>();		
		
		HashSet<String> uses = Block.usedVariables(fd.body(),
				new HashSet<String>());
		
		int slot = 0;
		if(fd.type().receiver != null) {
			slots.put("this",slot++);
			uses.remove("this");
		}
				
		List<Type> paramTypes = fd.type().params;
		List<String> paramNames = fd.parameterNames();
		for(int i=0;i!=paramTypes.size();++i) {
			String name = paramNames.get(i);			
			slots.put(name,slot++);			
			uses.remove(name);
		}
		
		for(String v : uses) {
			slots.put(v,slot++);						
		}
		
		for(Code c : fd.body()) {			
			translate(c,slots,bytecodes);			
		}		
		
		return bytecodes;
	}
	
	public void translate(Code c, HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes) {
		if(c instanceof Code.Assign) {
			translate((Code.Assign)c,slots,bytecodes);
		} else if(c instanceof Code.Return){
			translate((Code.Return)c,slots,bytecodes);
		} else if(c instanceof Code.BinOp) {
			translate((Code.BinOp)c,slots,bytecodes);
		} else if(c instanceof Code.UnOp) {
			translate((Code.UnOp)c,slots,bytecodes);
		} else if(c instanceof Code.Goto) {
			translate((Code.Goto)c,slots,bytecodes);
		} else if(c instanceof Code.IfGoto) {
			translate((Code.IfGoto)c,slots,bytecodes);
		} else if(c instanceof Code.Label){
			translate((Code.Label)c,slots,bytecodes);
		} else if(c instanceof Code.Debug){
			translate((Code.Debug)c,slots,bytecodes);
		}
	}
	
	public void translate(Code.Assign c, HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes) {
		// Translate right-hand side
		translate(c.rhs,slots,bytecodes);

		// Apply conversion if necessary
		convert(c.type,c.rhs.type(),slots,bytecodes);
		
		// Write assignment
		bytecodes.add(new Bytecode.Store(slots.get(c.lhs), convertType(c.type)));		
	}

	public void translate(Code.Return c, HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes) {
		if (c.rhs != null) {
			translate(c.rhs, slots, bytecodes);
			bytecodes.add(new Bytecode.Return(convertType(c.type)));
		} else {		
			bytecodes.add(new Bytecode.Return(null));
		}
	}

	public void translate(Code.BinOp c, HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes) {

		// translate 1st right-hand side
		translate(c.rhs1, slots, bytecodes);

		// Apply conversion (if necessary)
		convert(c.type, c.rhs1.type(), slots, bytecodes);

		// translate 2nd right-hand side
		translate(c.rhs2, slots, bytecodes);

		// Apply conversion (if necessary)
		convert(c.type, c.rhs2.type(), slots, bytecodes);

		JvmType type = convertType(c.type);
		JvmType.Function ftype = new JvmType.Function(type,type);
		
		switch(c.op) {
		case ADD:			
			bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "add", ftype,
					Bytecode.VIRTUAL));
			break;
		case SUB:			
			bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "subtract", ftype,
					Bytecode.VIRTUAL));
			break;
		case MUL:			
			bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "multiply", ftype,
					Bytecode.VIRTUAL));
			break;
		case DIV:			
			bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "divide", ftype,
					Bytecode.VIRTUAL));
			break;
		}
		
		bytecodes.add(new Bytecode.Store(slots.get(c.lhs),type));
	}

	public void translate(Code.UnOp c, HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes) {

		// translate right-hand side
		translate(c.rhs, slots, bytecodes);

		// Apply conversion (if necessary)
		convert(c.type, c.rhs.type(), slots, bytecodes);

		JvmType type = convertType(c.type);

		switch (c.op) {
		case NEG: {
			JvmType.Function ftype = new JvmType.Function(type);
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type, "negate",
					ftype, Bytecode.VIRTUAL));
			break;
		}
		case NOT: {
			String exitLabel = freshLabel();
			String trueLabel = freshLabel();
			bytecodes.add(new Bytecode.If(Bytecode.If.EQ, trueLabel));
			bytecodes.add(new Bytecode.LoadConst(0));
			bytecodes.add(new Bytecode.Goto(exitLabel));
			bytecodes.add(new Bytecode.Label(trueLabel));
			bytecodes.add(new Bytecode.LoadConst(1));
			bytecodes.add(new Bytecode.Label(exitLabel));
			break;
		}
		case LENGTHOF: {
			JvmType.Function ftype = new JvmType.Function(T_INT);
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type, "size",
					ftype, Bytecode.VIRTUAL));
			bytecodes.add(new Bytecode.Conversion(T_INT, T_LONG));
			ftype = new JvmType.Function(BIG_INTEGER, T_LONG);
			bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "valueOf",
					ftype, Bytecode.STATIC));
			break;
		}
		}
		
		bytecodes.add(new Bytecode.Store(slots.get(c.lhs),type));
	}	
	public void translate(Code.IfGoto c, HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes) {
		translate(c.lhs,slots,bytecodes);
		translate(c.rhs,slots,bytecodes);
		JvmType type = convertType(c.type);
		int op;
		switch(c.op) {
		case EQ:
		{
			JvmType.Function ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz)type, "equals", ftype,
					Bytecode.VIRTUAL));			
			op = Bytecode.If.EQ;
			break;
		}
		case NEQ:
		{
			JvmType.Function ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz)type, "equals", ftype,
					Bytecode.VIRTUAL));
			op = Bytecode.If.NE;
			break;
		}
		case LT:
		{			
			JvmType.Function ftype = new JvmType.Function(T_INT,type);
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type, "compareTo", ftype,
					Bytecode.VIRTUAL));
			op = Bytecode.If.LT;			
			break;
		}
		case LTEQ:
		{			
			JvmType.Function ftype = new JvmType.Function(T_INT,type);
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type,
					"compareTo", ftype, Bytecode.VIRTUAL));
			op = Bytecode.If.LE;
			break;
		}
		case GT:
		{
			System.out.println("GOING GT");
			JvmType.Function ftype = new JvmType.Function(T_INT, type);
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type,
					"compareTo", ftype, Bytecode.VIRTUAL));
			op = Bytecode.If.GT;
			break;
		}
		case GTEQ:
		{
			System.out.println("GOING GTEQ");
			JvmType.Function ftype = new JvmType.Function(T_INT,type);
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type,
					"compareTo", ftype, Bytecode.VIRTUAL));			
			op = Bytecode.If.GE;
			break;
		}
		case SUBSETEQ:
		case SUBSET:
		case ELEMOF:
			default:
				throw new RuntimeException("unknown if condition encountered");
		}		
		bytecodes.add(new Bytecode.If(op, c.target));
	}
	public void translate(Code.Goto c, HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Goto(c.target));
	}
	public void translate(Code.Label c, HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Label(c.label));
	}
	public void translate(Code.Debug c, HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes) {
		translate(c.rhs,slots,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_VOID,WHILEYLIST);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "println", ftype,
				Bytecode.STATIC));
	}
	
	public void translate(RVal r, HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes) {
		if (r instanceof Value) {
			translate((Value) r, slots, bytecodes);
		} else {
			RVal.Variable v = (RVal.Variable) r;
			bytecodes.add(new Bytecode.Load(slots.get(v.name),
					convertType(v.type)));
		}
	}
	
	public void translate(Value v, HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes) {
		if(v instanceof Value.Bool) {
			translate((Value.Bool)v,slots,bytecodes);
		} else if(v instanceof Value.Int) {
			translate((Value.Int)v,slots,bytecodes);
		} else if(v instanceof Value.Real) {
			translate((Value.Real)v,slots,bytecodes);
		} else if(v instanceof Value.Set) {
			translate((Value.Set)v,slots,bytecodes);
		} else if(v instanceof Value.List) {
			translate((Value.List)v,slots,bytecodes);
		} else if(v instanceof Value.Tuple) {
			translate((Value.Tuple)v,slots,bytecodes);
		} else {
			throw new IllegalArgumentException("unknown value encountered:" + v);
		}
	}
	
	protected void translate(Value.Bool e, HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes) {
		if (e.value) {
			bytecodes.add(new Bytecode.LoadConst(1));
		} else {
			bytecodes.add(new Bytecode.LoadConst(0));
		}
	}
	protected void translate(Value.Int e, HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes) {
		BigInteger v = e.value;
		if(v.bitLength() < 64) {			
			bytecodes.add(new Bytecode.LoadConst(v.longValue()));
			JvmType.Function ftype = new JvmType.Function(BIG_INTEGER,T_LONG);
			bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "valueOf", ftype,
					Bytecode.STATIC));
		} else {
			// in this context, we need to use a byte array to construct the
			// integer object.
			byte[] bytes = v.toByteArray();
			JvmType.Array bat = new JvmType.Array(JvmTypes.T_BYTE);
			
			bytecodes.add(new Bytecode.New(BIG_INTEGER));		
			bytecodes.add(new Bytecode.Dup(BIG_INTEGER));			
			bytecodes.add(new Bytecode.LoadConst(bytes.length));
			bytecodes.add(new Bytecode.New(bat));
			for(int i=0;i!=bytes.length;++i) {
				bytecodes.add(new Bytecode.Dup(bat));
				bytecodes.add(new Bytecode.LoadConst(i));
				bytecodes.add(new Bytecode.LoadConst(bytes[i]));
				bytecodes.add(new Bytecode.ArrayStore(bat));
			}			
			
			JvmType.Function ftype = new JvmType.Function(T_VOID,bat);						
			bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "<init>", ftype,
					Bytecode.SPECIAL));
		}		
	}

	protected void translate(Value.Real e, HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes) {
		BigRational rat = e.value;
		BigInteger den = rat.denominator();
		BigInteger num = rat.numerator();
		if(den.equals(BigRational.ONE)) {
			if(num.bitLength() < 32) {			
				bytecodes.add(new Bytecode.LoadConst(num.intValue()));				
				JvmType.Function ftype = new JvmType.Function(BIG_RATIONAL,T_INT);
				bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "valueOf", ftype,
						Bytecode.STATIC));
			} else if(num.bitLength() < 64) {			
				bytecodes.add(new Bytecode.LoadConst(num.longValue()));				
				JvmType.Function ftype = new JvmType.Function(BIG_RATIONAL,T_LONG);
				bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "valueOf", ftype,
						Bytecode.STATIC));
			} else {
				// in this context, we need to use a byte array to construct the
				// integer object.
				byte[] bytes = num.toByteArray();
				JvmType.Array bat = new JvmType.Array(JvmTypes.T_BYTE);
				
				bytecodes.add(new Bytecode.New(BIG_RATIONAL));		
				bytecodes.add(new Bytecode.Dup(BIG_RATIONAL));			
				bytecodes.add(new Bytecode.LoadConst(bytes.length));
				bytecodes.add(new Bytecode.New(bat));
				for(int i=0;i!=bytes.length;++i) {
					bytecodes.add(new Bytecode.Dup(bat));
					bytecodes.add(new Bytecode.LoadConst(i));
					bytecodes.add(new Bytecode.LoadConst(bytes[i]));
					bytecodes.add(new Bytecode.ArrayStore(bat));
				}			
				
				JvmType.Function ftype = new JvmType.Function(T_VOID,bat);						
				bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "<init>", ftype,
						Bytecode.SPECIAL));								
			}	
		} else if(num.bitLength() < 32 && den.bitLength() < 32) {			
			bytecodes.add(new Bytecode.LoadConst(num.intValue()));
			bytecodes.add(new Bytecode.LoadConst(den.intValue()));
			JvmType.Function ftype = new JvmType.Function(BIG_RATIONAL,T_INT,T_INT);
			bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "valueOf", ftype,
					Bytecode.STATIC));
		} else if(num.bitLength() < 64 && den.bitLength() < 64) {			
			bytecodes.add(new Bytecode.LoadConst(num.longValue()));
			bytecodes.add(new Bytecode.LoadConst(den.longValue()));
			JvmType.Function ftype = new JvmType.Function(BIG_RATIONAL,T_LONG,T_LONG);
			bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "valueOf", ftype,
					Bytecode.STATIC));
		} else {
			// First, do numerator bytes
			byte[] bytes = num.toByteArray();
			JvmType.Array bat = new JvmType.Array(JvmTypes.T_BYTE);
			
			bytecodes.add(new Bytecode.New(BIG_RATIONAL));		
			bytecodes.add(new Bytecode.Dup(BIG_RATIONAL));			
			bytecodes.add(new Bytecode.LoadConst(bytes.length));
			bytecodes.add(new Bytecode.New(bat));
			for(int i=0;i!=bytes.length;++i) {
				bytecodes.add(new Bytecode.Dup(bat));
				bytecodes.add(new Bytecode.LoadConst(i));
				bytecodes.add(new Bytecode.LoadConst(bytes[i]));
				bytecodes.add(new Bytecode.ArrayStore(bat));
			}		
			
			// Second, do denominator bytes
			bytes = den.toByteArray();			
			bytecodes.add(new Bytecode.LoadConst(bytes.length));
			bytecodes.add(new Bytecode.New(bat));
			for(int i=0;i!=bytes.length;++i) {
				bytecodes.add(new Bytecode.Dup(bat));
				bytecodes.add(new Bytecode.LoadConst(i));
				bytecodes.add(new Bytecode.LoadConst(bytes[i]));
				bytecodes.add(new Bytecode.ArrayStore(bat));
			}
			
			// Finally, construct BigRational object
			JvmType.Function ftype = new JvmType.Function(T_VOID,bat,bat);						
			bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "<init>", ftype,
					Bytecode.SPECIAL));			
		}		
	}

	protected void translate(Value.Set lv, HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes) {
		construct(WHILEYSET, slots, bytecodes);		
		JvmType.Function ftype = new JvmType.Function(T_BOOL,
				JAVA_LANG_OBJECT);  
		for(Value e : lv.values) {
			// FIXME: there is a bug here for bool sets
			bytecodes.add(new Bytecode.Dup(WHILEYSET));
			translate(e, slots, bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYSET,"add",ftype,Bytecode.VIRTUAL));
			// FIXME: there is a bug here for bool sets
			bytecodes.add(new Bytecode.Pop(WHILEYSET));
		}
	}

	protected void translate(Value.List lv, HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes) {
		construct(WHILEYLIST, slots, bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_BOOL, JAVA_LANG_OBJECT);
		for (Value e : lv.values) {
			bytecodes.add(new Bytecode.Dup(WHILEYLIST));
			translate(e, slots, bytecodes);
			addWriteConversion(e.type(), bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "add", ftype,
					Bytecode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(WHILEYLIST));
		}
	}

	protected void translate(Value.Tuple expr, HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
				JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
		construct(WHILEYTUPLE, slots, bytecodes);
		for (Map.Entry<String, Value> e : expr.values.entrySet()) {
			Type et = e.getValue().type();
			bytecodes.add(new Bytecode.Dup(WHILEYTUPLE));
			bytecodes.add(new Bytecode.LoadConst(e.getKey()));
			translate(e.getValue(), slots, bytecodes);
			addWriteConversion(et, bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE, "put", ftype,
					Bytecode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(WHILEYTUPLE));
		}
	}

	/**
	 * The read conversion is necessary in situations where we're reading a
	 * value from a collection (e.g. WhileyList, WhileySet, etc) and then
	 * putting it on the stack. In such case, we need to convert boolean values
	 * from Boolean objects to bool primitives.
	 */
	public void addReadConversion(Type et, ArrayList<Bytecode> bytecodes) {
		if(et instanceof Type.Bool) {
			bytecodes.add(new Bytecode.CheckCast(JAVA_LANG_BOOLEAN));
			JvmType.Function ftype = new JvmType.Function(T_BOOL);
			bytecodes.add(new Bytecode.Invoke(JAVA_LANG_BOOLEAN,
					"booleanValue", ftype, Bytecode.VIRTUAL));
		} else {
			bytecodes.add(new Bytecode.CheckCast(convertType(et)));
		}
	}

	/**
	 * The write conversion is necessary in situations where we're write a value
	 * from the stack into a collection (e.g. WhileyList, WhileySet, etc). In
	 * such case, we need to convert boolean values from bool primitives to
	 * Boolean objects.
	 */
	public void addWriteConversion(Type et, ArrayList<Bytecode> bytecodes) {
		if(et instanceof Type.Bool) {
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_BOOLEAN,T_BOOL);
			bytecodes.add(new Bytecode.Invoke(JAVA_LANG_BOOLEAN,
					"valueOf", ftype, Bytecode.STATIC));
		} 
	}

	/**
	 * The construct method provides a generic way to construct a Java object.
	 * 
	 * @param owner
	 * @param slots
	 * @param bytecodes
	 * @param params
	 */
	public void construct(JvmType.Clazz owner, HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes, RVal... params) {
		bytecodes.add(new Bytecode.New(owner));		
		bytecodes.add(new Bytecode.Dup(owner));
		ArrayList<JvmType> paramTypes = new ArrayList<JvmType>();
		for(RVal e : params) {			
			translate(e,slots,bytecodes);
			paramTypes.add(convertType(e.type()));
		}
		JvmType.Function ftype = new JvmType.Function(T_VOID,paramTypes);
		
		// call the appropriate constructor
		bytecodes.add(new Bytecode.Invoke(owner, "<init>", ftype,
				Bytecode.SPECIAL));

	}		 
	
	protected void convert(Type toType, Type fromType,
			HashMap<String, Integer> slots, ArrayList<Bytecode> bytecodes) {		
		
		if(toType.equals(fromType)) {		
			// do nothing!			
		} else if (!(toType instanceof Type.Bool) && fromType instanceof Type.Bool) {
			// this is either going into a union type, or the any type
			convert(toType, (Type.Bool) fromType, slots, bytecodes);
		} else if(toType == Type.T_REAL && fromType == Type.T_INT) {			
			JvmType.Function ftype = new JvmType.Function(BIG_RATIONAL,BIG_INTEGER);
			bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "valueOf", ftype,
					Bytecode.STATIC));			
		} else if(toType instanceof Type.List && fromType instanceof Type.List) {
			convert((Type.List) toType, (Type.List) fromType, slots, bytecodes);			
		} else if(toType instanceof Type.Set && fromType instanceof Type.List) {
			convert((Type.Set) toType, (Type.List) fromType, slots, bytecodes);			
		} else if(toType instanceof Type.Set && fromType instanceof Type.Set) {
			convert((Type.Set) toType, (Type.Set) fromType, slots, bytecodes);			
		} else if(toType instanceof Type.Tuple && fromType instanceof Type.Tuple) {
			convert((Type.Tuple) toType, (Type.Tuple) fromType, slots, bytecodes);
		} else {
			// every other kind of conversion is either a syntax error (which
			// should have been caught by TypeChecker); or, a nop (since no
			// conversion is required on this particular platform).
		}
	}
	
	protected void convert(Type.List toType, Type.List fromType,
			HashMap<String, Integer> slots,			
			ArrayList<Bytecode> bytecodes) {
		
		if(fromType.element == Type.T_VOID) {
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
		construct(WHILEYLIST,slots,bytecodes);
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
		addReadConversion(fromType.element,bytecodes);
		convert(toType.element, fromType.element, slots,
				bytecodes);			
		ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "add",
				ftype, Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(T_BOOL));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(slots.get(tmp),WHILEYLIST));
	}
	
	protected void convert(Type.Set toType, Type.List fromType,
			HashMap<String, Integer> slots,			
			ArrayList<Bytecode> bytecodes) {
						
		if(fromType.element == Type.T_VOID) {
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
		construct(WHILEYSET,slots,bytecodes);
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
		addReadConversion(fromType.element,bytecodes);
		convert(toType.element, fromType.element, slots,
				bytecodes);			
		ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "add",
				ftype, Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(T_BOOL));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(slots.get(tmp),WHILEYSET));
	}
	
	protected void convert(Type.Set toType, Type.Set fromType,
			HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes) {
		
		if(fromType.element == Type.T_VOID) {
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
		construct(WHILEYSET,slots,bytecodes);
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
		bytecodes.add(new Bytecode.CheckCast(convertType(fromType.element)));		
		convert(toType.element, fromType.element, slots,
				bytecodes);			
		ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "add",
				ftype, Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(T_BOOL));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(slots.get(tmp),WHILEYSET));
	}
	
	public void convert(Type.Tuple toType, Type.Tuple fromType,
			HashMap<String, Integer> slots,
			ArrayList<Bytecode> bytecodes) {
		slots = (HashMap) slots.clone();
		String oldtup = freshVar(slots);
		String newtup = freshVar(slots);
		bytecodes.add(new Bytecode.Store(slots.get(oldtup),WHILEYTUPLE));
		construct(WHILEYTUPLE,slots,bytecodes);
		bytecodes.add(new Bytecode.Store(slots.get(newtup),WHILEYTUPLE));		
				
		for(String key : toType.types.keySet()) {
			Type to = toType.types.get(key);
			Type from = fromType.types.get(key);					
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
	
	public void convert(Type toType, Type.Bool fromType,
			HashMap<String, Integer> slots, ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_BOOLEAN,T_BOOL);			
		bytecodes.add(new Bytecode.Invoke(JAVA_LANG_BOOLEAN,"valueOf",ftype,Bytecode.STATIC));			
		// done deal!
	}
	
	
	public final static JvmType.Clazz WHILEYLIST = new JvmType.Clazz("wyil.jvm.rt","WhileyList");
	public final static JvmType.Clazz WHILEYSET = new JvmType.Clazz("wyil.jvm.rt","WhileySet");
	public final static JvmType.Clazz WHILEYTUPLE = new JvmType.Clazz("wyil.jvm.rt","WhileyTuple");	
	public final static JvmType.Clazz WHILEYPROCESS = new JvmType.Clazz(
			"wyil.jvm.rt", "WhileyProcess");
	public final static JvmType.Clazz BIG_INTEGER = new JvmType.Clazz("java.math","BigInteger");
	public final static JvmType.Clazz BIG_RATIONAL = new JvmType.Clazz("wyil.jvm.rt","BigRational");
	private static final JvmType.Clazz JAVA_LANG_SYSTEM = new JvmType.Clazz("java.lang","System");
	private static final JvmType.Clazz JAVA_IO_PRINTSTREAM = new JvmType.Clazz("java.io","PrintStream");
	private static final JvmType.Clazz JAVA_LANG_RUNTIMEEXCEPTION = new JvmType.Clazz("java.lang","RuntimeException");
	private static final JvmType.Clazz JAVA_LANG_ASSERTIONERROR = new JvmType.Clazz("java.lang","AssertionError");
	private static final JvmType.Clazz JAVA_UTIL_COLLECTION = new JvmType.Clazz("java.util","Collection");	
	
	public JvmType convertType(Type t) {
		if(t == Type.T_VOID) {
			return T_VOID;
		} else if(t == Type.T_ANY) {
			return JAVA_LANG_OBJECT;
		} else if(t instanceof Type.Bool) {
			return T_BOOL;
		} else if(t instanceof Type.Int) {
			return BIG_INTEGER;
		} else if(t instanceof Type.Real) {
			return BIG_RATIONAL;
		} else if(t instanceof Type.List) {
			return WHILEYLIST;
		} else if(t instanceof Type.Set) {
			return WHILEYSET;
		} else if(t instanceof Type.Tuple) {
			return WHILEYTUPLE;
		} else if(t instanceof Type.Process) {
			return WHILEYPROCESS;
		} else if(t instanceof Type.Named) {
			Type.Named nt = (Type.Named) t;
			return convertType(nt.type);
		} else if(t instanceof Type.Union) {
			Type.Union ut = (Type.Union) t;
			Type c = Type.leastUpperBound(ut.bounds);
			if(c != null) {
				// there was some commonality between types
				return convertType(c);
			} else {
				return JAVA_LANG_OBJECT;
			}
		} else if(t instanceof Type.Recursive) {
			Type.Recursive rt = (Type.Recursive) t;
			if(rt.type == null) {
				return JAVA_LANG_OBJECT;
			} else {
				return convertType(rt.type);
			}
		} else if(t instanceof Type.Fun) {
			Type.Fun ft = (Type.Fun) t; 
			ArrayList<JvmType> paramTypes = new ArrayList<JvmType>();
			if(ft.receiver != null) {
				paramTypes.add(convertType(ft.receiver));
			}
			for(Type pt : ft.params) {
				paramTypes.add(convertType(pt));
			}
			JvmType rt = convertType(ft.ret);
			return new JvmType.Function(rt,paramTypes);
		}else {
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
	
	protected String nameMangle(String name, Type.Fun type) {		
		return name + "$" + type2str(type);		
	}
	
	protected String type2str(Type t) {
		if(t == Type.T_EXISTENTIAL) {
			return "?";
		} else if(t == Type.T_ANY) {
			return "*";
		} else if(t == Type.T_VOID) {
			return "V";
		} else if(t instanceof Type.Bool) {
			return "B";
		} else if(t instanceof Type.Int) {
			return "I";
		} else if(t instanceof Type.Real) {
			return "R";
		} else if(t instanceof Type.List) {
			Type.List st = (Type.List) t;
			return "[" + type2str(st.element) + "]";
		} else if(t instanceof Type.Set) {
			Type.Set st = (Type.Set) t;
			return "{" + type2str(st.element) + "}";
		} else if(t instanceof Type.Union) {
			Type.Union st = (Type.Union) t;
			String r = "";
			boolean firstTime=true;
			for(Type b : st.bounds) {
				if(!firstTime) {
					r += "|";
				}
				firstTime=false;
				r += type2str(b);
			}			
			return r;
		} else if(t instanceof Type.Tuple) {
			Type.Tuple st = (Type.Tuple) t;
			ArrayList<String> keys = new ArrayList<String>(st.types.keySet());
			Collections.sort(keys);
			String r="(";
			for(String k : keys) {
				Type kt = st.types.get(k);
				r += k + ":" + type2str(kt);
			}			
			return r + ")";
		} else if(t instanceof Type.Process) {
			Type.Process st = (Type.Process) t;
			return "P" + type2str(st.element);
		} else if(t instanceof Type.Named) {
			Type.Named st = (Type.Named) t;
			return "N" + st.module + ";" + st.name + ";"
					+ type2str(st.type);
		} else if(t instanceof Type.Fun) {
			Type.Fun ft = (Type.Fun) t;
			String r = "";
			if(ft.receiver != null) {
				r += type2str(ft.receiver) + "$";
			}				
			r += type2str(ft.ret);
			for(Type pt : ft.params) {
				r += type2str(pt);
			}
			return r;
		} else if(t instanceof Type.Recursive) {
			Type.Recursive rt = (Type.Recursive) t;
			if(rt.type == null) {
				return rt.name;
			} else {
				return rt.name + ":" + type2str(rt.type);
			}
		} else {
			throw new RuntimeException("unknown type encountered: " + t);
		}
	}
}
