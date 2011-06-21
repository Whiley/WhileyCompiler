// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyjc.io;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.zip.*;

import wyjc.attributes.WhileyDefine;
import wyjc.attributes.WhileyType;
import wyjc.attributes.WhileyVersion;
import wyil.*;
import static wyil.util.SyntaxError.*;
import wyil.util.*;
import wyil.lang.*;
import wyil.lang.Code.*;
import static wyil.lang.Block.*;
import wyjc.runtime.BigRational;
import wyjvm.io.BinaryInputStream;
import wyjvm.io.BinaryOutputStream;
import wyjvm.lang.*;
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
	protected String filename;
	protected JvmType.Clazz owner;
	
	public ClassFileBuilder(ModuleLoader loader, int whileyMajorVersion, int whileyMinorVersion) {
		this.loader = loader;
		this.WHILEY_MINOR_VERSION = whileyMinorVersion;
		this.WHILEY_MAJOR_VERSION = whileyMajorVersion;
	}

	public ClassFile build(Module module) {
		owner = new JvmType.Clazz(module.id().pkg().toString(),
				module.id().module().toString());
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);
		modifiers.add(Modifier.ACC_FINAL);
		ClassFile cf = new ClassFile(49, owner, JAVA_LANG_OBJECT,
				new ArrayList<JvmType.Clazz>(), modifiers);
	
		this.filename = module.filename();
		
		boolean addMainLauncher = false;		
				
		for(Module.ConstDef cd : module.constants()) {	
			// FIXME: this is an ugly hack for now
			ArrayList<BytecodeAttribute> attrs = new ArrayList<BytecodeAttribute>();
			for(Attribute a : cd.attributes()) {
				if(a instanceof BytecodeAttribute) {
					attrs.add((BytecodeAttribute)a);
				}
			}
			WhileyDefine wd = new WhileyDefine(cd.name(),cd.constant(),attrs);
			cf.attributes().add(wd);
		}
		
		for(Module.TypeDef td : module.types()) {
			// FIXME: this is an ugly hack for now
			ArrayList<BytecodeAttribute> attrs = new ArrayList<BytecodeAttribute>();
			for(Attribute a : td.attributes()) {
				if(a instanceof BytecodeAttribute) {
					attrs.add((BytecodeAttribute)a);
				}
			}
			Type t = td.type();			
			WhileyDefine wd = new WhileyDefine(td.name(),t,attrs);
			cf.attributes().add(wd);
		}
		
		HashMap<Value,Integer> constants = new HashMap<Value,Integer>();
		for(Module.Method method : module.methods()) {				
			if(method.name().equals("main")) { 
				addMainLauncher = true;
			}			
			cf.methods().addAll(build(method, constants));			
		}		
		
		if(constants.size() > 0) {
			buildConstants(constants,cf);
		}
				
		if(addMainLauncher) {
			cf.methods().add(buildMainLauncher(owner));
		}
		
		cf.attributes().add(
				new WhileyVersion(WHILEY_MAJOR_VERSION, WHILEY_MINOR_VERSION));
		
		return cf;
	}	
	
	public void buildConstants(HashMap<Value,Integer> constants, ClassFile cf) {		
		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
		
		for(Map.Entry<Value,Integer> entry : constants.entrySet()) {			
			Value constant = entry.getKey();
			int index = entry.getValue();
			
			// First, create the static final field that will hold this constant 
			String name = "constant$" + index;
			ArrayList<Modifier> fmods = new ArrayList<Modifier>();
			fmods.add(Modifier.ACC_PRIVATE);
			fmods.add(Modifier.ACC_STATIC);
			fmods.add(Modifier.ACC_FINAL);
			JvmType type = convertType(constant.type());
			ClassFile.Field field = new ClassFile.Field(name, type, fmods);
			cf.fields().add(field);
			
			// Now, create code to intialise this field
			translate(constant,0,bytecodes);
			bytecodes.add(new Bytecode.PutField(owner, name, type, Bytecode.STATIC));
		}
		
		bytecodes.add(new Bytecode.Return(null));
		
		// now, create static initialiser method
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);
		modifiers.add(Modifier.ACC_STATIC);
		modifiers.add(Modifier.ACC_SYNTHETIC);
		JvmType.Function ftype = new JvmType.Function(new JvmType.Void());
		ClassFile.Method clinit = new ClassFile.Method("<clinit>", ftype, modifiers);
		cf.methods().add(clinit);
		
		// finally add code for staticinitialiser method
		wyjvm.attributes.Code code = new wyjvm.attributes.Code(bytecodes,new ArrayList(),clinit);
		clinit.attributes().add(code);				
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
		codes.add(new Bytecode.Invoke(WHILEYUTIL,"fromStringList",ft2,Bytecode.STATIC));
		Type.Fun wyft = Type.T_FUN(WHILEY_SYSTEM_T,
				Type.T_VOID, Type.T_LIST(Type.T_LIST(Type.T_INT)));
		JvmType.Function ft3 = convertFunType(wyft);
		
		// The following is a little bit of hack. Basically we flush the stdout
		// channel on exit
		codes.add(new Bytecode.Invoke(owner, nameMangle("main",wyft), ft3, Bytecode.STATIC));
		ft3 = new JvmType.Function(T_VOID);		
		codes.add(new Bytecode.Invoke(WHILEYIO, "flush", ft3, Bytecode.STATIC));
		codes.add(new Bytecode.Return(null));
		
		wyjvm.attributes.Code code = new wyjvm.attributes.Code(codes,
				new ArrayList(), cm);
		cm.attributes().add(code);
		
		return cm;	
	}
	
	public List<ClassFile.Method> build(Module.Method method,
			HashMap<Value, Integer> constants) {
		ArrayList<ClassFile.Method> methods = new ArrayList<ClassFile.Method>();
		int num = 1;
		for(Module.Case c : method.cases()) {
			methods.add(build(num++,c,method,constants));
		}
		return methods;
	}
	
	public ClassFile.Method build(int caseNum, Module.Case mcase,
			Module.Method method, HashMap<Value,Integer> constants) {		
		
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		if(method.isPublic()) {
			modifiers.add(Modifier.ACC_PUBLIC);
		}
		modifiers.add(Modifier.ACC_STATIC);		
		JvmType.Function ft = convertFunType(method.type());		
		String name = nameMangle(method.name(),method.type());
		/* need to put this back somehow?
		if(method.cases().size() > 1) {
			name = name + "$" + caseNum;
		}
		*/
				
		ClassFile.Method cm = new ClassFile.Method(name,ft,modifiers);		
		for(Attribute a : mcase.attributes()) {
			if(a instanceof BytecodeAttribute) {
				// FIXME: this is a hack
				cm.attributes().add((BytecodeAttribute)a);
			}
		}
				
		ArrayList<Bytecode> codes = translate(mcase,constants);
		wyjvm.attributes.Code code = new wyjvm.attributes.Code(codes,new ArrayList(),cm);
		cm.attributes().add(code);		
		
		return cm;
	}
	
	public ArrayList<Bytecode> translate(Module.Case mcase, HashMap<Value,Integer> constants) {
		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();				
		translate(mcase.body(),mcase.locals().size(),constants,bytecodes);				
		return bytecodes;
	}

	/**
	 * Translate the given block into bytecodes.
	 * 
	 * @param blk
	 *            --- wyil block to be translated.
	 * @param freeSlot
	 *            --- identifies the first unsused bytecode register.
	 * @param bytecodes
	 *            --- list to insert bytecodes into *
	 */
	public void translate(Block blk, int freeSlot, HashMap<Value,Integer> constants,
			ArrayList<Bytecode> bytecodes) {
		for (Entry s : blk) {
			freeSlot = translate(s, freeSlot, constants, bytecodes);
		}
	}
	
	public int translate(Entry entry, int freeSlot,
			HashMap<Value,Integer> constants, ArrayList<Bytecode> bytecodes) {
		try {
			Code code = entry.code;
			if(code instanceof Assert) {
				 // translate((Assert)code,freeSlot,bytecodes);
			} else if(code instanceof BinOp) {
				 translate((BinOp)code,entry,freeSlot,bytecodes);
			} else if(code instanceof Convert) {
				 translate((Convert)code,freeSlot,bytecodes);
			} else if(code instanceof Const) {
				translate((Const) code, freeSlot, constants, bytecodes);
			} else if(code instanceof Debug) {
				 translate((Debug)code,freeSlot,bytecodes);
			} else if(code instanceof DictLoad) {
				 translate((DictLoad)code,freeSlot,bytecodes);
			} else if(code instanceof End) {
				 translate((End)code,freeSlot,bytecodes);
			} else if(code instanceof ExternJvm) {
				translate((ExternJvm)code,freeSlot,bytecodes);
			} else if(code instanceof Fail) {
				 translate((Fail)code,freeSlot,bytecodes);
			} else if(code instanceof FieldLoad) {
				 translate((FieldLoad)code,freeSlot,bytecodes);
			} else if(code instanceof ForAll) {
				 freeSlot = translate((ForAll)code,freeSlot,bytecodes);
			} else if(code instanceof Goto) {
				 translate((Goto)code,freeSlot,bytecodes);
			} else if(code instanceof IfGoto) {
				translate((IfGoto) code, entry, freeSlot, bytecodes);
			} else if(code instanceof IfType) {
				translate((IfType) code, entry, freeSlot, bytecodes);
			} else if(code instanceof IndirectInvoke) {
				 translate((IndirectInvoke)code,freeSlot,bytecodes);
			} else if(code instanceof IndirectSend) {
				 translate((IndirectSend)code,freeSlot,bytecodes);
			} else if(code instanceof Invoke) {
				 translate((Invoke)code,freeSlot,bytecodes);
			} else if(code instanceof Label) {
				translate((Label)code,freeSlot,bytecodes);
			} else if(code instanceof ListOp) {
				 translate((ListOp)code,entry,freeSlot,bytecodes);
			} else if(code instanceof ListLoad) {
				 translate((ListLoad)code,freeSlot,bytecodes);
			} else if(code instanceof Load) {
				 translate((Load)code,freeSlot,bytecodes);
			} else if(code instanceof Loop) {
				 translate((Loop)code,freeSlot,bytecodes);
			} else if(code instanceof MultiStore) {
				 translate((MultiStore)code,freeSlot,bytecodes);
			} else if(code instanceof NewDict) {
				 translate((NewDict)code,freeSlot,bytecodes);
			} else if(code instanceof NewList) {
				 translate((NewList)code,freeSlot,bytecodes);
			} else if(code instanceof NewRecord) {
				 translate((NewRecord)code,freeSlot,bytecodes);
			} else if(code instanceof NewSet) {
				 translate((NewSet)code,freeSlot,bytecodes);
			} else if(code instanceof Return) {
				 translate((Return)code,freeSlot,bytecodes);
			} else if(code instanceof Skip) {
				// do nothing
			} else if(code instanceof Send) {
				 translate((Send)code,freeSlot,bytecodes);
			} else if(code instanceof SetOp) {
				 translate((SetOp)code,entry,freeSlot,bytecodes);
			} else if(code instanceof Store) {
				 translate((Store)code,freeSlot,bytecodes);
			} else if(code instanceof Switch) {
				 translate((Switch)code,entry,freeSlot,bytecodes);
			} else if(code instanceof UnOp) {
				 translate((UnOp)code,freeSlot,bytecodes);
			} else {
				syntaxError("unknown wyil code encountered (" + code + ")", filename, entry);
			}
			
		} catch (SyntaxError ex) {
			throw ex;
		} catch (Exception ex) {		
			syntaxError("internal failure", filename, entry, ex);
		}
		
		return freeSlot;
	}
	
	public void translate(Code.Const c, int freeSlot,
			HashMap<Value,Integer> constants,
			ArrayList<Bytecode> bytecodes) {
		
		Value constant = c.constant;
		if (constant instanceof Value.Number || constant instanceof Value.Bool
				|| constant instanceof Value.Null) {
			translate(constant,freeSlot,bytecodes);					
		} else {
			int id;
			if(constants.containsKey(constant)) {
				id = constants.get(constant);
			} else {
				id = constants.size();
				constants.put(constant, id);				
			}
			String name = "constant$" + id;
			JvmType type = convertType(constant.type());
			bytecodes.add(new Bytecode.GetField(owner, name, type, Bytecode.STATIC));
		}		
	}
	
	public void translate(Code.Convert c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {		
		upConversion(c.to,c.from,freeSlot,bytecodes);				
	}
	
	protected void upConversion(Type toType, Type fromType,
			int freeSlot, ArrayList<Bytecode> bytecodes) {				
		if (Type.isomorphic(toType, fromType)) {		
			// do nothing!						
		} else if (!(toType instanceof Type.Bool) && fromType instanceof Type.Bool) {
			// this is either going into a union type, or the any type
			upConversion(toType, (Type.Bool) fromType, freeSlot, bytecodes);
		} else if(fromType == Type.T_INT) {									
			upConversion(toType, (Type.Int)fromType,freeSlot,bytecodes);  
		} else if(toType instanceof Type.List && fromType instanceof Type.List) {
			upConversion((Type.List) toType, (Type.List) fromType, freeSlot, bytecodes);			
		} else if(toType instanceof Type.Dictionary && fromType instanceof Type.List) {
			upConversion((Type.Dictionary) toType, (Type.List) fromType, freeSlot, bytecodes);			
		} else if(toType instanceof Type.Set && fromType instanceof Type.List) {
			upConversion((Type.Set) toType, (Type.List) fromType, freeSlot, bytecodes);			
		} else if(toType instanceof Type.Set && fromType instanceof Type.Set) {
			upConversion((Type.Set) toType, (Type.Set) fromType, freeSlot, bytecodes);			
		} else if(toType instanceof Type.Record && fromType instanceof Type.Record) {
			upConversion((Type.Record) toType, (Type.Record) fromType, freeSlot, bytecodes);
		} else {
			// every other kind of conversion is either a syntax error (which
			// should have been caught by TypeChecker); or, a nop (since no
			// conversion is required on this particular platform).
		}
	}
	
	protected void upConversion(Type.List toType, Type.List fromType,
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		
		if(fromType.element() == Type.T_VOID) {
			// nothing to do, in this particular case
			return;
		}
		
		// The following piece of code implements a java for-each loop which
		// iterates every element of the input collection, and recursively
		// converts it before loading it back onto a new WhileyList.
		
		String loopLabel = freshLabel();
		String exitLabel = freshLabel();
		int iter = freeSlot++;
		int tmp = freeSlot++;
		JvmType.Function ftype = new JvmType.Function(JAVA_UTIL_ITERATOR);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_COLLECTION, "iterator",
				ftype, Bytecode.INTERFACE));
		bytecodes.add(new Bytecode.Store(iter,
				JAVA_UTIL_ITERATOR));
		construct(WHILEYLIST,freeSlot,bytecodes);
		bytecodes.add(new Bytecode.Store(tmp, WHILEYLIST));
		bytecodes.add(new Bytecode.Label(loopLabel));
		ftype = new JvmType.Function(T_BOOL);
		bytecodes.add(new Bytecode.Load(iter,JAVA_UTIL_ITERATOR));
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "hasNext",
				ftype, Bytecode.INTERFACE));
		bytecodes.add(new Bytecode.If(Bytecode.If.EQ, exitLabel));
		bytecodes.add(new Bytecode.Load(tmp,WHILEYLIST));
		bytecodes.add(new Bytecode.Load(iter,JAVA_UTIL_ITERATOR));
		ftype = new JvmType.Function(JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "next",
				ftype, Bytecode.INTERFACE));						
		addReadConversion(fromType.element(),bytecodes);
		upConversion(toType.element(), fromType.element(), freeSlot,
				bytecodes);			
		ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "add",
				ftype, Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(T_BOOL));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(tmp,WHILEYLIST));
	}
	
	protected void upConversion(Type.Dictionary toType, Type.List fromType,
			int freeSlot, ArrayList<Bytecode> bytecodes) {
						
		if(fromType.element() == Type.T_VOID) {
			// nothing to do, in this particular case
			return;
		}
		
		// The following piece of code implements a java for-each loop which
		// iterates every element of the input collection, and recursively
		// converts it before loading it back onto a new WhileyList. 
		String loopLabel = freshLabel();
		String exitLabel = freshLabel();
		int iter = freeSlot++;
		int source = freeSlot++;
		int target = freeSlot++;		
		bytecodes.add(new Bytecode.Store(source,JAVA_UTIL_LIST));
		bytecodes.add(new Bytecode.LoadConst(0));		
		bytecodes.add(new Bytecode.Store(iter,T_INT));
				
		construct(WHILEYMAP,freeSlot,bytecodes);
		bytecodes.add(new Bytecode.Store(target, WHILEYMAP));
		bytecodes.add(new Bytecode.Label(loopLabel));
		JvmType.Function ftype = new JvmType.Function(T_INT);		
		bytecodes.add(new Bytecode.Load(iter,JvmTypes.T_INT));
		bytecodes.add(new Bytecode.Load(source,JAVA_UTIL_LIST));
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_LIST, "size",
				ftype, Bytecode.INTERFACE));
		bytecodes.add(new Bytecode.IfCmp(Bytecode.IfCmp.GE, T_INT, exitLabel));
		bytecodes.add(new Bytecode.Load(target,WHILEYSET));
		bytecodes.add(new Bytecode.Load(iter,T_INT));		
		ftype = new JvmType.Function(BIG_RATIONAL,T_INT);
		bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "valueOf",
				ftype, Bytecode.STATIC));				
		bytecodes.add(new Bytecode.Load(source,WHILEYMAP));
		bytecodes.add(new Bytecode.Load(iter,T_INT));
		ftype = new JvmType.Function(JAVA_LANG_OBJECT,T_INT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_LIST, "get",
				ftype, Bytecode.INTERFACE));						
		addReadConversion(fromType.element(),bytecodes);		
		upConversion(toType.value(), fromType.element(), freeSlot,
				bytecodes);			
		ftype = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYMAP, "put",
				ftype, Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		bytecodes.add(new Bytecode.Iinc(iter,1));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(target,WHILEYMAP));		
	}
	
	protected void upConversion(Type.Set toType, Type.List fromType,
			int freeSlot,			
			ArrayList<Bytecode> bytecodes) {
						
		if(fromType.element() == Type.T_VOID) {
			// nothing to do, in this particular case
			return;
		}				
		
		// The following piece of code implements a java for-each loop which
		// iterates every element of the input collection, and recursively
		// converts it before loading it back onto a new WhileyList. 
		String loopLabel = freshLabel();
		String exitLabel = freshLabel();
		int iter = freeSlot++;
		int tmp = freeSlot++;
		JvmType.Function ftype = new JvmType.Function(JAVA_UTIL_ITERATOR);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_COLLECTION, "iterator",
				ftype, Bytecode.INTERFACE));
		bytecodes.add(new Bytecode.Store(iter,
				JAVA_UTIL_ITERATOR));
		construct(WHILEYSET,freeSlot,bytecodes);
		bytecodes.add(new Bytecode.Store(tmp, WHILEYSET));
		bytecodes.add(new Bytecode.Label(loopLabel));
		ftype = new JvmType.Function(T_BOOL);
		bytecodes.add(new Bytecode.Load(iter,JAVA_UTIL_ITERATOR));
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "hasNext",
				ftype, Bytecode.INTERFACE));
		bytecodes.add(new Bytecode.If(Bytecode.If.EQ, exitLabel));
		bytecodes.add(new Bytecode.Load(tmp,WHILEYSET));
		bytecodes.add(new Bytecode.Load(iter,JAVA_UTIL_ITERATOR));
		ftype = new JvmType.Function(JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "next",
				ftype, Bytecode.INTERFACE));						
		addReadConversion(fromType.element(),bytecodes);
		upConversion(toType.element(), fromType.element(), freeSlot,
				bytecodes);			
		ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "add",
				ftype, Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(T_BOOL));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(tmp,WHILEYSET));
	}
	
	protected void upConversion(Type.Set toType, Type.Set fromType,
			int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		
		if(fromType.element() == Type.T_VOID) {
			// nothing to do, in this particular case
			return;
		}
		
		// The following piece of code implements a java for-each loop which
		// iterates every element of the input collection, and recursively
		// converts it before loading it back onto a new WhileyList. 
		String loopLabel = freshLabel();
		String exitLabel = freshLabel();
		int iter = freeSlot++;
		int tmp = freeSlot++;		
		JvmType.Function ftype = new JvmType.Function(JAVA_UTIL_ITERATOR);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_COLLECTION, "iterator",
				ftype, Bytecode.INTERFACE));
		bytecodes.add(new Bytecode.Store(iter,
				JAVA_UTIL_ITERATOR));
		construct(WHILEYSET,freeSlot,bytecodes);
		bytecodes.add(new Bytecode.Store(tmp, WHILEYSET));
		bytecodes.add(new Bytecode.Label(loopLabel));
		ftype = new JvmType.Function(T_BOOL);
		bytecodes.add(new Bytecode.Load(iter,JAVA_UTIL_ITERATOR));
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "hasNext",
				ftype, Bytecode.INTERFACE));
		bytecodes.add(new Bytecode.If(Bytecode.If.EQ, exitLabel));
		bytecodes.add(new Bytecode.Load(tmp,WHILEYSET));
		bytecodes.add(new Bytecode.Load(iter,JAVA_UTIL_ITERATOR));
		ftype = new JvmType.Function(JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "next",
				ftype, Bytecode.INTERFACE));
		addCheckCast(convertType(fromType.element()),bytecodes);		
		upConversion(toType.element(), fromType.element(), freeSlot,
				bytecodes);			
		ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "add",
				ftype, Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(T_BOOL));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(tmp,WHILEYSET));
	}
	
	public void upConversion(Type.Record toType, Type.Record fromType,
			int freeSlot,
			ArrayList<Bytecode> bytecodes) {		
		int slot = freeSlot++;		
		bytecodes.add(new Bytecode.Store(slot,WHILEYRECORD));
		Map<String,Type> toFields = toType.fields();
		Map<String,Type> fromFields = fromType.fields();
		for(String key : toFields.keySet()) {
			Type to = toFields.get(key);
			Type from = fromFields.get(key);	
			if(Type.isomorphic(to,from)) {
				// can skip
			} else {
				bytecodes.add(new Bytecode.Load(slot,WHILEYRECORD));
				bytecodes.add(new Bytecode.LoadConst(key));
				bytecodes.add(new Bytecode.Load(slot,WHILEYRECORD));
				bytecodes.add(new Bytecode.LoadConst(key));
				JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);			
				bytecodes.add(new Bytecode.Invoke(WHILEYRECORD,"get",ftype,Bytecode.VIRTUAL));								
				addReadConversion(from,bytecodes);							
				// now perform recursive conversion
				upConversion(to,from,freeSlot,bytecodes);							
				ftype = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);			
				bytecodes.add(new Bytecode.Invoke(WHILEYRECORD,"put",ftype,Bytecode.VIRTUAL));
				bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
			}
		}
		bytecodes.add(new Bytecode.Load(slot,WHILEYRECORD));		
	}
	
	public void upConversion(Type toType, Type.Bool fromType,
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_BOOLEAN,T_BOOL);			
		bytecodes.add(new Bytecode.Invoke(JAVA_LANG_BOOLEAN,"valueOf",ftype,Bytecode.STATIC));			
		// done deal!
	}
	
	public void upConversion(Type toType, Type.Int fromType,
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		// no op
	}

	public void translate(Code.Store c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {		
		bytecodes.add(new Bytecode.Store(c.slot, convertType(c.type)));				
	}

	public void translate(Code.MultiStore c, int freeSlot,ArrayList<Bytecode> bytecodes) {
		
		// Now, my general feeling is that the multistore bytecode could use
		// some work. Essentially, to simplify this process of figuring our what
		// is being updated.
		
		// First, check if this is updating the process' state
		Type type = c.type;
				
		if(c.slot == 0 && Type.isSubtype(Type.T_PROCESS(Type.T_ANY), type)) {
			Type.Process p = (Type.Process) type;
			type = p.element();
		}
		
		// Second, determine type of value being assigned
		
		List<String> fields = c.fields;
		int fi = 0;						
		Type iter = type;
		for(int i=0;i!=c.level;++i) {
			if(Type.isSubtype(Type.T_DICTIONARY(Type.T_ANY, Type.T_ANY),iter)) {
				Type.Dictionary dict = Type.effectiveDictionaryType(iter);				
				iter = dict.value();
			} else if(Type.isSubtype(Type.T_LIST(Type.T_ANY),iter)) {
				Type.List list = Type.effectiveListType(iter);
				iter = list.element();
			} else {
				Type.Record rec = Type.effectiveRecordType(iter);
				String field = fields.get(fi++);
				iter = rec.fields().get(field);
			}	
		}
		
		// Third, store the value to be assigned				
		JvmType val_t = convertType(iter);		
		bytecodes.add(new Bytecode.Store(freeSlot,val_t));
		bytecodes.add(new Bytecode.Load(c.slot, convertType(c.type)));
		
		if(type != c.type) {
			// this case is for assignments to process states
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT);		
			bytecodes.add(new Bytecode.Invoke(WHILEYPROCESS, "state", ftype,
					Bytecode.VIRTUAL));
			// finally, we need to cast the object we got back appropriately.		
			Type.Process pt = (Type.Process) c.type;						
			addReadConversion(pt.element(), bytecodes);
		}
		
		// Fourth, finally process the assignment path and update the object in
		// question.		
		iter = type;
		fi = 0;								
		for(int i=0;i!=c.level;++i) {
			boolean read = (i != c.level - 1);
			
			if(Type.isSubtype(Type.T_DICTIONARY(Type.T_ANY, Type.T_ANY),iter)) {
				Type.Dictionary dict = Type.effectiveDictionaryType(iter);				
				bytecodes.add(new Bytecode.Swap());
				if(read) { 
					JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
							JAVA_LANG_OBJECT);
					bytecodes.add(new Bytecode.Invoke(WHILEYMAP, "get", ftype,
						Bytecode.VIRTUAL));
					addReadConversion(dict.value(),bytecodes);
				} else {
					JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
							JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);
					bytecodes.add(new Bytecode.Load(freeSlot, val_t));
					addWriteConversion(dict.value(),bytecodes);
					bytecodes.add(new Bytecode.Invoke(WHILEYMAP, "put", ftype,
							Bytecode.VIRTUAL));
					bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
				}
				iter = dict.value();
			} else if(Type.isSubtype(Type.T_LIST(Type.T_ANY),iter)) {
				Type.List list = Type.effectiveListType(iter);
				JvmType.Function ftype = new JvmType.Function(T_INT);
				bytecodes.add(new Bytecode.Swap());
				bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "intValue", ftype,
						Bytecode.VIRTUAL));				
				if(read) {
					ftype = new JvmType.Function(JAVA_LANG_OBJECT,
							T_INT);
					bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "get", ftype,
							Bytecode.VIRTUAL));
					addReadConversion(list.element(),bytecodes);
				} else {
					ftype = new JvmType.Function(JAVA_LANG_OBJECT,
							T_INT,JAVA_LANG_OBJECT);
					bytecodes.add(new Bytecode.Load(freeSlot, val_t));
					addWriteConversion(list.element(),bytecodes);
					bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "set", ftype,
							Bytecode.VIRTUAL));
					bytecodes.add(new Bytecode.Pop(T_BOOL));
				}
				iter = list.element();
			} else {
				Type.Record rec = Type.effectiveRecordType(iter);
				String field = fields.get(fi++);
				bytecodes.add(new Bytecode.LoadConst(field));				
				if(read) {
					JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);
					bytecodes.add(new Bytecode.Invoke(WHILEYRECORD,"get",ftype,Bytecode.VIRTUAL));				
					addReadConversion(rec.fields().get(field),bytecodes);
				} else {
					JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);
					bytecodes.add(new Bytecode.Load(freeSlot, val_t));
					addWriteConversion(rec.fields().get(field),bytecodes);
					bytecodes.add(new Bytecode.Invoke(WHILEYRECORD,"put",ftype,Bytecode.VIRTUAL));
					bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
				}
				iter = rec.fields().get(field);
			}
		}		
	}

	public void translate(Code.Return c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		if (c.type == Type.T_VOID) {
			bytecodes.add(new Bytecode.Return(null));
		} else {
			bytecodes.add(new Bytecode.Return(convertType(c.type)));
		}
	}

	public void translate(Code.Throw c, Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {			
		bytecodes.add(new Bytecode.New(WHILEYEXCEPTION));
		bytecodes.add(new Bytecode.DupX1());
		bytecodes.add(new Bytecode.Swap());
		
		bytecodes.add(new Bytecode.Swap());		
		JvmType.Function ftype = new JvmType.Function(T_VOID,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYEXCEPTION, "<init>", ftype,
				Bytecode.SPECIAL));		
		bytecodes.add(new Bytecode.Throw());
	}
	
	public void translate(Code.Switch c, Block.Entry entry, int freeSlot,
			ArrayList<Bytecode> bytecodes) {

		ArrayList<Pair<Integer, String>> cases = new ArrayList();
		boolean canUseSwitchBytecode = true;
		for (Pair<Value, String> p : c.branches) {
			// first, check whether the switch value is indeed an integer.
			Value v = (Value) p.first();
			if (!(v instanceof Value.Number && ((Value.Number) v).value
					.isInteger())) {
				canUseSwitchBytecode = false;
				break;
			}
			// second, check whether integer value can fit into a Java int
			Value.Number vi = (Value.Number) v;
			int iv = vi.value.intValue();
			if (!BigRational.valueOf(iv).equals(vi.value)) {
				canUseSwitchBytecode = false;
				break;
			}
			// ok, we're all good so far
			cases.add(new Pair(iv, p.second()));
		}

		if (canUseSwitchBytecode) {
			JvmType.Function ftype = new JvmType.Function(T_INT);
			bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "intValue", ftype,
					Bytecode.VIRTUAL));
			bytecodes.add(new Bytecode.Switch(c.defaultTarget, cases));
		} else {
			// ok, in this case we have to fall back to series of the if
			// conditions. Not ideal.  
			bytecodes.add(new Bytecode.Store(freeSlot, convertType(c.type)));
			for (Pair<Value, String> p : c.branches) {
				Value value = p.first();
				String target = p.second();
				translate(value,freeSlot+1,bytecodes);
				bytecodes.add(new Bytecode.Load(freeSlot, convertType(c.type)));				
				// Now, construct fake bytecode to do the comparison.
				// FIXME: bug if types require some kind of coercion
				Code.IfGoto ifgoto = Code.IfGoto(value.type(),Code.COp.EQ,target);			
				translate(ifgoto,entry,freeSlot+1,bytecodes);
			}
			bytecodes.add(new Bytecode.Goto(c.defaultTarget));
		}
	}
	
	public void translate(Code.IfGoto c, Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {	
				
		JvmType type = convertType(c.type);
		if(c.type == Type.T_BOOL) {
			// boolean is a special case, since it is not implemented as an
			// object on the JVM stack. Therefore, we need to use the "if_cmp"
			// bytecode, rather than calling .equals() and using "if" bytecode.
			switch(c.op) {
			case EQ:				
				bytecodes.add(new Bytecode.IfCmp(Bytecode.IfCmp.EQ, type, c.target));
				break;			
			case NEQ:				
				bytecodes.add(new Bytecode.IfCmp(Bytecode.IfCmp.NE, type, c.target));				
				break;			
			}
		} else {
			// Non-boolean case. Just use the Object.equals() method, followed
			// by "if" bytecode.			
			int op;
			switch(c.op) {
			case EQ:
			{				
				if(Type.isSubtype(c.type, Type.T_NULL)) {
					// this indicates an interesting special case. The left
					// handside of this equality can be null. Therefore, we
					// cannot directly call "equals()" on this method, since
					// this would cause a null pointer exception!
					JvmType.Function ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);
					bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "equals", ftype,
							Bytecode.STATIC));
				} else {					
					JvmType.Function ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
					bytecodes.add(new Bytecode.Invoke((JvmType.Clazz)type, "equals", ftype,
							Bytecode.VIRTUAL));								
				}
				op = Bytecode.If.NE;
				break;
			}
			case NEQ:
			{
				if (Type.isSubtype(c.type, Type.T_NULL)) {
					// this indicates an interesting special case. The left
					// handside of this equality can be null. Therefore, we
					// cannot directly call "equals()" on this method, since
					// this would cause a null pointer exception!
					JvmType.Function ftype = new JvmType.Function(T_BOOL,
							JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
					bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "equals",
							ftype, Bytecode.STATIC));
				} else {
					JvmType.Function ftype = new JvmType.Function(T_BOOL,
							JAVA_LANG_OBJECT);
					bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type,
							"equals", ftype, Bytecode.VIRTUAL));
				}
				op = Bytecode.If.EQ;
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
				JvmType.Function ftype = new JvmType.Function(T_INT, type);
				bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type,
						"compareTo", ftype, Bytecode.VIRTUAL));
				op = Bytecode.If.GT;
				break;
			}
			case GTEQ:
			{		
				JvmType.Function ftype = new JvmType.Function(T_INT,type);
				bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type,
						"compareTo", ftype, Bytecode.VIRTUAL));			
				op = Bytecode.If.GE;
				break;
			}
			case SUBSETEQ:
			{
				JvmType.Function ftype = new JvmType.Function(T_BOOL,WHILEYSET);
				bytecodes.add(new Bytecode.Invoke(WHILEYSET, "subsetEq", ftype,
						Bytecode.VIRTUAL));
				op = Bytecode.If.NE;
				break;
			}
			case SUBSET:
			{
				JvmType.Function ftype = new JvmType.Function(T_BOOL,WHILEYSET);
				bytecodes.add(new Bytecode.Invoke(WHILEYSET, "subset", ftype,
						Bytecode.VIRTUAL));
				op = Bytecode.If.NE;
				break;
			}
			case ELEMOF:
			{
				JvmType.Function ftype = new JvmType.Function(T_BOOL,
						JAVA_LANG_OBJECT);
				bytecodes.add(new Bytecode.Swap());
				bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_COLLECTION, "contains",
						ftype, Bytecode.INTERFACE));
				op = Bytecode.If.NE;
				break;
			}			
			
			default:
				syntaxError("unknown if condition encountered",filename,stmt);
				return;
			}
			
			// do the jump
			bytecodes.add(new Bytecode.If(op, c.target));
		}
	}
	
	public void translate(Code.IfType c, Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {						
		
		// This method (including the helper) is pretty screwed up. It needs a
		// serious rethink to catch all cases, and to be efficient.
		
		String exitLabel = freshLabel();
		String trueLabel = freshLabel();
		bytecodes.add(new Bytecode.Load(c.slot, convertType(c.type)));
		translateTypeTest(trueLabel, c.type, c.test, stmt, bytecodes);
									
		Type gdiff = Type.leastDifference(c.type,c.test);			
		bytecodes.add(new Bytecode.Load(c.slot, convertType(c.type)));
		addReadConversion(gdiff,bytecodes);		
		bytecodes.add(new Bytecode.Store(c.slot,convertType(gdiff)));							
		bytecodes.add(new Bytecode.Goto(exitLabel));
		bytecodes.add(new Bytecode.Label(trueLabel));
				
		Type glb = Type.greatestLowerBound(c.type, c.test);
		bytecodes.add(new Bytecode.Load(c.slot, convertType(c.type)));
		addReadConversion(glb,bytecodes);		
		bytecodes.add(new Bytecode.Store(c.slot,convertType(glb)));			
		bytecodes.add(new Bytecode.Goto(c.target));
		bytecodes.add(new Bytecode.Label(exitLabel));		
	}
	
	// The purpose of this method is to translate a type test. We're testing to
	// see whether what's on the top of the stack (the value) is a subtype of
	// the type being tested. The difference between value's static type and
	// test type help to reduce the scope of the test. For example, if the
	// static type is int|[int] and the test type is int, then all we need is to
	// perform an instanceof BigInteger. Other situations are trickier. For
	// example, testing a static type [int]|[bool] against type [int] is harder,
	// since both are actually instances of java.util.List. 
	protected void translateTypeTest(String trueTarget, Type src, Type test,
			Entry stmt, ArrayList<Bytecode> bytecodes) {		
						
		// First, determine the intersection of the actual type and the type
		// we're testing for.  This is really an optimisation.
		test = Type.greatestLowerBound(src,test);					
				
		if(test == Type.T_VOID) {
			// in this case, we must fail.
			bytecodes.add(new Bytecode.Pop(convertType(src)));			
		} else if(Type.isSubtype(test, src)) {			
			// in this case, we must succeed.
			bytecodes.add(new Bytecode.Pop(convertType(src)));
			bytecodes.add(new Bytecode.Goto(trueTarget));
		} else if (test instanceof Type.Null) {
			// Easy case		
			bytecodes.add(new Bytecode.If(Bytecode.If.NULL, trueTarget));
		} else if(test instanceof Type.Int) {
			translateTypeTest(trueTarget, src, (Type.Int) test,
					stmt, bytecodes);
		} else if(test instanceof Type.Real) {
			translateTypeTest(trueTarget, src, (Type.Real) test,
					stmt, bytecodes);			
		} else if(test instanceof Type.List) {
			translateTypeTest(trueTarget, src, (Type.List) test,
					stmt, bytecodes);			
		} else if(test instanceof Type.Set) {
			translateTypeTest(trueTarget, src, (Type.Set) test, stmt, bytecodes);			
		} else if(test instanceof Type.Record) {				
			translateTypeTest(trueTarget, src, (Type.Record) test, stmt,
					bytecodes);			
		} else if(test instanceof Type.Union){
			translateTypeTest(trueTarget, src, (Type.Union) test, stmt,
					bytecodes);			
		} 			
	}

	/**
	 * Check that a value of the given src type is an integer. The main
	 * difficulty here, is that it may not be enough to just test whether the
	 * value is a BigInteger. For example, if src has type real then we have a
	 * BigRational, and we must call the isInteger() method instead.
	 * 
	 * @param trueTarget --- target branch if test succeeds
	 * @param src --- type of expression being tested
	 * @param test --- type of test
	 * @param stmt --- stmt containing test (useful for line number info)
	 * @param bytecodes --- list of bytecodes (to which test is appended) 	 
	 */
	protected void translateTypeTest(String trueTarget, Type src, Type.Int test,
			Entry stmt, ArrayList<Bytecode> bytecodes) {
		
		String falseTarget = freshLabel();
				
		if(!Type.isSubtype(Type.T_REAL,src)) {
			String nextTarget = freshLabel();
			bytecodes.add(new Bytecode.Dup(BIG_RATIONAL));
			bytecodes.add(new Bytecode.InstanceOf(BIG_RATIONAL));			
			bytecodes.add(new Bytecode.If(Bytecode.If.NE, nextTarget));
			bytecodes.add(new Bytecode.Pop(BIG_RATIONAL));
			bytecodes.add(new Bytecode.Goto(falseTarget));
			bytecodes.add(new Bytecode.Label(nextTarget));
			addCheckCast(BIG_RATIONAL,bytecodes);
		}		
		JvmType.Function ftype = new JvmType.Function(JvmTypes.T_BOOL);
		bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL,"isInteger", ftype, Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.If(Bytecode.If.NE, trueTarget));
		bytecodes.add(new Bytecode.Label(falseTarget));		
	}
	
	/**
	 * Check that a value of the given src type is a real. 
	 * 
	 * @param trueTarget --- target branch if test succeeds
	 * @param src --- type of expression being tested
	 * @param test --- type of test
	 * @param stmt --- stmt containing test (useful for line number info)
	 * @param bytecodes --- list of bytecodes (to which test is appended) 	 
	 */
	protected void translateTypeTest(String trueTarget, Type src, Type.Real test,
			Entry stmt, ArrayList<Bytecode> bytecodes) {
		
		// NOTE: on entry we know that src cannot be a Type.Real, since this case
		// would have been already caught.
									
		bytecodes.add(new Bytecode.InstanceOf(BIG_RATIONAL));
		bytecodes.add(new Bytecode.If(Bytecode.If.NE, trueTarget));				
	}
	
	/**
	 * Check that a value of the given src type matches the list type given by
	 * test. If src is not a list, then we must begin by performing an
	 * instanceof WHILEYLIST. If this is true, then we may need to further
	 * distinguish the elements of the list. For example, testing [real] ~=
	 * [int] requires iterating the elements of the list to check that they are
	 * all indeed ints.
	 * 
	 * @param trueTarget --- target branch if test succeeds
	 * @param src --- type of expression being tested
	 * @param test --- type of test
	 * @param stmt --- stmt containing test (useful for line number info)
	 * @param bytecodes --- list of bytecodes (to which test is appended) 
	 */
	protected void translateTypeTest(String trueTarget, Type src, Type.List test,
			Entry stmt, ArrayList<Bytecode> bytecodes) {								
		
		// ======================================================================
		// First, perform an instanceof test (if necessary) 
		// ======================================================================
		
		Type.List nsrc;
		String falseTarget = freshLabel();
		
		if(src instanceof Type.List) {
			// We already know the value is a list, so we don't need to perform
			// an instanceof test.		
			nsrc = (Type.List) src;
		} else {			
			bytecodes.add(new Bytecode.Dup(convertType(src)));		
			bytecodes.add(new Bytecode.InstanceOf(WHILEYLIST));
			bytecodes.add(new Bytecode.If(Bytecode.If.EQ, falseTarget));
			addCheckCast(WHILEYLIST,bytecodes);				
			
			nsrc = Type.effectiveListType(Type.greatestLowerBound(src, Type.T_LIST(Type.T_ANY)));						
			
			if(Type.isSubtype(test,nsrc)) {				
				// Getting here indicates that the instanceof test was
				// sufficient to be certain that the type test succeeds.			
				bytecodes.add(new Bytecode.Pop(WHILEYLIST));
				bytecodes.add(new Bytecode.Goto(trueTarget));				
				bytecodes.add(new Bytecode.Label(falseTarget));
				bytecodes.add(new Bytecode.Pop(WHILEYLIST));
				return;
			}
		}
						
		// ======================================================================
		// Second, check empty list case (as this always passes) 
		// ======================================================================		
		
		String nextTarget = freshLabel();		
		bytecodes.add(new Bytecode.Dup(WHILEYLIST));
		JvmType.Function fun_t = new JvmType.Function(JvmTypes.T_INT);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "size", fun_t , Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.If(Bytecode.If.NE, nextTarget));
		bytecodes.add(new Bytecode.Pop(WHILEYLIST));
		bytecodes.add(new Bytecode.Goto(trueTarget));
		bytecodes.add(new Bytecode.Label(nextTarget));
		
		// ======================================================================
		// Third, check elements of list (tricky)
		// ======================================================================		
						
		fun_t = new JvmType.Function(JAVA_LANG_OBJECT,JvmTypes.T_INT);
		bytecodes.add(new Bytecode.LoadConst(new Integer(0)));
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "get", fun_t , Bytecode.VIRTUAL));			
		translateTypeTest(trueTarget,nsrc.element(),test.element(),stmt,bytecodes);
		String finalTarget = freshLabel();
		bytecodes.add(new Bytecode.Goto(finalTarget));
		
		// Add the false label for the case when the original instanceof test fails
		bytecodes.add(new Bytecode.Label(falseTarget));
		bytecodes.add(new Bytecode.Pop(WHILEYLIST));
		bytecodes.add(new Bytecode.Label(finalTarget));		
	}

	/**
	 * Check that a value of the given src type matches the set type given by
	 * test. If src is not a set, then we must begin by performing an
	 * instanceof WHILEYSET. If this is true, then we may need to further
	 * distinguish the elements of the list. For example, testing {real} ~=
	 * {int} requires iterating the elements of the list to check that they are
	 * all indeed ints.
	 * 
	 * @param trueTarget --- target branch if test succeeds
	 * @param src --- type of expression being tested
	 * @param test --- type of test
	 * @param stmt --- stmt containing test (useful for line number info)
	 * @param bytecodes --- list of bytecodes (to which test is appended) 
	 */
	protected void translateTypeTest(String trueTarget, Type src, Type.Set test,
			Entry stmt, ArrayList<Bytecode> bytecodes) {
		
		// ======================================================================
		// First, perform an instanceof test (if necessary) 
		// ======================================================================
		
		Type.Set nsrc;
		String falseTarget = freshLabel();
		
		if(src instanceof Type.List) {
			// We already know the value is a list, so we don't need to perform
			// an instanceof test.		
			nsrc = (Type.Set) src;
		} else {			
			bytecodes.add(new Bytecode.Dup(convertType(src)));		
			bytecodes.add(new Bytecode.InstanceOf(WHILEYSET));
			bytecodes.add(new Bytecode.If(Bytecode.If.EQ, falseTarget));
			addCheckCast(WHILEYSET,bytecodes);				
			
			// FIXME: this is a bug as we're guaranteed to have a set type
			// here. For example, int|{int}|{real} & [*] ==> {int}|{real} (by S-UNION2)
			nsrc = (Type.Set) Type.greatestLowerBound(src, Type.T_SET(Type.T_ANY));
			
			if(Type.isSubtype(test,nsrc)) {
				// Getting here indicates that the instanceof test was
				// sufficient to be certain that the type test succeeds.			
				bytecodes.add(new Bytecode.Pop(WHILEYSET));
				bytecodes.add(new Bytecode.Goto(trueTarget));				
				bytecodes.add(new Bytecode.Label(falseTarget));
				bytecodes.add(new Bytecode.Pop(WHILEYSET));
				return;
			}
		}
						
		// ======================================================================
		// Second, check empty set case (as this always passes) 
		// ======================================================================		
		
		String nextTarget = freshLabel();
		bytecodes.add(new Bytecode.Dup(WHILEYSET));
		JvmType.Function fun_t = new JvmType.Function(JvmTypes.T_INT);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "size", fun_t , Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.If(Bytecode.If.NE, nextTarget));
		bytecodes.add(new Bytecode.Pop(WHILEYSET));
		bytecodes.add(new Bytecode.Goto(trueTarget));
		bytecodes.add(new Bytecode.Label(nextTarget));
		
		// ======================================================================
		// Third, check elements of list (tricky)
		// ======================================================================		
						
		fun_t = new JvmType.Function(JAVA_UTIL_ITERATOR);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "iterator", fun_t , Bytecode.VIRTUAL));			
		fun_t = new JvmType.Function(JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "next", fun_t , Bytecode.INTERFACE));			
		translateTypeTest(trueTarget,nsrc.element(),test.element(),stmt,bytecodes);
		
		// Add the false label for the case when the original instanceof test fails
		bytecodes.add(new Bytecode.Label(falseTarget));
		bytecodes.add(new Bytecode.Pop(WHILEYSET));
	}

	/**
	 * <p>
	 * Check that a value of the given src type matches the record type given by
	 * test. If src is not a record, then we must begin by performing an
	 * instanceof WHILEYRECORD. If this is true, then we may need to further and
	 * to distinguish which fields it actually has. We optimise this by making
	 * the least number of field checks possible. For example, consider this
	 * type:
	 * </p>
	 * 
	 * <pre>
	 * define RType as {int x,int y}|{int x, int z}|int
	 * 
	 * int f(RType r):
	 *     if e &tilde;= {int x, int y}
	 *         return 1
	 *     else:
	 *         return 2
	 * </pre>
	 * <p>
	 * In this case, <code>r</code> will have type <code>Object</code> on entry.
	 * Thus, we must perform an <code>instanceof</code>
	 * <code>WhileyRecord</code> check. Furthermore, in the true case, we must
	 * establish whether or not we have a field <code>y</code> (but we don't
	 * need to test for field <code>x</code>).
	 * </p>
	 * 
	 * @param trueTarget
	 *            --- target branch if test succeeds
	 * @param src
	 *            --- type of expression being tested
	 * @param test
	 *            --- type of test
	 * @param stmt
	 *            --- stmt containing test (useful for line number info)
	 * @param bytecodes
	 *            --- list of bytecodes (to which test is appended)
	 */
	protected void translateTypeTest(String trueTarget, Type src,
			Type.Record test, Entry stmt, ArrayList<Bytecode> bytecodes) {
				
		JvmType.Function fun_t = new JvmType.Function(JvmTypes.JAVA_LANG_OBJECT,JvmTypes.JAVA_LANG_OBJECT);
		
		// ======================================================================
		// First, perform an instanceof test (if necessary) 
		// ======================================================================
				
		String falseTarget = freshLabel();		
		if(!(src instanceof Type.Record)) {			
			if(Type.effectiveRecordType(src) == null) {					
				// not guaranteed to have a record here, so ensure we do.				
				bytecodes.add(new Bytecode.Dup(convertType(src)));		
				bytecodes.add(new Bytecode.InstanceOf(WHILEYRECORD));
				bytecodes.add(new Bytecode.If(Bytecode.If.EQ, falseTarget));			
				addCheckCast(WHILEYRECORD,bytecodes);	

				// Narrow the type down to a record (which we now know is true)			
				src = narrowRecordType(src);							
				
				if(Type.isSubtype(test,src)) {				
					// Getting here indicates that the instanceof test was
					// sufficient to be certain that the type test succeeds.			
					bytecodes.add(new Bytecode.Pop(WHILEYRECORD));
					bytecodes.add(new Bytecode.Goto(trueTarget));
					bytecodes.add(new Bytecode.Label(falseTarget));
					bytecodes.add(new Bytecode.Pop(WHILEYRECORD));
					return;
				}								
			}
			
			// ======================================================================
			// Second, determine if correct fields present  
			// ======================================================================
			
			Set<String> fields = identifyDistinguishingFields(src,test.fields().keySet()); 									
			
			for(String f : fields) {
				bytecodes.add(new Bytecode.Dup(WHILEYRECORD));
				bytecodes.add(new Bytecode.LoadConst(f));				
				bytecodes.add(new Bytecode.Invoke(WHILEYRECORD, "get", fun_t , Bytecode.VIRTUAL));
				bytecodes.add(new Bytecode.If(Bytecode.If.NULL,falseTarget));
			}
			
			src = narrowRecordType(src,test.fields().keySet());
			
			if(Type.isSubtype(test,src)) {				
				// Getting here indicates that distinguishing fields test was
				// sufficient to be certain that the type test succeeds.			
				bytecodes.add(new Bytecode.Pop(WHILEYRECORD));
				bytecodes.add(new Bytecode.Goto(trueTarget));				
				bytecodes.add(new Bytecode.Label(falseTarget));
				bytecodes.add(new Bytecode.Pop(WHILEYRECORD));
				return;
			}
		}
		
		// ======================================================================
		// Third, perform (minimal) number of type tests for fields (i.e. S-DEPTH) 
		// ======================================================================
		
		// Note, we could potentially do better here by avoid multiple look ups
		// of the same field. This can happen if the field in question is used
		// for distinguishing different records (see above). 
		
		Type.Record nsrc = Type.effectiveRecordType(src);
		
		for(Map.Entry<String,Type> e : test.fields().entrySet()) {
			String field = e.getKey();
			Type testType = e.getValue();
			Type srcType = nsrc.fields().get(field);			
			if(srcType == null) { srcType = Type.T_ANY; }
			if(!Type.isSubtype(testType,srcType)) {
				// this field needs to be checked
				String nextTarget = freshLabel();
				bytecodes.add(new Bytecode.Dup(WHILEYRECORD));
				bytecodes.add(new Bytecode.LoadConst(field));				
				bytecodes.add(new Bytecode.Invoke(WHILEYRECORD, "get", fun_t , Bytecode.VIRTUAL));
				addReadConversion(srcType,bytecodes);
				// TODO: I don't think we always need to do this.
				translateTypeTest(nextTarget,srcType,testType,stmt,bytecodes);
				bytecodes.add(new Bytecode.Goto(falseTarget));
				bytecodes.add(new Bytecode.Label(nextTarget));
			}
		}
		
		// Ok, we have a match!
		bytecodes.add(new Bytecode.Pop(WHILEYRECORD));
		bytecodes.add(new Bytecode.Goto(trueTarget));
		
		bytecodes.add(new Bytecode.Label(falseTarget));
		bytecodes.add(new Bytecode.Pop(WHILEYRECORD));
	}
	
	// The following method should be replaced in future by a GLB test, using an
	// "open" record type (which currently doesn't exist).
	protected Type narrowRecordType(Type t) {

		if (t instanceof Type.Any || t instanceof Type.Void || t instanceof Type.Null
				|| t instanceof Type.Real || t instanceof Type.Int || t instanceof Type.Bool
				|| t instanceof Type.Meta || t instanceof Type.Existential
				|| t instanceof Type.Process || t instanceof Type.List
				|| t instanceof Type.Set) {
			return Type.T_VOID;
		} else if(t instanceof Type.Record) {
				return (Type) t;
		}
		
		// Ok, must be union ...
						
		Type.Union u = (Type.Union) t;
		Type lub = Type.T_VOID;
		for(Type b : u.bounds()) {						
			lub = Type.leastUpperBound(narrowRecordType(b),lub);			
		}
		
		return lub;
	}

	// identify the greatest type which has *exactly* those fields given.
	protected Type narrowRecordType(Type t, Set<String> fields) {
		HashMap<String,Type> types = new HashMap<String,Type>();
		for(String f : fields) {
			types.put(f, Type.T_ANY);
		}
		Type ub = Type.T_RECORD(types);				
		
		return Type.greatestLowerBound(t, ub);		
	}
	
	/**
	 * <p>The following method accepts a type whose values are guaranteed to be
	 * records, and a string of required fields. It then attempts to determine
	 * the minimal number of field checks required to be sure a value of the
	 * given type has exactly the given fields.</p>
	 * 
	 * @param src
	 * @param test
	 * @return
	 */
	protected Set<String> identifyDistinguishingFields(Type src, Set<String> fields) {		
		if(src instanceof Type.Record) {
			return Collections.EMPTY_SET;
		}
		
		// The real challenge with all of these methods is understanding what
		// the possible type forms are at any point.		
		Type.Union ut = (Type.Union) src; 
		
		boolean finished = false;
		HashSet<String> solution = new HashSet<String>();
		
		while(!finished) {			
			HashMap<String,Integer> conflicts = new HashMap<String,Integer>();
			
			// First, initialise conflicts
			
			for(String f : fields) {
				conflicts.put(f, 0);
			}
			
			// Second, count conflicts
			
			for(Type t : ut.bounds()) {
				
				// FIXME: following obviously broken, as unsure if t is record
				Type.Record rt = (Type.Record) t;
				Set<String> rt_types_keySet = rt.fields().keySet(); 
				if(rt_types_keySet.containsAll(solution)) {
					// only count conflicts from records not already discounted.
					for(String f : rt_types_keySet) {
						Integer conflict = conflicts.get(f);
						if(conflict != null) { conflicts.put(f, conflict+1); }
					}
				}
			}						
			
			// Third, select least conflict
			
			String chosen = null;
			int min = Integer.MAX_VALUE;
			
			// Now, identifiy least conflict
			for(Map.Entry<String,Integer> c : conflicts.entrySet()) {
				int val = c.getValue();
				String key = c.getKey();
				if(!solution.contains(key) && val < min) {
					min = val;
					chosen = key;					
				}
			}
			
			solution.add(chosen);				
			if(min == 1 || solution.size() == fields.size()) { finished = true; }
		}
		
		return solution;
	}

	/**
	 * Check that a value of the given src type matches the union type given by
	 * test. The challenge here is to implement this efficiently, by avoiding
	 * unnecessarily repeating instanceof tests.
	 * 
	 * @param trueTarget
	 *            --- target branch if test succeeds
	 * @param src
	 *            --- type of expression being tested
	 * @param test
	 *            --- type of test
	 * @param stmt
	 *            --- stmt containing test (useful for line number info)
	 * @param bytecodes
	 *            --- list of bytecodes (to which test is appended)
	 */		
	protected void translateTypeTest(String trueTarget, Type src, Type.Union test,
			Entry stmt, ArrayList<Bytecode> bytecodes) {	
		
		// FIXME: at the moment, this approach is not very efficient!
		
		// following line is a bit of a hack to work around lack of depth subtyping.
		Type.Dictionary dict = null;
		Type.List list = null;
		Type.Set set = null;
		Type.Process proc = null;
		
		String trampoline = freshLabel();
		String falseLabel = freshLabel();	
		
		for(Type t : test.bounds()) {
			
			// the point of these tests is to avoid duplication. For example,
			// with a type [int]|[[int]] we only want to perform one instanceof
			// ArrayList.
			
			if(t instanceof Type.List) {
				Type.List l = (Type.List)t;				
				if(list == null) {
					list = l;
				} else {
					list = Type.T_LIST(Type.leastUpperBound(l.element(),list.element()));
				}
			} else if(t instanceof Type.Set) {
				Type.Set l = (Type.Set)t;				
				if(set == null) {
					set = l;
				} else {
					set = Type.T_SET(Type.leastUpperBound(l.element(),set.element()));
				}
			} else if(t instanceof Type.Dictionary) {
				Type.Dictionary l = (Type.Dictionary) t;				
				if(dict == null) {
					dict = l;
				} else {
					dict = Type.T_DICTIONARY(Type.leastUpperBound(l.key(),dict.key()),
							Type.leastUpperBound(l.value(),dict.value()));
				}
			} else {
				bytecodes.add(new Bytecode.Dup(convertType(src)));
				translateTypeTest(trampoline,src,t,stmt, bytecodes);
				src = Type.leastDifference(src,t);
			}
		}
			
		
		if(list != null) {
			bytecodes.add(new Bytecode.Dup(convertType(src)));		
			translateTypeTest(trampoline,src,list,stmt, bytecodes);
			src = Type.leastDifference(src, list);
		} 
		if(dict != null) {
			bytecodes.add(new Bytecode.Dup(convertType(src)));						
			translateTypeTest(trampoline,src,dict,stmt, bytecodes);
			src = Type.leastDifference(src, dict);
		}
		if(set != null) {
			bytecodes.add(new Bytecode.Dup(convertType(src)));			
			translateTypeTest(trampoline,src,set,stmt, bytecodes);
			src = Type.leastDifference(src, set);
		} 
		if(proc != null) {
			bytecodes.add(new Bytecode.Dup(convertType(src)));
			translateTypeTest(trampoline,src,proc,stmt, bytecodes);
			src = Type.leastDifference(src, proc);
		} 
				
		bytecodes.add(new Bytecode.Pop(convertType(src)));
		bytecodes.add(new Bytecode.Goto(falseLabel));
		bytecodes.add(new Bytecode.Label(trampoline));
		bytecodes.add(new Bytecode.Pop(convertType(src)));
		bytecodes.add(new Bytecode.Goto(trueTarget));
		bytecodes.add(new Bytecode.Label(falseLabel));
		
	}
	
	public void translate(Code.Loop c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Label(c.target + "$head"));
	}

	protected void translate(Code.End end,			
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Goto(end.label + "$head"));
		bytecodes.add(new Bytecode.Label(end.label));
	}
	
	public int translate(Code.ForAll c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {	
		Type elementType;
		
		// FIXME: following is broken because we need to use the effective type.

		if (c.type instanceof Type.Set) {
			elementType = ((Type.Set) c.type).element();
		} else {
			elementType = ((Type.List) c.type).element();
		}

		JvmType.Function ftype = new JvmType.Function(JAVA_UTIL_ITERATOR);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_COLLECTION, "iterator",
				ftype, Bytecode.INTERFACE));
		bytecodes.add(new Bytecode.Store(freeSlot, JAVA_UTIL_ITERATOR));
		bytecodes.add(new Bytecode.Label(c.target + "$head"));
		ftype = new JvmType.Function(T_BOOL);
		bytecodes.add(new Bytecode.Load(freeSlot, JAVA_UTIL_ITERATOR));
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "hasNext", ftype,
				Bytecode.INTERFACE));
		bytecodes.add(new Bytecode.If(Bytecode.If.EQ, c.target));
		bytecodes.add(new Bytecode.Load(freeSlot, JAVA_UTIL_ITERATOR));
		ftype = new JvmType.Function(JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "next", ftype,
				Bytecode.INTERFACE));
		addReadConversion(elementType, bytecodes);
		bytecodes.add(new Bytecode.Store(c.slot, JAVA_LANG_OBJECT));
		
		// we need to increase the freeSlot, since we've allocated one slot to
		// hold the register.
		
		return freeSlot + 1;
	}


	public void translate(Code.Goto c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Goto(c.target));
	}
	public void translate(Code.Label c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Label(c.label));
	}
	
	public void translate(Code.Debug c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(T_VOID,WHILEYLIST);
		bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "debug", ftype,
				Bytecode.STATIC));
	}
	
	public void translate(Code.Fail c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.New(JAVA_LANG_RUNTIMEEXCEPTION));
		bytecodes.add(new Bytecode.Dup(JAVA_LANG_RUNTIMEEXCEPTION));
		bytecodes.add(new Bytecode.LoadConst(c.msg));
		JvmType.Function ftype = new JvmType.Function(T_VOID, JAVA_LANG_STRING);
		bytecodes.add(new Bytecode.Invoke(JAVA_LANG_RUNTIMEEXCEPTION, "<init>",
				ftype, Bytecode.SPECIAL));
		bytecodes.add(new Bytecode.Throw());
	}
	public void translate(Code.ExternJvm c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.addAll(c.bytecodes);
	}
		
	public void translate(Code.Load c, int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Load(c.slot, convertType(c.type)));
	}
	
	public void translate(Code.DictLoad c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {					
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
				JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYMAP, "get", ftype,
				Bytecode.VIRTUAL));
		addReadConversion(c.type.value(),bytecodes);	
	}
	
	public void translate(Code.ListOp c, Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {						
		
		switch(c.lop) {
		case APPEND:	
		{
			JvmType.Function ftype;
			if(c.dir == OpDir.UNIFORM) {
				ftype = new JvmType.Function(WHILEYLIST,WHILEYLIST,WHILEYLIST);
			} else if(c.dir == OpDir.LEFT) {
				ftype = new JvmType.Function(WHILEYLIST,WHILEYLIST,JAVA_LANG_OBJECT);
			} else {
				ftype = new JvmType.Function(WHILEYLIST,JAVA_LANG_OBJECT,WHILEYLIST);
			}													
			bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "append", ftype,
					Bytecode.STATIC));			
			break;
		}
		case LENGTHOF:
		{
			JvmType.Function ftype = new JvmType.Function(T_INT);						
			bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "size",
					ftype, Bytecode.VIRTUAL));								
			ftype = new JvmType.Function(BIG_RATIONAL, T_INT);
			bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "valueOf",
					ftype, Bytecode.STATIC));
			break;
		}
		case SUBLIST:
		{
			JvmType.Function ftype = new JvmType.Function(WHILEYLIST, WHILEYLIST,
					BIG_RATIONAL, BIG_RATIONAL);
			bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "sublist", ftype,
					Bytecode.STATIC));
			break;
		}
		default:
			syntaxError("unknown list expression encountered",filename,stmt);
		}
	}
	
	public void translate(Code.ListLoad c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {					
		JvmType.Function ftype = new JvmType.Function(T_INT);
		bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "intValue", ftype,
				Bytecode.VIRTUAL));
		ftype = new JvmType.Function(JAVA_LANG_OBJECT,
				T_INT);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "get", ftype,
				Bytecode.VIRTUAL));
		addReadConversion(c.type.element(),bytecodes);	
	}
	
	public void translate(Code.FieldLoad c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.LoadConst(c.field));
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYRECORD,"get",ftype,Bytecode.VIRTUAL));				
		addReadConversion(c.fieldType(),bytecodes);
	}

	public void translate(Code.BinOp c, Block.Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {				
				
		JvmType type = convertType(c.type);
		JvmType.Function ftype = new JvmType.Function(type,type);
		
		switch(c.bop) {
		case ADD:			
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz)type, "add", ftype,
					Bytecode.VIRTUAL));
			break;
		case SUB:			
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz)type, "subtract", ftype,
					Bytecode.VIRTUAL));
			break;
		case MUL:			
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz)type, "multiply", ftype,
					Bytecode.VIRTUAL));
			break;
		case DIV:			
			if(c.type == Type.T_INT) {
				bytecodes.add(new Bytecode.Invoke((JvmType.Clazz)type, "intDivide", ftype,
					Bytecode.VIRTUAL));
			} else {
				bytecodes.add(new Bytecode.Invoke((JvmType.Clazz)type, "divide", ftype,
						Bytecode.VIRTUAL));
			}
			break;
		case REM:									
				bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type,
						"intRemainder", ftype, Bytecode.VIRTUAL));			
			break;
		default:
			syntaxError("unknown binary expression encountered",filename,stmt);
		}		
	}

	public void translate(Code.SetOp c, Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {		
		
		switch(c.sop) {
		case UNION:		
		case INTERSECT:
		case DIFFERENCE:
		{
			JvmType.Function ftype;
			if(c.dir == OpDir.UNIFORM) {
				ftype = new JvmType.Function(WHILEYSET,WHILEYSET,WHILEYSET);
			} else if(c.dir == OpDir.LEFT) {
				ftype = new JvmType.Function(WHILEYSET,WHILEYSET,JAVA_LANG_OBJECT);
			} else {
				ftype = new JvmType.Function(WHILEYSET,JAVA_LANG_OBJECT,WHILEYSET);
			}													
			bytecodes.add(new Bytecode.Invoke(WHILEYLIST, c.sop.toString(), ftype,
					Bytecode.STATIC));			
			break;
		}
		case LENGTHOF:			
		{
			JvmType.Function ftype = new JvmType.Function(T_INT);			
			bytecodes.add(new Bytecode.Invoke(WHILEYSET, "size",
					ftype, Bytecode.VIRTUAL));							
			ftype = new JvmType.Function(BIG_RATIONAL, T_INT);
			bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "valueOf",
					ftype, Bytecode.STATIC));
			break;
		}			
		default:
			syntaxError("unknown set operation encountered",filename,stmt);
		}
	}
	
	public void translate(Code.UnOp c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {				
				
		JvmType type = convertType(c.type);

		switch (c.uop) {
		case NEG: {			
			JvmType.Function ftype = new JvmType.Function(type);
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type, "negate",
					ftype, Bytecode.VIRTUAL));
			break;
		}			
		case PROCESSSPAWN:
		{
			bytecodes.add(new Bytecode.New(WHILEYPROCESS));			
			bytecodes.add(new Bytecode.DupX1());
			bytecodes.add(new Bytecode.DupX1());			
			bytecodes.add(new Bytecode.Swap());
			// TODO: problem here ... need to swap or something				
			JvmType.Function ftype = new JvmType.Function(T_VOID,JAVA_LANG_OBJECT);
			bytecodes.add(new Bytecode.Invoke(WHILEYPROCESS, "<init>", ftype,
					Bytecode.SPECIAL));
			ftype = new JvmType.Function(T_VOID);			
			bytecodes.add(new Bytecode.Invoke(WHILEYPROCESS, "start", ftype,
					Bytecode.VIRTUAL));
			break;
		}
		case PROCESSACCESS:
		{			
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT);		
			bytecodes.add(new Bytecode.Invoke(WHILEYPROCESS, "state", ftype,
					Bytecode.VIRTUAL));
			// finally, we need to cast the object we got back appropriately.		
			Type.Process pt = (Type.Process) c.type;						
			addReadConversion(pt.element(), bytecodes);
			break;
		}
		}		
	}
	
	protected void translate(Code.NewDict c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		construct(WHILEYMAP, freeSlot, bytecodes);
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
				JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Store(freeSlot, WHILEYMAP));
		JvmType valueT = convertType(c.type.value());

		for (int i = 0; i != c.nargs; ++i) {
			bytecodes.add(new Bytecode.Store(freeSlot + 1, valueT));
			bytecodes.add(new Bytecode.Load(freeSlot, WHILEYMAP));
			bytecodes.add(new Bytecode.Swap());
			addWriteConversion(c.type.key(), bytecodes);
			bytecodes.add(new Bytecode.Load(freeSlot + 1, valueT));
			bytecodes.add(new Bytecode.Invoke(WHILEYMAP, "put", ftype,
					Bytecode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		}

		bytecodes.add(new Bytecode.Load(freeSlot, WHILEYMAP));
	}
	
	protected void translate(Code.NewList c, int freeSlot, ArrayList<Bytecode> bytecodes) {
		construct(WHILEYLIST, freeSlot, bytecodes);		
		JvmType.Function ftype = new JvmType.Function(T_BOOL,
				JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Store(freeSlot,WHILEYLIST));
				
		for(int i=0;i!=c.nargs;++i) {
			bytecodes.add(new Bytecode.Load(freeSlot,WHILEYLIST));
			bytecodes.add(new Bytecode.Swap());			
			addWriteConversion(c.type.element(),bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYLIST,"add",ftype,Bytecode.VIRTUAL));
			// FIXME: there is a bug here for bool lists
			bytecodes.add(new Bytecode.Pop(JvmTypes.T_BOOL));
		}
		
		// At this stage, we have a problem. We've added the elements into the
		// list in reverse order. For simplicity, I simply call reverse at this
		// stage. However, it begs the question how we can do better.
		//
		// We could store each value into a register and then reload them in the
		// reverse order. For very large lists, this might cause a problem I
		// suspect.
		//
		// Another option would be to have a special list initialise function
		// with a range of different constructors for different sized lists.
		
		bytecodes.add(new Bytecode.Load(freeSlot,WHILEYLIST));	
		JvmType.Clazz owner = new JvmType.Clazz("java.util","Collections");
		ftype = new JvmType.Function(T_VOID, JAVA_UTIL_LIST);		
		bytecodes.add(new Bytecode.Invoke(owner,"reverse",ftype,Bytecode.STATIC));	
		
		bytecodes.add(new Bytecode.Load(freeSlot,WHILEYLIST));	
	}
	
	public void translate(Code.NewRecord expr, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		construct(WHILEYRECORD, freeSlot, bytecodes);		
		bytecodes.add(new Bytecode.Store(freeSlot,WHILEYRECORD));
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
				JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
		
		HashMap<String,Type> fields = expr.type.fields();
		ArrayList<String> keys = new ArrayList<String>(fields.keySet());
		Collections.sort(keys);
		Collections.reverse(keys);
		for(String key : keys) {
			Type et = fields.get(key);				
			bytecodes.add(new Bytecode.Load(freeSlot,WHILEYRECORD));
			bytecodes.add(new Bytecode.Swap());
			bytecodes.add(new Bytecode.LoadConst(key));
			bytecodes.add(new Bytecode.Swap());
			addWriteConversion(et,bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYRECORD,"put",ftype,Bytecode.VIRTUAL));						
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		}
		
		bytecodes.add(new Bytecode.Load(freeSlot,WHILEYRECORD));
	}
	
	protected void translate(Code.NewSet c, int freeSlot, ArrayList<Bytecode> bytecodes) {
		construct(WHILEYSET, freeSlot, bytecodes);		
		JvmType.Function ftype = new JvmType.Function(T_BOOL,
				JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Store(freeSlot,WHILEYSET));		
		
		for(int i=0;i!=c.nargs;++i) {
			bytecodes.add(new Bytecode.Load(freeSlot,WHILEYSET));
			bytecodes.add(new Bytecode.Swap());			
			addWriteConversion(c.type.element(),bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYSET,"add",ftype,Bytecode.VIRTUAL));
			// FIXME: there is a bug here for bool lists
			bytecodes.add(new Bytecode.Pop(JvmTypes.T_BOOL));
		}
		
		bytecodes.add(new Bytecode.Load(freeSlot,WHILEYSET));
	}
	
	public void translate(Code.Invoke c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		ModuleID mid = c.name.module();
		String mangled = nameMangle(c.name.name(), c.type);
		JvmType.Clazz owner = new JvmType.Clazz(mid.pkg().toString(),
				mid.module());
		JvmType.Function type = convertFunType(c.type);
		bytecodes
				.add(new Bytecode.Invoke(owner, mangled, type, Bytecode.STATIC));

		// now, handle the case of an invoke which returns a value that should
		// be discarded. 
		if(!c.retval && c.type.ret() != Type.T_VOID) {
			bytecodes.add(new Bytecode.Pop(convertType(c.type.ret())));
		}
	}
	
	public void translate(Code.IndirectInvoke c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {				
		
		// The main issue here, is that we have all of the parameters on the
		// stack. What we need to do is to put them into an array, so they can
		// then be passed into Method.invoke()
		//
		// To make this work, what we'll do is use a temporary register to hold
		// the array as we build it up.

		Type.Fun ft = (Type.Fun) c.type;		
		JvmType.Array arrT = new JvmType.Array(JAVA_LANG_OBJECT);		

		bytecodes.add(new Bytecode.LoadConst(ft.params().size()));
		bytecodes.add(new Bytecode.New(arrT));
		bytecodes.add(new Bytecode.Store(freeSlot,arrT));
		
		List<Type> params = ft.params();
		for(int i=params.size()-1;i>=0;--i) {
			Type pt = params.get(i);
			bytecodes.add(new Bytecode.Load(freeSlot,arrT));
			bytecodes.add(new Bytecode.Swap());
			bytecodes.add(new Bytecode.LoadConst(i));
			bytecodes.add(new Bytecode.Swap());
			addWriteConversion(pt,bytecodes);
			bytecodes.add(new Bytecode.ArrayStore(arrT));			
		}

		bytecodes.add(new Bytecode.LoadConst(null));
		bytecodes.add(new Bytecode.Load(freeSlot,arrT));
		JvmType.Clazz owner = new JvmType.Clazz("java.lang.reflect","Method");		
		JvmType.Function type = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT,arrT);		
		
		bytecodes.add(new Bytecode.Invoke(owner, "invoke", type,
				Bytecode.VIRTUAL));						
		addReadConversion(ft.ret(),bytecodes);	
	}

	public void translate(Code.Send c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		
		// The main issue here, is that we have all of the parameters + receiver
		// on the stack. What we need to do is to put them into an array, so
		// they can then be passed into Method.invoke()
		//
		// To make this work, what we'll do is use a temporary register to hold
		// the array as we build it up.

		Type.Fun ft = (Type.Fun) c.type;		
		JvmType.Array arrT = new JvmType.Array(JAVA_LANG_OBJECT);		
		bytecodes.add(new Bytecode.LoadConst(ft.params().size()+1));
		bytecodes.add(new Bytecode.New(arrT));
		bytecodes.add(new Bytecode.Store(freeSlot,arrT));
		
		// first, peal parameters off stack in reverse order
		
		List<Type> params = ft.params();
		for(int i=params.size()-1;i>=0;--i) {
			Type pt = params.get(i);
			bytecodes.add(new Bytecode.Load(freeSlot,arrT));
			bytecodes.add(new Bytecode.Swap());
			bytecodes.add(new Bytecode.LoadConst(i+1));
			bytecodes.add(new Bytecode.Swap());			
			addWriteConversion(pt,bytecodes);
			bytecodes.add(new Bytecode.ArrayStore(arrT));			
		}
		
		// finally, setup the stack for the send
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_REFLECT_METHOD,
				JAVA_LANG_STRING, JAVA_LANG_STRING);
		
		bytecodes.add(new Bytecode.LoadConst(c.name.module().toString()));		
		bytecodes
				.add(new Bytecode.LoadConst(nameMangle(c.name.name(), c.type)));
		bytecodes.add(new Bytecode.Invoke(WHILEYIO, "functionRef", ftype,
				Bytecode.STATIC));
		bytecodes.add(new Bytecode.Load(freeSlot, arrT));
							
		if (c.synchronous && c.retval) {			
			ftype = new JvmType.Function(JAVA_LANG_OBJECT,
					JAVA_LANG_REFLECT_METHOD, JAVA_LANG_OBJECT_ARRAY);
			bytecodes.add(new Bytecode.Invoke(WHILEYPROCESS, "syncSend", ftype,
					Bytecode.VIRTUAL));
			addReadConversion(c.type.ret(), bytecodes);
		} else if (c.synchronous) {			
			ftype = new JvmType.Function(T_VOID,
					JAVA_LANG_REFLECT_METHOD, JAVA_LANG_OBJECT_ARRAY);
			bytecodes.add(new Bytecode.Invoke(WHILEYPROCESS, "vSyncSend", ftype,
					Bytecode.VIRTUAL));
		} else {
			ftype = new JvmType.Function(T_VOID,
					JAVA_LANG_REFLECT_METHOD, JAVA_LANG_OBJECT_ARRAY);
			bytecodes.add(new Bytecode.Invoke(WHILEYPROCESS, "asyncSend",
					ftype, Bytecode.VIRTUAL));
		} 
	}
	
	public void translate(Code.IndirectSend c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
	
	}
		
	public void translate(Value v, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		if(v instanceof Value.Null) {
			translate((Value.Null)v,freeSlot,bytecodes);
		} else if(v instanceof Value.Bool) {
			translate((Value.Bool)v,freeSlot,bytecodes);
		} else if(v instanceof Value.Number) {
			translate((Value.Number)v,freeSlot,bytecodes);
		} else if(v instanceof Value.Set) {
			translate((Value.Set)v,freeSlot,bytecodes);
		} else if(v instanceof Value.List) {
			translate((Value.List)v,freeSlot,bytecodes);
		} else if(v instanceof Value.Record) {
			translate((Value.Record)v,freeSlot,bytecodes);
		} else if(v instanceof Value.Dictionary) {
			translate((Value.Dictionary)v,freeSlot,bytecodes);
		} else if(v instanceof Value.FunConst) {
			translate((Value.FunConst)v,freeSlot,bytecodes);
		} else {
			throw new IllegalArgumentException("unknown value encountered:" + v);
		}
	}
	
	protected void translate(Value.Null e, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.LoadConst(null));
	}
	
	protected void translate(Value.Bool e, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		if (e.value) {
			bytecodes.add(new Bytecode.LoadConst(1));
		} else {
			bytecodes.add(new Bytecode.LoadConst(0));
		}
	}

	protected void translate(Value.Number e, int freeSlot,
			ArrayList<Bytecode> bytecodes) {		
		BigRational rat = e.value;
		BigInteger den = rat.denominator();
		BigInteger num = rat.numerator();
		if(rat.isInteger()) {
			// this 
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
	
	protected void translate(Value.Set lv, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		construct(WHILEYSET, freeSlot, bytecodes);		
		JvmType.Function ftype = new JvmType.Function(T_BOOL,
				JAVA_LANG_OBJECT);  
		for(Value e : lv.values) {
			// FIXME: there is a bug here for bool sets
			bytecodes.add(new Bytecode.Dup(WHILEYSET));
			translate(e, freeSlot, bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYSET,"add",ftype,Bytecode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(JvmTypes.T_BOOL));
		}
	}

	protected void translate(Value.List lv, int freeSlot,
			ArrayList<Bytecode> bytecodes) {		
		bytecodes.add(new Bytecode.New(WHILEYLIST));		
		bytecodes.add(new Bytecode.Dup(WHILEYLIST));
		bytecodes.add(new Bytecode.LoadConst(lv.values.size()));
		JvmType.Function ftype = new JvmType.Function(T_VOID,T_INT);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "<init>", ftype,
				Bytecode.SPECIAL));
		
		ftype = new JvmType.Function(WHILEYLIST, WHILEYLIST, WHILEYANY);		
		for (Value e : lv.values) {			
			translate(e, freeSlot, bytecodes);
			addWriteConversion(e.type(), bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "append_l", ftype,
					Bytecode.STATIC));			
		}		
	}

	protected void translate(Value.Record expr, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
				JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
		construct(WHILEYRECORD, freeSlot, bytecodes);
		for (Map.Entry<String, Value> e : expr.values.entrySet()) {
			Type et = e.getValue().type();
			bytecodes.add(new Bytecode.Dup(WHILEYRECORD));
			bytecodes.add(new Bytecode.LoadConst(e.getKey()));
			translate(e.getValue(), freeSlot, bytecodes);
			addWriteConversion(et, bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYRECORD, "put", ftype,
					Bytecode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(WHILEYRECORD));
		}
	}
	
	protected void translate(Value.Dictionary expr, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
				JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
		
		construct(WHILEYMAP, freeSlot, bytecodes);
		
		for (Map.Entry<Value, Value> e : expr.values.entrySet()) {
			Type kt = e.getKey().type();
			Type vt = e.getValue().type();
			bytecodes.add(new Bytecode.Dup(WHILEYMAP));			
			translate(e.getKey(), freeSlot, bytecodes);
			addWriteConversion(kt, bytecodes);
			translate(e.getValue(), freeSlot, bytecodes);
			addWriteConversion(vt, bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYMAP, "put", ftype,
					Bytecode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(WHILEYMAP));
		}
	}
	
	protected void translate(Value.FunConst e, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_REFLECT_METHOD,JAVA_LANG_STRING,JAVA_LANG_STRING);
		NameID nid = e.name;		
		bytecodes.add(new Bytecode.LoadConst(nid.module().toString()));
		bytecodes.add(new Bytecode.LoadConst(nameMangle(nid.name(),e.type)));
		bytecodes.add(new Bytecode.Invoke(WHILEYIO, "functionRef", ftype,Bytecode.STATIC));
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
			addCheckCast(convertType(et),bytecodes);			
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

	public void addCheckCast(JvmType type, ArrayList<Bytecode> bytecodes) {
		// The following can happen in situations where a variable has type
		// void. In principle, we could remove this as obvious dead-code, but
		// for now I just avoid it.
		if(type instanceof JvmType.Void) {
			return;
		} else if(!type.equals(JAVA_LANG_OBJECT)) {
			// pointless to add a cast for object
			bytecodes.add(new Bytecode.CheckCast(type));
		}
	}
	
	/**
	 * The construct method provides a generic way to construct a Java object.
	 * 
	 * @param owner
	 * @param freeSlot
	 * @param bytecodes
	 * @param params
	 */
	public void construct(JvmType.Clazz owner, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.New(owner));		
		bytecodes.add(new Bytecode.Dup(owner));
		ArrayList<JvmType> paramTypes = new ArrayList<JvmType>();		
		JvmType.Function ftype = new JvmType.Function(T_VOID,paramTypes);
		bytecodes.add(new Bytecode.Invoke(owner, "<init>", ftype,
				Bytecode.SPECIAL));
	}		 
	
		
	public final static Type.Process WHILEY_SYSTEM_OUT_T = (Type.Process) Type
			.minimise(Type.T_PROCESS(Type.T_EXISTENTIAL(new NameID(
					new ModuleID(new PkgID("whiley", "lang"), "System"), "1"))));

	public final static Type.Process WHILEY_SYSTEM_T = (Type.Process) Type
			.minimise(Type.T_PROCESS(Type.T_RECORD(new HashMap() {
				{
					put("out", WHILEY_SYSTEM_OUT_T);
					put("rest", Type.T_EXISTENTIAL(new NameID(new ModuleID(
							new PkgID("whiley", "lang"), "System"), "1")));
				}
			})));
	
	public final static JvmType.Clazz WHILEYANY = new JvmType.Clazz("wyjc.runtime","Any");
	public final static JvmType.Clazz WHILEYUTIL = new JvmType.Clazz("wyjc.runtime","Util");
	public final static JvmType.Clazz WHILEYLIST = new JvmType.Clazz("wyjc.runtime","List");
	public final static JvmType.Clazz WHILEYSET = new JvmType.Clazz("wyjc.runtime","Set");
	public final static JvmType.Clazz WHILEYIO = new JvmType.Clazz("wyjc.runtime","IO");
	public final static JvmType.Clazz WHILEYMAP = new JvmType.Clazz("wyjc.runtime","Dictionary");
	public final static JvmType.Clazz WHILEYRECORD = new JvmType.Clazz("wyjc.runtime","Record");	
	public final static JvmType.Clazz WHILEYPROCESS = new JvmType.Clazz(
			"wyjc.runtime", "Actor");	
	public final static JvmType.Clazz WHILEYEXCEPTION = new JvmType.Clazz("wyjc.runtime","Exception");	
	public final static JvmType.Clazz BIG_RATIONAL = new JvmType.Clazz("wyjc.runtime","BigRational");
	private static final JvmType.Clazz JAVA_LANG_SYSTEM = new JvmType.Clazz("java.lang","System");
	private static final JvmType.Array JAVA_LANG_OBJECT_ARRAY = new JvmType.Array(JAVA_LANG_OBJECT);
	private static final JvmType.Clazz JAVA_UTIL_LIST = new JvmType.Clazz("java.util","List");
	private static final JvmType.Clazz JAVA_LANG_REFLECT_METHOD = new JvmType.Clazz("java.lang.reflect","Method");
	private static final JvmType.Clazz JAVA_IO_PRINTSTREAM = new JvmType.Clazz("java.io","PrintStream");
	private static final JvmType.Clazz JAVA_LANG_RUNTIMEEXCEPTION = new JvmType.Clazz("java.lang","RuntimeException");
	private static final JvmType.Clazz JAVA_LANG_ASSERTIONERROR = new JvmType.Clazz("java.lang","AssertionError");
	private static final JvmType.Clazz JAVA_UTIL_COLLECTION = new JvmType.Clazz("java.util","Collection");	
	
	public JvmType.Function convertFunType(Type.Fun t) {		
		Type.Fun ft = (Type.Fun) t; 
		ArrayList<JvmType> paramTypes = new ArrayList<JvmType>();
		if(ft.receiver() != null) {
			paramTypes.add(convertType(ft.receiver()));
		}
		for(Type pt : ft.params()) {
			paramTypes.add(convertType(pt));
		}
		JvmType rt = convertType(ft.ret());			
		return new JvmType.Function(rt,paramTypes);		
	}
	
	public JvmType convertType(Type t) {
		if(t == Type.T_VOID) {
			return T_VOID;
		} else if(t == Type.T_ANY) {
			return JAVA_LANG_OBJECT;
		} else if(t == Type.T_NULL) {
			return JAVA_LANG_OBJECT;
		} else if(t instanceof Type.Bool) {
			return T_BOOL;
		} else if(t instanceof Type.Int) {
			return BIG_RATIONAL;
		} else if(t instanceof Type.Real) {
			return BIG_RATIONAL;
		} else if(t instanceof Type.List) {
			return WHILEYLIST;
		} else if(t instanceof Type.Set) {
			return WHILEYSET;
		} else if(t instanceof Type.Dictionary) {
			return WHILEYMAP;
		} else if(t instanceof Type.Record) {
			return WHILEYRECORD;
		} else if(t instanceof Type.Process) {
			return WHILEYPROCESS;
		} else if(t instanceof Type.Union) {
			// There's an interesting question as to whether we need to do more
			// here. For example, a union of a set and a list could result in
			// contains ?
			Type.Record tt = Type.effectiveRecordType(t);
			if(tt != null) {
				return WHILEYRECORD;
			} else {
				return JAVA_LANG_OBJECT;
			}
		} else if(t instanceof Type.Meta) {							
			return JAVA_LANG_OBJECT;			
		} else if(t instanceof Type.Fun) {						
			return JAVA_LANG_REFLECT_METHOD;
		}else {
			throw new RuntimeException("unknown type encountered: " + t);
		}		
	}	
			
	protected int label = 0;
	protected String freshLabel() {
		return "cfblab" + label++;
	}	
	
	public static String nameMangle(String name, Type.Fun ft) {				
		try {			
			return name + "$" + typeMangle(ft);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
		
	public static String typeMangle(Type.Fun ft) throws IOException {		
		JavaIdentifierOutputStream jout = new JavaIdentifierOutputStream();
		BinaryOutputStream binout = new BinaryOutputStream(jout);		
		Types.BinaryWriter tm = new Types.BinaryWriter(binout);
		Type.build(tm,ft);		
		binout.close(); // force flush		
		//testMangle1(ft);
		return jout.toString();		
	}	
	/*			
	public static void testMangle1(Type.Fun ft) throws IOException {
		IdentifierOutputStream jout = new IdentifierOutputStream();
		BinaryOutputStream binout = new BinaryOutputStream(jout);
		Types.BinaryWriter tm = new Types.BinaryWriter(binout);		
		Type.build(tm,ft);
		binout.close();		
		System.out.println("MANGLED: " + ft + " => " + jout.toString());
		Type.Fun type = (Type.Fun) new Types.BinaryReader(
				new BinaryInputStream(new IdentifierInputStream(
						jout.toString()))).read();
		System.out.println("UNMANGLED TO: " + type);
		if(!type.equals(ft)) {
			throw new RuntimeException("INVALID TYPE RECONSTRUCTED");
		}
	}	
	*/
}
