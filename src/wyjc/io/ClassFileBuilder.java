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

import wyjc.Main;
import wyjc.attributes.WhileyDefine;
import wyjc.attributes.WhileyVersion;
import wybs.lang.Path;
import wybs.lang.SyntaxError;
import wyil.*;
import static wybs.lang.SyntaxError.*;
import wyil.util.*;
import wyil.lang.*;
import wyil.lang.Code.*;
import static wyil.lang.Block.*;
import wyjc.runtime.BigRational;
import wyjvm.attributes.Code.Handler;
import wyjvm.attributes.LineNumberTable;
import wyjvm.attributes.SourceFile;
import wyjvm.io.BinaryOutputStream;
import wyjvm.lang.*;
import wyjvm.lang.Modifier;
import static wyjvm.lang.JvmTypes.*;

/**
 * The purpose of the class file builder is to construct a jvm class file from a
 * given WhileyFile.
 * 
 * @author David J. Pearce
 */
public class ClassFileBuilder {
	protected int CLASS_VERSION = 49;
	protected int WHILEY_MINOR_VERSION;
	protected int WHILEY_MAJOR_VERSION;
	protected String filename;
	protected JvmType.Clazz owner;
	
	public ClassFileBuilder(int whileyMajorVersion, int whileyMinorVersion) {
		this.WHILEY_MINOR_VERSION = whileyMinorVersion;
		this.WHILEY_MAJOR_VERSION = whileyMajorVersion;
	}

	public ClassFile build(WyilFile module) {		
		owner = new JvmType.Clazz(module.id().parent().toString().replace('.','/'),
				module.id().last());
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);
		modifiers.add(Modifier.ACC_FINAL);
		ClassFile cf = new ClassFile(49, owner, JAVA_LANG_OBJECT,
				new ArrayList<JvmType.Clazz>(), modifiers);
	
		this.filename = module.filename();
		
		if(filename != null) {
			cf.attributes().add(new SourceFile(filename));
		}
		
		boolean addMainLauncher = false;		
				
		for(WyilFile.ConstDef cd : module.constants()) {	
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
		
		for(WyilFile.TypeDef td : module.types()) {
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
		
		HashMap<Constant,Integer> constants = new HashMap<Constant,Integer>();
		for(WyilFile.Method method : module.methods()) {				
			if(method.name().equals("main")) { 
				addMainLauncher = true;
			}			
			cf.methods().addAll(build(method, constants));			
		}		
			
		buildConstants(constants,cf);		
				
		if(addMainLauncher) {
			cf.methods().add(buildMainLauncher(owner));
		}
		
		cf.attributes().add(
				new WhileyVersion(WHILEY_MAJOR_VERSION, WHILEY_MINOR_VERSION));
		
		return cf;
	}	
	
	public void buildConstants(HashMap<Constant,Integer> constants, ClassFile cf) {						
		buildCoercions(constants,cf);
		buildValues(constants,cf);
	}
	
	public void buildCoercions(HashMap<Constant,Integer> constants, ClassFile cf) {
		HashSet<Constant> done = new HashSet<Constant>();
		HashMap<Constant,Integer> original = constants;
		// this could be a little more efficient I think!!		
		while(done.size() != constants.size()) {
			// We have to clone the constants map, since it may be expanded as a
			// result of buildCoercion(). This will occur if the coercion
			// constructed requires a helper coercion that was not in the
			// original constants map.  
			HashMap<Constant,Integer> nconstants = new HashMap<Constant,Integer>(constants);		
			for(Map.Entry<Constant,Integer> entry : constants.entrySet()) {
				Constant e = entry.getKey();
				if(!done.contains(e) && e instanceof Coercion) {
					Coercion c = (Coercion) e;
					buildCoercion(c.from,c.to,entry.getValue(),nconstants,cf);
				} 
				done.add(e);
			}
			constants = nconstants;
		}
		original.putAll(constants);
	}
	
	public void buildValues(HashMap<Constant,Integer> constants, ClassFile cf) {
		int nvalues = 0;
		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
		
		for(Map.Entry<Constant,Integer> entry : constants.entrySet()) {			
			Constant c = entry.getKey();
			if(c instanceof ValueConst) {
				nvalues++;
				Value constant = ((ValueConst)c).value;
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
		}
		
		if(nvalues > 0) {
			// create static initialiser method, but only if we really need to.
			bytecodes.add(new Bytecode.Return(null));

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
	}
		
	public ClassFile.Method buildMainLauncher(JvmType.Clazz owner) {
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);
		modifiers.add(Modifier.ACC_STATIC);
		modifiers.add(Modifier.ACC_SYNTHETIC);
		
		// Make the initial system process.
		JvmType.Function ft1 =
		    new JvmType.Function(T_VOID, new JvmType.Array(JAVA_LANG_STRING));
		ClassFile.Method cm = new ClassFile.Method("main", ft1, modifiers);
		JvmType.Array strArr = new JvmType.Array(JAVA_LANG_STRING);
		ArrayList<Bytecode> codes = new ArrayList<Bytecode>();
	
		// Create the scheduler that will handle concurrency.
		codes.add(new Bytecode.New(WHILEYSCHEDULER));
		
		// Store the scheduler for later.
		codes.add(new Bytecode.Dup(WHILEYSCHEDULER));
		codes.add(new Bytecode.Store(1, WHILEYSCHEDULER));
		
		// Use the special constructor if a fixed number of threads is called for.
		JvmType.Function ctype;
		if (Main.threadCount > -1) {
			codes.add(new Bytecode.LoadConst(Main.threadCount));
			ctype = new JvmType.Function(T_VOID, T_INT);
		} else {
			ctype = new JvmType.Function(T_VOID);
		}
		codes.add(new Bytecode.Invoke(WHILEYSCHEDULER, "<init>", ctype,
				Bytecode.SPECIAL));
		
		// Create the starting strand.
		codes.add(new Bytecode.New(WHILEYSTRAND));
		
		// Create a copy for the call to sendSync.
		codes.add(new Bytecode.Dup(WHILEYSTRAND));
		
		// Call the strand's constructor.
		codes.add(new Bytecode.Load(1, WHILEYSCHEDULER));
		codes.add(new Bytecode.Invoke(WHILEYSTRAND, "<init>",
				new JvmType.Function(T_VOID, WHILEYSCHEDULER), Bytecode.SPECIAL));
		
		// Get the ::main method out.
		Type.Method wyft = Type.Method(Type.T_VOID, Type.T_VOID, WHILEY_SYSTEM_T);
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_REFLECT_METHOD,
				JAVA_LANG_STRING, JAVA_LANG_STRING);
		
		codes.add(new Bytecode.LoadConst(owner.toString()));
		codes.add(new Bytecode.LoadConst(nameMangle("main", wyft)));
		codes.add(new Bytecode.Invoke(WHILEYUTIL, "functionRef", ftype,
				Bytecode.STATIC));
		
		// Create the ::main arguments list.
		codes.add(new Bytecode.LoadConst(1));
		codes.add(new Bytecode.New(JAVA_LANG_OBJECT_ARRAY));
		codes.add(new Bytecode.Dup(JAVA_LANG_OBJECT_ARRAY));
		codes.add(new Bytecode.LoadConst(0));
		
		// Create the console record.
		codes.add(new Bytecode.Load(0, strArr));
		codes.add(new Bytecode.Load(1, WHILEYSCHEDULER));
		codes.add(new Bytecode.Invoke(WHILEYUTIL, "newSystemConsole",
				new JvmType.Function(WHILEYRECORD, new JvmType.Array(JAVA_LANG_STRING),
						WHILEYSCHEDULER), Bytecode.STATIC));
		
		// Add the console to the arguments list.
		codes.add(new Bytecode.ArrayStore(JAVA_LANG_OBJECT_ARRAY));
		
		// Call the send method (this does not block).
		codes.add(new Bytecode.Invoke(WHILEYSTRAND, "sendAsync",
				new JvmType.Function(T_VOID, JAVA_LANG_REFLECT_METHOD,
						JAVA_LANG_OBJECT_ARRAY), Bytecode.VIRTUAL));
		
		// And return.
		codes.add(new Bytecode.Return(null));

		wyjvm.attributes.Code code =
		    new wyjvm.attributes.Code(codes, new ArrayList<Handler>(), cm);
		cm.attributes().add(code);

		return cm;
	}
	
	public List<ClassFile.Method> build(WyilFile.Method method,
			HashMap<Constant, Integer> constants) {
		ArrayList<ClassFile.Method> methods = new ArrayList<ClassFile.Method>();
		int num = 1;
		for(WyilFile.Case c : method.cases()) {
			if(method.isNative()) {
				methods.add(buildNativeOrExport(c,method,constants));
			} else {
				if(method.isExport()) {
					methods.add(buildNativeOrExport(c,method,constants));
				}
				methods.add(build(num++,c,method,constants));
			}
		}
		return methods;
	}
	
	public ClassFile.Method build(int caseNum, WyilFile.Case mcase,
			WyilFile.Method method, HashMap<Constant,Integer> constants) {		
		
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
			
		ArrayList<Handler> handlers = new ArrayList<Handler>();
		ArrayList<LineNumberTable.Entry> lineNumbers = new ArrayList<LineNumberTable.Entry>();		
		ArrayList<Bytecode> codes;				
		codes = translate(mcase,constants,handlers,lineNumbers);		
		wyjvm.attributes.Code code = new wyjvm.attributes.Code(codes,handlers,cm);
		if(!lineNumbers.isEmpty()) {
			code.attributes().add(new LineNumberTable(lineNumbers));
		}						
		cm.attributes().add(code);
		
		return cm;
	}
	
	public ClassFile.Method buildNativeOrExport(WyilFile.Case mcase,
			WyilFile.Method method, HashMap<Constant,Integer> constants) {
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		if(method.isPublic()) {
			modifiers.add(Modifier.ACC_PUBLIC);
		}
		modifiers.add(Modifier.ACC_STATIC);					
		JvmType.Function ft = convertFunType(method.type());		
		
		String name = method.name();
		if(method.isNative()) {
			name = nameMangle(method.name(),method.type());
		}
				
		ClassFile.Method cm = new ClassFile.Method(name,ft,modifiers);		
		for(Attribute a : mcase.attributes()) {
			if(a instanceof BytecodeAttribute) {
				// FIXME: this is a hack
				cm.attributes().add((BytecodeAttribute)a);
			}
		}
			
		ArrayList<Handler> handlers = new ArrayList<Handler>();			
		ArrayList<Bytecode> codes;				
		codes = translateNativeOrExport(method);		
		wyjvm.attributes.Code code = new wyjvm.attributes.Code(codes,handlers,cm);
							
		cm.attributes().add(code);
		
		return cm;
	}
	
	public ArrayList<Bytecode> translateNativeOrExport(WyilFile.Method method) {

		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
		Type.FunctionOrMethodOrMessage ft = method.type();
		int slot = 0;
		// first, check to see if need to load receiver
		if (ft instanceof Type.Message) {
			Type.Message mt = (Type.Message) ft;
			if (mt.receiver() != null) {
				bytecodes.add(new Bytecode.Load(slot++, convertType(mt
						.receiver())));
			}
		}

		for (Type param : ft.params()) {
			bytecodes.add(new Bytecode.Load(slot++, convertType(param)));
		}

		if (method.isNative()) {
			JvmType.Clazz redirect = new JvmType.Clazz(owner.pkg(), owner
					.components().get(0).first(), "native");
			bytecodes.add(new Bytecode.Invoke(redirect, method.name(),
					convertFunType(ft), Bytecode.STATIC));
		} else {
			JvmType.Clazz redirect = new JvmType.Clazz(owner.pkg(), owner
					.components().get(0).first());
			bytecodes.add(new Bytecode.Invoke(redirect, nameMangle(
					method.name(), method.type()), convertFunType(ft),
					Bytecode.STATIC));
		}

		if (ft.ret() == Type.T_VOID) {
			bytecodes.add(new Bytecode.Return(null));
		} else {
			bytecodes.add(new Bytecode.Return(convertType(ft.ret())));
		}

		return bytecodes;
	}
	
	public ArrayList<Bytecode> translate(WyilFile.Case mcase,
			HashMap<Constant, Integer> constants, ArrayList<Handler> handlers,
			ArrayList<LineNumberTable.Entry> lineNumbers) {
		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
		translate(mcase.body(), mcase.body().numSlots(), constants, handlers,
				lineNumbers, bytecodes);
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
	public void translate(Block blk, int freeSlot,
			HashMap<Constant, Integer> constants, 
			ArrayList<Handler> handlers,
			ArrayList<LineNumberTable.Entry> lineNumbers,
			ArrayList<Bytecode> bytecodes) {
		
		ArrayList<UnresolvedHandler> unresolvedHandlers = new ArrayList<UnresolvedHandler>();
		for (Entry s : blk) {
			Attribute.Source loc = s.attribute(Attribute.Source.class);
			if(loc != null) {				
				lineNumbers.add(new LineNumberTable.Entry(bytecodes.size(),loc.line));
			}
			freeSlot = translate(s, freeSlot, constants, unresolvedHandlers,
					bytecodes);
		}
		
		if (unresolvedHandlers.size() > 0) {
			HashMap<String, Integer> labels = new HashMap<String, Integer>();

			for (int i = 0; i != bytecodes.size(); ++i) {
				Bytecode b = bytecodes.get(i);
				if (b instanceof Bytecode.Label) {
					Bytecode.Label lab = (Bytecode.Label) b;
					labels.put(lab.name, i);
				}
			}

			for (UnresolvedHandler ur : unresolvedHandlers) {
				int start = labels.get(ur.start);
				int end = labels.get(ur.end);
				Handler handler = new Handler(start, end, ur.target,
						ur.exception);
				handlers.add(handler);
			}
		}

		// here, we need to resolve the handlers.
	}
	
	public int translate(Entry entry, int freeSlot,
			HashMap<Constant, Integer> constants,
			ArrayList<UnresolvedHandler> handlers, ArrayList<Bytecode> bytecodes) {
		try {
			Code code = entry.code;
			if(code instanceof Assert) {
				 // translate((Assert)code,freeSlot,bytecodes);
			} else if(code instanceof BinOp) {
				 translate((BinOp)code,entry,freeSlot,bytecodes);
			} else if(code instanceof Convert) {
				 translate((Convert)code,freeSlot,constants,bytecodes);
			} else if(code instanceof Const) {
				translate((Const) code, freeSlot, constants, bytecodes);
			} else if(code instanceof Debug) {
				 translate((Debug)code,freeSlot,bytecodes);
			} else if(code instanceof Destructure) {
				 translate((Destructure)code,freeSlot,bytecodes);
			} else if(code instanceof LoopEnd) {
				 translate((LoopEnd)code,freeSlot,bytecodes);
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
				translate((IfType) code, entry, freeSlot, constants, bytecodes);
			} else if(code instanceof IndirectInvoke) {
				 translate((IndirectInvoke)code,freeSlot,bytecodes);
			} else if(code instanceof IndirectSend) {
				 translate((IndirectSend)code,freeSlot,bytecodes);
			} else if(code instanceof Invoke) {
				 translate((Invoke)code,freeSlot,bytecodes);
			} else if(code instanceof Invert) {
				 translate((Invert)code,freeSlot,bytecodes);
			} else if(code instanceof Label) {
				translate((Label)code,freeSlot,bytecodes);
			} else if(code instanceof ListAppend) {
				 translate((ListAppend)code,entry,freeSlot,bytecodes);
			} else if(code instanceof LengthOf) {
				 translate((LengthOf)code,entry,freeSlot,bytecodes);
			} else if(code instanceof SubList) {
				 translate((SubList)code,entry,freeSlot,bytecodes);
			} else if(code instanceof IndexOf) {
				 translate((IndexOf)code,freeSlot,bytecodes);
			} else if(code instanceof Load) {
				 translate((Load)code,freeSlot,bytecodes);
			} else if(code instanceof Loop) {
				 translate((Loop)code,freeSlot,bytecodes);
			} else if(code instanceof Move) {
				 translate((Move)code,freeSlot,bytecodes);
			} else if(code instanceof Update) {
				 translate((Update)code,freeSlot,bytecodes);
			} else if(code instanceof NewDict) {
				 translate((NewDict)code,freeSlot,bytecodes);
			} else if(code instanceof NewList) {
				 translate((NewList)code,freeSlot,bytecodes);
			} else if(code instanceof NewRecord) {
				 translate((NewRecord)code,freeSlot,bytecodes);
			} else if(code instanceof NewSet) {
				 translate((NewSet)code,freeSlot,bytecodes);
			} else if(code instanceof NewTuple) {
				 translate((NewTuple)code,freeSlot,bytecodes);
			} else if(code instanceof Negate) {
				 translate((Negate)code,freeSlot,bytecodes);
			} else if(code instanceof Dereference) {
				 translate((Dereference)code,freeSlot,bytecodes);
			} else if(code instanceof Return) {
				 translate((Return)code,freeSlot,bytecodes);
			} else if(code instanceof Skip) {
				// do nothing
			} else if(code instanceof Send) {
				 translate((Send)code,freeSlot,bytecodes);
			} else if(code instanceof SetUnion) {
				 translate((SetUnion)code,entry,freeSlot,bytecodes);
			} else if(code instanceof SetIntersect) {
				 translate((SetIntersect)code,entry,freeSlot,bytecodes);
			} else if(code instanceof SetDifference) {
				 translate((SetDifference)code,entry,freeSlot,bytecodes);
			} else if(code instanceof StringAppend) {
				 translate((StringAppend)code,entry,freeSlot,bytecodes);
			} else if(code instanceof SubString) {
				 translate((SubString)code,entry,freeSlot,bytecodes);
			} else if(code instanceof Store) {
				 translate((Store)code,freeSlot,bytecodes);
			} else if(code instanceof Switch) {
				 translate((Switch)code,entry,freeSlot,bytecodes);
			} else if(code instanceof TryCatch) {
				 translate((TryCatch)code,entry,freeSlot,handlers,constants,bytecodes);
			} else if(code instanceof New) {
				 translate((New)code,freeSlot,bytecodes);
			} else if(code instanceof Throw) {
				 translate((Throw)code,freeSlot,bytecodes);
			} else if(code instanceof TupleLoad) {
				 translate((TupleLoad)code,freeSlot,bytecodes);
			} else {
				internalFailure("unknown wyil code encountered (" + code + ")", filename, entry);
			}
			
		} catch (SyntaxError ex) {
			throw ex;
		} catch (Exception ex) {		
			internalFailure(ex.getMessage(), filename, entry, ex);
		}
		
		return freeSlot;
	}
	
	public void translate(Code.Const c, int freeSlot,
			HashMap<Constant,Integer> constants,
			ArrayList<Bytecode> bytecodes) {
		
		Value constant = c.constant;
		if (constant instanceof Value.Rational || constant instanceof Value.Bool
				|| constant instanceof Value.Null || constant instanceof Value.Byte) {
			translate(constant,freeSlot,bytecodes);					
		} else {
			int id = ValueConst.get(constant,constants);			
			String name = "constant$" + id;
			JvmType type = convertType(constant.type());
			bytecodes.add(new Bytecode.GetField(owner, name, type, Bytecode.STATIC));
			// the following is necessary to prevent in-place updates of our
			// constants!
			addIncRefs(constant.type(),bytecodes);
		}		
	}
	
	public void translate(Code.Convert c, int freeSlot,
			HashMap<Constant, Integer> constants, ArrayList<Bytecode> bytecodes) {		
		addCoercion(c.from,c.to,freeSlot,constants,bytecodes);		
	}
	
	
	public void translate(Code.Store c, int freeSlot,			
			ArrayList<Bytecode> bytecodes) {		
		JvmType type = convertType(c.type);
		bytecodes.add(new Bytecode.Store(c.slot, type));				
	}

	public void translate(Code.Update code, int freeSlot,
			ArrayList<Bytecode> bytecodes) {

		ArrayList<Type> indices = new ArrayList<Type>();
		for (Code.LVal lv : code) {
			if (lv instanceof Code.StringLVal || lv instanceof Code.ListLVal) {
				indices.add(Type.T_INT);
			} else if (lv instanceof Code.DictLVal) {
				DictLVal dlv = (Code.DictLVal) lv;
				indices.add(dlv.type().key());
			} else {
				// no need to do anything for records
			}
		}

		int indexSlot = freeSlot;
		freeSlot += indices.size();
		// Third, store the value to be assigned
		JvmType val_t = convertType(code.rhs());
		bytecodes.add(new Bytecode.Store(freeSlot++, val_t));

		for (int i = indices.size() - 1; i >= 0; --i) {
			JvmType t = convertType(indices.get(i));
			bytecodes.add(new Bytecode.Store(indexSlot + i, t));
		}

		bytecodes.add(new Bytecode.Load(code.slot, convertType(code.beforeType)));
		
		multiStoreHelper(code.afterType, code.level - 1, code.fields.iterator(),
				indexSlot, val_t, freeSlot, bytecodes);
		bytecodes.add(new Bytecode.Store(code.slot, convertType(code.afterType)));
	}

	public void multiStoreHelper(Type type, int level,
			Iterator<String> fields, int indexSlot, JvmType val_t, int freeSlot, 
			ArrayList<Bytecode> bytecodes) {
		
		// This method is major ugly. I'm sure there must be a better way of
		// doing this. Probably, if I change the multistore bytecode, that would
		// help.
		
		if(Type.isSubtype(Type.Reference(Type.T_ANY), type)) {			
			Type.Reference pt = (Type.Reference) type;
			bytecodes.add(new Bytecode.Dup(WHILEYPROCESS));
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT);		
			bytecodes.add(new Bytecode.Invoke(WHILEYPROCESS, "getState", ftype,
					Bytecode.VIRTUAL));							
			addReadConversion(pt.element(),bytecodes);
			multiStoreHelper(pt.element(),level-1,fields,indexSlot,val_t,freeSlot,bytecodes);						
			ftype = new JvmType.Function(WHILEYPROCESS,JAVA_LANG_OBJECT);		
			bytecodes.add(new Bytecode.Invoke(WHILEYPROCESS, "setState", ftype,
					Bytecode.VIRTUAL));
			
		} else if(type instanceof Type.EffectiveDictionary) {
			Type.EffectiveDictionary dict = (Type.EffectiveDictionary) type;				
			
			if(level != 0) {				
				bytecodes.add(new Bytecode.Dup(WHILEYMAP));				
				bytecodes.add(new Bytecode.Load(indexSlot,convertType(dict.key())));
				addWriteConversion(dict.key(),bytecodes);				
				JvmType.Function ftype = new JvmType.Function(
						JAVA_LANG_OBJECT, WHILEYMAP, JAVA_LANG_OBJECT);
				bytecodes.add(new Bytecode.Invoke(WHILEYMAP, "internal_get", ftype,
					Bytecode.STATIC));				
				addReadConversion(dict.value(),bytecodes);
				multiStoreHelper(dict.value(),level-1,fields,indexSlot+1,val_t,freeSlot,bytecodes);
				bytecodes.add(new Bytecode.Load(indexSlot,convertType(dict.key())));
				bytecodes.add(new Bytecode.Swap());
			} else {
				bytecodes.add(new Bytecode.Load(indexSlot,convertType(dict.key())));
				addWriteConversion(dict.key(),bytecodes);
				bytecodes.add(new Bytecode.Load(indexSlot+1, val_t));	
				addWriteConversion(dict.value(),bytecodes);
			}
						
			JvmType.Function ftype = new JvmType.Function(WHILEYMAP,
					WHILEYMAP,JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);						
			bytecodes.add(new Bytecode.Invoke(WHILEYMAP, "put", ftype,
					Bytecode.STATIC));			
						
		} else if(type == Type.T_STRING) {
			
			// assert: level must be zero here
			bytecodes.add(new Bytecode.Load(indexSlot, BIG_INTEGER));
			bytecodes.add(new Bytecode.Load(indexSlot+1, val_t));
			addWriteConversion(Type.T_INT,bytecodes);			

			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_STRING,
					JAVA_LANG_STRING,BIG_INTEGER,T_CHAR);			
			bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "set", ftype,
					Bytecode.STATIC));						
			
		} else if(type instanceof Type.EffectiveList) {
			Type.EffectiveList list = (Type.EffectiveList) type;				
										
			if(level != 0) {
				bytecodes.add(new Bytecode.Dup(WHILEYLIST));											
				bytecodes.add(new Bytecode.Load(indexSlot,BIG_INTEGER));				
				JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
						WHILEYLIST,BIG_INTEGER);
				bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "internal_get", ftype,
						Bytecode.STATIC));				
				addReadConversion(list.element(),bytecodes);
				multiStoreHelper(list.element(),level-1,fields,indexSlot+1,val_t,freeSlot,bytecodes);				
				bytecodes.add(new Bytecode.Load(indexSlot,BIG_INTEGER));
				bytecodes.add(new Bytecode.Swap());
			} else {				
				bytecodes.add(new Bytecode.Load(indexSlot,BIG_INTEGER));
				bytecodes.add(new Bytecode.Load(indexSlot+1, val_t));
				addWriteConversion(list.element(),bytecodes);
			}
			
			JvmType.Function ftype = new JvmType.Function(WHILEYLIST,
					WHILEYLIST,BIG_INTEGER,JAVA_LANG_OBJECT);			
			bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "set", ftype,
					Bytecode.STATIC));							
		} else {
			Type.EffectiveRecord rec = (Type.EffectiveRecord) type;			
			String field = fields.next();			
			if(level != 0) {				
				bytecodes.add(new Bytecode.Dup(WHILEYRECORD));				
				bytecodes.add(new Bytecode.LoadConst(field));				
				JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,WHILEYRECORD,JAVA_LANG_STRING);
				bytecodes.add(new Bytecode.Invoke(WHILEYRECORD,"internal_get",ftype,Bytecode.STATIC));
				addReadConversion(rec.fields().get(field),bytecodes);
				multiStoreHelper(rec.fields().get(field),level-1,fields,indexSlot,val_t,freeSlot,bytecodes);				
				bytecodes.add(new Bytecode.LoadConst(field));
				bytecodes.add(new Bytecode.Swap());
			} else {
				bytecodes.add(new Bytecode.LoadConst(field));				
				bytecodes.add(new Bytecode.Load(indexSlot, val_t));
				addWriteConversion(rec.fields().get(field),bytecodes);
			}
			
			JvmType.Function ftype = new JvmType.Function(WHILEYRECORD,WHILEYRECORD,JAVA_LANG_STRING,JAVA_LANG_OBJECT);						
			bytecodes.add(new Bytecode.Invoke(WHILEYRECORD,"put",ftype,Bytecode.STATIC));					
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

	public void translate(Code.Throw c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {			
		bytecodes.add(new Bytecode.New(WHILEYEXCEPTION));
		bytecodes.add(new Bytecode.DupX1());
		bytecodes.add(new Bytecode.Swap());				
		JvmType.Function ftype = new JvmType.Function(T_VOID,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYEXCEPTION, "<init>", ftype,
				Bytecode.SPECIAL));		
		bytecodes.add(new Bytecode.Throw());
	}
	
	public void translate(Code.TupleLoad c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
				WHILEYTUPLE, T_INT);
		bytecodes.add(new Bytecode.LoadConst(c.index));		
		bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE, "get", ftype,
				Bytecode.STATIC));		
		addReadConversion(c.type.elements().get(c.index), bytecodes);
	}
	
	public void translate(Code.Switch c, Block.Entry entry, int freeSlot,
			ArrayList<Bytecode> bytecodes) {

		ArrayList<Pair<Integer, String>> cases = new ArrayList();
		boolean canUseSwitchBytecode = true;
		for (Pair<Value, String> p : c.branches) {
			// first, check whether the switch value is indeed an integer.
			Value v = (Value) p.first();
			if (!(v instanceof Value.Integer)) {
				canUseSwitchBytecode = false;
				break;
			}
			// second, check whether integer value can fit into a Java int
			Value.Integer vi = (Value.Integer) v;
			int iv = vi.value.intValue();
			if (!BigInteger.valueOf(iv).equals(vi.value)) {
				canUseSwitchBytecode = false;
				break;
			}
			// ok, we're all good so far
			cases.add(new Pair(iv, p.second()));
		}

		if (canUseSwitchBytecode) {
			JvmType.Function ftype = new JvmType.Function(T_INT);
			bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "intValue", ftype,
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

	public void translate(Code.TryCatch c, Block.Entry entry, int freeSlot,
			ArrayList<UnresolvedHandler> handlers,
			HashMap<Constant, Integer> constants, ArrayList<Bytecode> bytecodes) {
		
		// this code works by redirecting *all* whiley exceptions into the
		// trampoline block. The trampoline then pulls out the matching ones,
		// and lets the remainder be rethrown.
		
		String start = freshLabel();
		String trampolineStart = freshLabel();
		bytecodes.add(new Bytecode.Goto(start));
		// trampoline goes here
		bytecodes.add(new Bytecode.Label(trampolineStart));
		bytecodes.add(new Bytecode.Dup(WHILEYEXCEPTION));
		bytecodes.add(new Bytecode.Store(freeSlot, WHILEYEXCEPTION));
		bytecodes.add(new Bytecode.GetField(WHILEYEXCEPTION, "value", JAVA_LANG_OBJECT, Bytecode.NONSTATIC));
		ArrayList<String> bounces = new ArrayList<String>();
		for (Pair<Type, String> handler : c.catches) {
			String bounce = freshLabel();
			bounces.add(bounce);
			bytecodes.add(new Bytecode.Dup(JAVA_LANG_OBJECT));
			translateTypeTest(bounce, Type.T_ANY, handler.first(),
					bytecodes, constants);
		}
		// rethrow what doesn't match
		bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		bytecodes.add(new Bytecode.Load(freeSlot, WHILEYEXCEPTION));
		bytecodes.add(new Bytecode.Throw());	
		for(int i=0;i!=bounces.size();++i) {
			String bounce = bounces.get(i);
			Pair<Type,String> handler = c.catches.get(i);
			bytecodes.add(new Bytecode.Label(bounce));
			addReadConversion(handler.first(),bytecodes);
			bytecodes.add(new Bytecode.Goto(handler.second()));			
		}
		bytecodes.add(new Bytecode.Label(start));
		
		UnresolvedHandler trampolineHandler = new UnresolvedHandler(start,
				c.target, trampolineStart, WHILEYEXCEPTION);
		handlers.add(trampolineHandler);
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
		} else if(c.type == Type.T_CHAR || c.type == Type.T_BYTE) {
			int op;
			switch(c.op) {
			case EQ:				
				op = Bytecode.IfCmp.EQ;
				break;
			case NEQ:				
				op = Bytecode.IfCmp.NE;
				break;
			case LT:				
				op = Bytecode.IfCmp.LT;
				break;
			case LTEQ:				
				op = Bytecode.IfCmp.LE;
				break;
			case GT:				
				op = Bytecode.IfCmp.GT;
				break;
			case GTEQ:				
				op = Bytecode.IfCmp.GE;
				break;
			default:
				internalFailure("unknown if condition encountered",filename,stmt);
				return;
			}
			bytecodes.add(new Bytecode.IfCmp(op, T_BYTE,c.target));
		} else {
			// Non-primitive case. Just use the Object.equals() method, followed
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
				JvmType.Function ftype = new JvmType.Function(T_BOOL,WHILEYSET,WHILEYSET);
				bytecodes.add(new Bytecode.Invoke(WHILEYSET, "subsetEq", ftype,
						Bytecode.STATIC));
				op = Bytecode.If.NE;
				break;
			}
			case SUBSET:
			{
				JvmType.Function ftype = new JvmType.Function(T_BOOL,WHILEYSET,WHILEYSET);
				bytecodes.add(new Bytecode.Invoke(WHILEYSET, "subset", ftype,
						Bytecode.STATIC));
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
			HashMap<Constant,Integer> constants, ArrayList<Bytecode> bytecodes) {						
		
		if(c.slot >= 0) {
			// In this case, we're updating the type of a local variable. To
			// make this work, we must update the JVM type of that slot as well
			// using a checkcast. 
			String exitLabel = freshLabel();
			String trueLabel = freshLabel();
					
			bytecodes.add(new Bytecode.Load(c.slot, convertType(c.type)));
			translateTypeTest(trueLabel, c.type, c.test, bytecodes, constants);

			Type gdiff = Type.intersect(c.type,Type.Negation(c.test));			
			bytecodes.add(new Bytecode.Load(c.slot, convertType(c.type)));
			// now, add checkcast									
			addReadConversion(gdiff,bytecodes);			
			bytecodes.add(new Bytecode.Store(c.slot,convertType(gdiff)));							
			bytecodes.add(new Bytecode.Goto(exitLabel));
			bytecodes.add(new Bytecode.Label(trueLabel));			
			Type glb = Type.intersect(c.type, c.test);			
			bytecodes.add(new Bytecode.Load(c.slot, convertType(c.type)));
			// now, add checkcast						
			addReadConversion(glb,bytecodes);
			bytecodes.add(new Bytecode.Store(c.slot,convertType(glb)));			
			bytecodes.add(new Bytecode.Goto(c.target));
			bytecodes.add(new Bytecode.Label(exitLabel));
		} else {
			// This is the easy case. We're not updating the type of a local
			// variable; rather we're just type testing a value on the stack.
			translateTypeTest(c.target, c.type, c.test, bytecodes, constants);
		}
	}
	
	// The purpose of this method is to translate a type test. We're testing to
	// see whether what's on the top of the stack (the value) is a subtype of
	// the type being tested.  
	protected void translateTypeTest(String trueTarget, Type src, Type test,
			ArrayList<Bytecode> bytecodes, HashMap<Constant,Integer> constants) {		
		
		// First, try for the easy cases
		
		if (test instanceof Type.Null) {
			// Easy case		
			bytecodes.add(new Bytecode.If(Bytecode.If.NULL, trueTarget));
		} else if(test instanceof Type.Bool) {
			bytecodes.add(new Bytecode.InstanceOf(JAVA_LANG_BOOLEAN));			
			bytecodes.add(new Bytecode.If(Bytecode.If.NE, trueTarget));
		} else if(test instanceof Type.Char) {
			bytecodes.add(new Bytecode.InstanceOf(JAVA_LANG_CHARACTER));			
			bytecodes.add(new Bytecode.If(Bytecode.If.NE, trueTarget));			
		} else if(test instanceof Type.Int) {
			bytecodes.add(new Bytecode.InstanceOf(BIG_INTEGER));			
			bytecodes.add(new Bytecode.If(Bytecode.If.NE, trueTarget));
		} else if(test instanceof Type.Real) {
			bytecodes.add(new Bytecode.InstanceOf(BIG_RATIONAL));			
			bytecodes.add(new Bytecode.If(Bytecode.If.NE, trueTarget));
		} else if(test instanceof Type.Strung) {
			bytecodes.add(new Bytecode.InstanceOf(JAVA_LANG_STRING));			
			bytecodes.add(new Bytecode.If(Bytecode.If.NE, trueTarget));
			
		} else {
			// Fall-back to an external (recursive) check			
			Value constant = Value.V_TYPE(test);
			int id = ValueConst.get(constant,constants);			
			String name = "constant$" + id;

			bytecodes.add(new Bytecode.GetField(owner, name, WHILEYTYPE, Bytecode.STATIC));

			JvmType.Function ftype = new JvmType.Function(T_BOOL,convertType(src),WHILEYTYPE);
			bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "instanceOf",
					ftype, Bytecode.STATIC));
			bytecodes.add(new Bytecode.If(Bytecode.If.NE, trueTarget));
		}
	}	

	public void translate(Code.Loop c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Label(c.target + "$head"));
	}
	
	protected void translate(Code.LoopEnd end,			
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Goto(end.label + "$head"));
		bytecodes.add(new Bytecode.Label(end.label));
	}
	
	public int translate(Code.ForAll c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {	
		
		Type elementType = c.type.element();		

		JvmType.Function ftype = new JvmType.Function(JAVA_UTIL_ITERATOR,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYCOLLECTION, "iterator", ftype, Bytecode.STATIC));
		ftype = new JvmType.Function(JAVA_UTIL_ITERATOR);
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
		bytecodes.add(new Bytecode.Store(c.slot, convertType(elementType)));
		
		// we need to increase the freeSlot, since we've allocated one slot to
		// hold the iterator.
		
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
		JvmType.Function ftype = new JvmType.Function(T_VOID,JAVA_LANG_STRING);
		bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "debug", ftype,
				Bytecode.STATIC));
	}
	
	public void translate(Code.Destructure code, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		
		if(code.type instanceof Type.EffectiveTuple) {
			Type.EffectiveTuple t = (Type.EffectiveTuple) code.type;
			List<Type> elements = t.elements();
			for(int i=0;i!=elements.size();++i) {
				Type elem = elements.get(i);
				if((i+1) != elements.size()) {
					bytecodes.add(new Bytecode.Dup(BIG_RATIONAL));
				}
				JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,T_INT);
				bytecodes.add(new Bytecode.LoadConst(i));
				// TODO: turn into static method call
				// NOTE: this is interesting since we should really be
				// decrementing the reference count. However, it's safe not to
				// do this since the destructure is "consuming" the tuple
				// anyway.
				bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE, "get", ftype,
						Bytecode.VIRTUAL));
				addReadConversion(elem,bytecodes);
				if((i+1) != elements.size()) {
					bytecodes.add(new Bytecode.Swap());
				}
			}
		} else {
			bytecodes.add(new Bytecode.Dup(BIG_RATIONAL));
			JvmType.Function ftype = new JvmType.Function(BIG_INTEGER);
			bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "numerator", ftype,
					Bytecode.VIRTUAL));
			bytecodes.add(new Bytecode.Swap());
			bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "denominator", ftype,
					Bytecode.VIRTUAL));
		}		
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
	
	public void translate(Code.Load c, int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Load(c.slot, convertType(c.type)));
		addIncRefs(c.type,bytecodes);
	}
	
	public void translate(Code.Move c, int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Load(c.slot, convertType(c.type)));
		// a move does not need to increment the reference count, since the
		// register is no longer usable after this point.
		
		// TODO: the following codes are not strictly necessary, and clearly
		// lead to less efficient bytecode. Therefore, at some point, they
		// should be remove. However, they do provide useful debugging code to
		// check that the Move bytecode is, in fact, doing what it should (i.e.
		// the register in question is actually dead).
		bytecodes.add(new Bytecode.LoadConst(1.0F));
		bytecodes.add(new Bytecode.Store(c.slot, T_FLOAT));
	}
		
	public void translate(Code.ListAppend c, Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {						
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
	}
	
	public void translate(Code.LengthOf c, Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {				
		// FIXME: the following is a bit of a hack which works for now. When I
		// refactor the collections library then it can be fixed properly.
		JvmType.Clazz ctype = JAVA_LANG_OBJECT;
		JvmType.Function ftype = new JvmType.Function(BIG_INTEGER,ctype);						
		bytecodes.add(new Bytecode.Invoke(WHILEYCOLLECTION, "length",
				ftype, Bytecode.STATIC));								
	}
	
	public void translate(Code.SubList c, Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {	
		JvmType.Function ftype = new JvmType.Function(WHILEYLIST, WHILEYLIST,
				BIG_INTEGER, BIG_INTEGER);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "sublist", ftype,
				Bytecode.STATIC));
	}	
	
	public void translate(Code.IndexOf c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
				JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYCOLLECTION, "indexOf", ftype,
				Bytecode.STATIC));
		addReadConversion(c.type.value(), bytecodes);
	}
	
	public void translate(Code.FieldLoad c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {		
		bytecodes.add(new Bytecode.LoadConst(c.field));
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,WHILEYRECORD,JAVA_LANG_STRING);
		bytecodes.add(new Bytecode.Invoke(WHILEYRECORD,"get",ftype,Bytecode.STATIC));						
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
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz)type, "divide", ftype,
					Bytecode.VIRTUAL));			
			break;
		case REM:									
				bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type,
						"remainder", ftype, Bytecode.VIRTUAL));			
			break;
		case RANGE:
			ftype = new JvmType.Function(WHILEYLIST,BIG_INTEGER,BIG_INTEGER);
			bytecodes.add(new Bytecode.Invoke(WHILEYUTIL,
					"range", ftype, Bytecode.STATIC));
			break;
		case BITWISEAND:
			bytecodes.add(new Bytecode.BinOp(Bytecode.BinOp.AND,T_INT));
			break;
		case BITWISEOR:
			bytecodes.add(new Bytecode.BinOp(Bytecode.BinOp.OR,T_INT));
			break;
		case BITWISEXOR:
			bytecodes.add(new Bytecode.BinOp(Bytecode.BinOp.XOR,T_INT));
			break;
		case LEFTSHIFT:
			ftype = new JvmType.Function(type,type,BIG_INTEGER);
			bytecodes.add(new Bytecode.Invoke(WHILEYUTIL,
					"leftshift", ftype, Bytecode.STATIC));
			break;
		case RIGHTSHIFT:
			ftype = new JvmType.Function(type,type,BIG_INTEGER);
			bytecodes.add(new Bytecode.Invoke(WHILEYUTIL,
					"rightshift", ftype, Bytecode.STATIC));
			break;
		default:
			internalFailure("unknown binary expression encountered",filename,stmt);
		}		
	}

	public void translate(Code.SetUnion c, Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {		
		JvmType.Function ftype;
		if(c.dir == OpDir.UNIFORM) {
			ftype = new JvmType.Function(WHILEYSET,WHILEYSET,WHILEYSET);
		} else if(c.dir == OpDir.LEFT) {
			ftype = new JvmType.Function(WHILEYSET,WHILEYSET,JAVA_LANG_OBJECT);
		} else {
			ftype = new JvmType.Function(WHILEYSET,JAVA_LANG_OBJECT,WHILEYSET);
		}													
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "union", ftype,
				Bytecode.STATIC));				
	}	
	
	public void translate(Code.SetIntersect c, Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {		
		JvmType.Function ftype;
		if(c.dir == OpDir.UNIFORM) {
			ftype = new JvmType.Function(WHILEYSET,WHILEYSET,WHILEYSET);
		} else if(c.dir == OpDir.LEFT) {
			ftype = new JvmType.Function(WHILEYSET,WHILEYSET,JAVA_LANG_OBJECT);
		} else {
			ftype = new JvmType.Function(WHILEYSET,JAVA_LANG_OBJECT,WHILEYSET);
		}													
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "intersect", ftype,
				Bytecode.STATIC));
	}
	
	public void translate(Code.SetDifference c, Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {		
		JvmType.Function ftype;
		if(c.dir == OpDir.UNIFORM) {
			ftype = new JvmType.Function(WHILEYSET,WHILEYSET,WHILEYSET);
		} else {
			ftype = new JvmType.Function(WHILEYSET,WHILEYSET,JAVA_LANG_OBJECT);
		} 												
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "difference", ftype,
				Bytecode.STATIC));
	}
		
	public void translate(Code.StringAppend c, Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {						
		JvmType.Function ftype;
		if(c.dir == OpDir.UNIFORM) {
			ftype = new JvmType.Function(JAVA_LANG_STRING,JAVA_LANG_STRING,JAVA_LANG_STRING);
		} else if(c.dir == OpDir.LEFT) {
			ftype = new JvmType.Function(JAVA_LANG_STRING,JAVA_LANG_STRING,T_CHAR);				
		} else {
			ftype = new JvmType.Function(JAVA_LANG_STRING,T_CHAR,JAVA_LANG_STRING);				
		}													
		bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "append", ftype,
				Bytecode.STATIC));
	}
		
	public void translate(Code.SubString c, Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {						
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_STRING,JAVA_LANG_STRING,
				BIG_INTEGER, BIG_INTEGER);
		bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "substring", ftype,
				Bytecode.STATIC));
	}	
	
	public void translate(Code.Invert c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {	
		bytecodes.add(new Bytecode.LoadConst(-1));
		bytecodes.add(new Bytecode.BinOp(Bytecode.BinOp.XOR,T_INT));			
	}
	
	public void translate(Code.Negate c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {								
		JvmType type = convertType(c.type);
		JvmType.Function ftype = new JvmType.Function(type);
		bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type, "negate",
				ftype, Bytecode.VIRTUAL));		
	}
	
	public void translate(Code.New c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {							
		bytecodes.add(new Bytecode.New(WHILEYPROCESS));			
		bytecodes.add(new Bytecode.DupX1());
		bytecodes.add(new Bytecode.Swap());
		// TODO: problem here ... need to swap or something		
		JvmType.Function ftype = new JvmType.Function(T_VOID, JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYPROCESS, "<init>", ftype,
				Bytecode.SPECIAL));
	}
	
	public void translate(Code.Dereference c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {				
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT);		
		bytecodes.add(new Bytecode.Invoke(WHILEYPROCESS, "getState", ftype,
				Bytecode.VIRTUAL));
		// finally, we need to cast the object we got back appropriately.		
		Type.Reference pt = (Type.Reference) c.type;						
		addReadConversion(pt.element(), bytecodes);
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
			// FIXME: need write conversion here?
			bytecodes.add(new Bytecode.Invoke(WHILEYMAP, "put", ftype,
					Bytecode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		}

		bytecodes.add(new Bytecode.Load(freeSlot, WHILEYMAP));
	}
	
	protected void translate(Code.NewList c, int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.New(WHILEYLIST));		
		bytecodes.add(new Bytecode.Dup(WHILEYLIST));
		bytecodes.add(new Bytecode.LoadConst(c.nargs));
		JvmType.Function ftype = new JvmType.Function(T_VOID,T_INT);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "<init>", ftype,
				Bytecode.SPECIAL));
		
		ftype = new JvmType.Function(WHILEYLIST, WHILEYLIST, JAVA_LANG_OBJECT);		
		for(int i=0;i!=c.nargs;++i) {			
			bytecodes.add(new Bytecode.Swap());			
			addWriteConversion(c.type.element(),bytecodes);			
			bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "internal_add", ftype,
					Bytecode.STATIC));			
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
				
		JvmType.Clazz owner = new JvmType.Clazz("java.util","Collections");
		ftype = new JvmType.Function(T_VOID, JAVA_UTIL_LIST);		
		bytecodes.add(new Bytecode.Dup(WHILEYLIST));
		bytecodes.add(new Bytecode.Invoke(owner,"reverse",ftype,Bytecode.STATIC));			
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
		JvmType.Function ftype = new JvmType.Function(WHILEYSET,
				WHILEYSET,JAVA_LANG_OBJECT);
		
		for(int i=0;i!=c.nargs;++i) {
			bytecodes.add(new Bytecode.Swap());			
			addWriteConversion(c.type.element(),bytecodes);			
			bytecodes.add(new Bytecode.Invoke(WHILEYSET,"internal_add",ftype,Bytecode.STATIC));
		}
	}
	
	protected void translate(Code.NewTuple c, int freeSlot, ArrayList<Bytecode> bytecodes) {
		construct(WHILEYTUPLE, freeSlot, bytecodes);
		JvmType.Function ftype = new JvmType.Function(WHILEYTUPLE,
				WHILEYTUPLE,JAVA_LANG_OBJECT);
					
		ArrayList<Type> types = new ArrayList<Type>(c.type.elements());
		Collections.reverse(types);
		
		for(Type type : types) {
			bytecodes.add(new Bytecode.Swap());	
			addWriteConversion(type,bytecodes);			
			bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE,"internal_add",ftype,Bytecode.STATIC));
		}
		
		// At this stage, we have a problem. We've added the elements into the
		// tuple in reverse order. For simplicity, I simply call reverse at this
		// stage. However, it begs the question how we can do better.
		//
		// We could store each value into a register and then reload them in the
		// reverse order. For very large lists, this might cause a problem I
		// suspect.
		//
		// Another option would be to have a special list initialise function
		// with a range of different constructors for different sized lists.
				
		JvmType.Clazz owner = new JvmType.Clazz("java.util","Collections");
		ftype = new JvmType.Function(T_VOID, JAVA_UTIL_LIST);		
		bytecodes.add(new Bytecode.Dup(WHILEYTUPLE));
		bytecodes.add(new Bytecode.Invoke(owner,"reverse",ftype,Bytecode.STATIC));
	}
	
	public void translate(Code.Invoke c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		Path.ID mid = c.name.module();
		String mangled = nameMangle(c.name.name(), c.type);
		JvmType.Clazz owner = new JvmType.Clazz(mid.parent().toString()
				.replace('/', '.'), mid.last());
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

		Type.FunctionOrMethodOrMessage ft = c.type;		
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

		Type.Message ft = c.type;		
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
		
		bytecodes.add(new Bytecode.LoadConst(c.name.module().toString().replace('/','.')));		
		bytecodes
				.add(new Bytecode.LoadConst(nameMangle(c.name.name(), c.type)));
		bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "functionRef", ftype,
				Bytecode.STATIC));
		bytecodes.add(new Bytecode.Load(freeSlot, arrT));
		
		ftype = new JvmType.Function(c.synchronous ? WHILEYFUTURE : T_VOID,
				JAVA_LANG_REFLECT_METHOD, JAVA_LANG_OBJECT_ARRAY);
		
		bytecodes.add(new Bytecode.Invoke(WHILEYMESSAGER, c.synchronous ?
				"sendSync" : "sendAsync", ftype, Bytecode.VIRTUAL));
		
		if (c.synchronous) {
			if (c.retval) { 
				bytecodes.add(new Bytecode.Invoke(WHILEYFUTURE, "getResult",
						new JvmType.Function(JAVA_LANG_OBJECT), Bytecode.VIRTUAL));
				addReadConversion(c.type.ret(), bytecodes);
			} else {
				bytecodes.add(new Bytecode.Pop(WHILEYFUTURE));
			}
		}
	}
	
	public void translate(Code.IndirectSend c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		// The main issue here, is that we have all of the parameters + receiver
		// on the stack. What we need to do is to put them into an array, so
		// they can then be passed into Method.invoke()
		//
		// To make this work, what we'll do is use a temporary register to hold
		// the array as we build it up.

		Type.Message ft = c.type;		
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
		bytecodes.add(new Bytecode.Swap());
		bytecodes.add(new Bytecode.Load(freeSlot, arrT));
		
		JvmType.Function ftype = new JvmType.Function(c.synchronous ? WHILEYFUTURE
				: T_VOID, JAVA_LANG_REFLECT_METHOD, JAVA_LANG_OBJECT_ARRAY);
		
		bytecodes.add(new Bytecode.Invoke(WHILEYMESSAGER, c.synchronous ?
				"sendSync" : "sendAsync", ftype, Bytecode.VIRTUAL));
		
		if (c.synchronous) {
			if (c.retval) {
				bytecodes.add(new Bytecode.Invoke(WHILEYFUTURE, "getResult",
						new JvmType.Function(JAVA_LANG_OBJECT), Bytecode.VIRTUAL));
				addReadConversion(c.type.ret(), bytecodes);
			} else {
				bytecodes.add(new Bytecode.Pop(WHILEYFUTURE));
			}
		}
	}
		
	public void translate(Value v, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		if(v instanceof Value.Null) {
			translate((Value.Null)v,freeSlot,bytecodes);
		} else if(v instanceof Value.Bool) {
			translate((Value.Bool)v,freeSlot,bytecodes);
		} else if(v instanceof Value.Byte) {
			translate((Value.Byte)v,freeSlot,bytecodes);
		} else if(v instanceof Value.Char) {
			translate((Value.Char)v,freeSlot,bytecodes);
		} else if(v instanceof Value.Integer) {
			translate((Value.Integer)v,freeSlot,bytecodes);
		} else if(v instanceof Value.Type) {
			translate((Value.Type)v,freeSlot,bytecodes);
		} else if(v instanceof Value.Rational) {
			translate((Value.Rational)v,freeSlot,bytecodes);
		} else if(v instanceof Value.Strung) {
			translate((Value.Strung)v,freeSlot,bytecodes);
		} else if(v instanceof Value.Set) {
			translate((Value.Set)v,freeSlot,bytecodes);
		} else if(v instanceof Value.List) {
			translate((Value.List)v,freeSlot,bytecodes);
		} else if(v instanceof Value.Record) {
			translate((Value.Record)v,freeSlot,bytecodes);
		} else if(v instanceof Value.Dictionary) {
			translate((Value.Dictionary)v,freeSlot,bytecodes);
		} else if(v instanceof Value.Tuple) {
			translate((Value.Tuple)v,freeSlot,bytecodes);
		} else if(v instanceof Value.FunctionOrMethodOrMessage) {
			translate((Value.FunctionOrMethodOrMessage)v,freeSlot,bytecodes);
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

	protected void translate(Value.Type e, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		JavaIdentifierOutputStream jout = new JavaIdentifierOutputStream();
		BinaryOutputStream bout = new BinaryOutputStream(jout);
		Type.BinaryWriter writer = new Type.BinaryWriter(bout);		
		try {
			writer.write(e.type);
			writer.close();
		} catch(IOException ex) {
			throw new RuntimeException(ex.getMessage(),ex);
		}
		
		bytecodes.add(new Bytecode.LoadConst(jout.toString()));
		JvmType.Function ftype = new JvmType.Function(WHILEYTYPE,
				JAVA_LANG_STRING);
		bytecodes.add(new Bytecode.Invoke(WHILEYTYPE, "valueOf", ftype,
				Bytecode.STATIC));		
	}
	
	protected void translate(Value.Byte e, int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.LoadConst(e.value));		
	}
	
	protected void translate(Value.Char e, int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.LoadConst(e.value));		
	}
			
	protected void translate(Value.Integer e, int freeSlot,			
			ArrayList<Bytecode> bytecodes) {		
		BigInteger num = e.value;
		
		if(num.bitLength() < 32) {			
			bytecodes.add(new Bytecode.LoadConst(num.intValue()));				
			bytecodes.add(new Bytecode.Conversion(T_INT,T_LONG));
			JvmType.Function ftype = new JvmType.Function(BIG_INTEGER,T_LONG);
			bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "valueOf", ftype,
					Bytecode.STATIC));
		} else if(num.bitLength() < 64) {			
			bytecodes.add(new Bytecode.LoadConst(num.longValue()));				
			JvmType.Function ftype = new JvmType.Function(BIG_INTEGER,T_LONG);
			bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "valueOf", ftype,
					Bytecode.STATIC));
		} else {
			// in this context, we need to use a byte array to construct the
			// integer object.
			byte[] bytes = num.toByteArray();
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
	
	protected void translate(Value.Rational e, int freeSlot,
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
	
	protected void translate(Value.Strung e, int freeSlot,
			ArrayList<Bytecode> bytecodes) {		
		bytecodes.add(new Bytecode.LoadConst(e.value));
	}
	
	protected void translate(Value.Set lv, int freeSlot,
			ArrayList<Bytecode> bytecodes) {	
		bytecodes.add(new Bytecode.New(WHILEYSET));		
		bytecodes.add(new Bytecode.Dup(WHILEYSET));
		JvmType.Function ftype = new JvmType.Function(T_VOID);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "<init>", ftype,
				Bytecode.SPECIAL));
		
		ftype = new JvmType.Function(T_BOOL, JAVA_LANG_OBJECT);		
		for (Value e : lv.values) {
			bytecodes.add(new Bytecode.Dup(WHILEYSET));
			translate(e, freeSlot, bytecodes);
			addWriteConversion(e.type(), bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYSET, "add", ftype,
					Bytecode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(T_BOOL));
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
		
		ftype = new JvmType.Function(T_BOOL, JAVA_LANG_OBJECT);		
		for (Value e : lv.values) {	
			bytecodes.add(new Bytecode.Dup(WHILEYLIST));
			translate(e, freeSlot, bytecodes);
			addWriteConversion(e.type(), bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "add", ftype,
					Bytecode.VIRTUAL));			
			bytecodes.add(new Bytecode.Pop(T_BOOL));
		}				
	}

	protected void translate(Value.Tuple lv, int freeSlot,
			ArrayList<Bytecode> bytecodes) {		
		bytecodes.add(new Bytecode.New(WHILEYTUPLE));		
		bytecodes.add(new Bytecode.Dup(WHILEYTUPLE));
		bytecodes.add(new Bytecode.LoadConst(lv.values.size()));
		JvmType.Function ftype = new JvmType.Function(T_VOID,T_INT);
		bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE, "<init>", ftype,
				Bytecode.SPECIAL));
		
		ftype = new JvmType.Function(T_BOOL, JAVA_LANG_OBJECT);		
		for (Value e : lv.values) {	
			bytecodes.add(new Bytecode.Dup(WHILEYTUPLE));
			translate(e, freeSlot, bytecodes);
			addWriteConversion(e.type(), bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE, "add", ftype,
					Bytecode.VIRTUAL));			
			bytecodes.add(new Bytecode.Pop(T_BOOL));
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
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
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
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		}
	}
	
	protected void translate(Value.FunctionOrMethodOrMessage e, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_REFLECT_METHOD,JAVA_LANG_STRING,JAVA_LANG_STRING);
		NameID nid = e.name;		
		bytecodes.add(new Bytecode.LoadConst(nid.module().toString().replace('/','.')));
		bytecodes.add(new Bytecode.LoadConst(nameMangle(nid.name(),e.type)));
		bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "functionRef", ftype,Bytecode.STATIC));
	}

	protected void addCoercion(Type from, Type to, int freeSlot,
			HashMap<Constant, Integer> constants, ArrayList<Bytecode> bytecodes) {
		
		// First, deal with coercions which require a change of representation
		// when going into a union.  For example, bool must => Boolean.
		if (!(to instanceof Type.Bool) && from instanceof Type.Bool) {
			// this is either going into a union type, or the any type
			buildCoercion((Type.Bool) from, to, freeSlot, bytecodes);
		} else if(from == Type.T_BYTE) {									
			buildCoercion((Type.Byte)from, to, freeSlot,bytecodes); 
		} else if(from == Type.T_CHAR) {									
			buildCoercion((Type.Char)from, to, freeSlot,bytecodes);  
		} else if (Type.intersect(from, to).equals(from)) {			
			// do nothing!
			// (note, need to check this after primitive types to avoid risk of
			// missing coercion to any)
		} else  if(from == Type.T_INT) {									
			buildCoercion((Type.Int)from, to, freeSlot,bytecodes);  
		} else if(from == Type.T_STRING && to instanceof Type.List) {									
			buildCoercion((Type.Strung)from, (Type.List) to, freeSlot,bytecodes); 
		} else {			
			// ok, it's a harder case so we use an explicit coercion function								
			int id = Coercion.get(from,to,constants);
			String name = "coercion$" + id;
			JvmType.Function ft = new JvmType.Function(convertType(to), convertType(from));
			bytecodes.add(new Bytecode.Invoke(owner, name, ft, Bytecode.STATIC));
		}
	}

	public void buildCoercion(Type.Bool fromType, Type toType, 
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_BOOLEAN,T_BOOL);			
		bytecodes.add(new Bytecode.Invoke(JAVA_LANG_BOOLEAN,"valueOf",ftype,Bytecode.STATIC));			
		// done deal!
	}
	
	public void buildCoercion(Type.Byte fromType, Type toType,
			int freeSlot, ArrayList<Bytecode> bytecodes) {		
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_BYTE,T_BYTE);			
		bytecodes.add(new Bytecode.Invoke(JAVA_LANG_BYTE,"valueOf",ftype,Bytecode.STATIC));			
		// done deal!
	}
	
	public void buildCoercion(Type.Int fromType, Type toType, 
			int freeSlot, ArrayList<Bytecode> bytecodes) {		
		Type glb = Type.intersect(Type.T_REAL, toType);
		if(glb == Type.T_REAL) { 
			// coercion required!
			JvmType.Function ftype = new JvmType.Function(BIG_RATIONAL,BIG_INTEGER);			
			bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL,"valueOf",ftype,Bytecode.STATIC));
		} else {				
			// must be => char
			JvmType.Function ftype = new JvmType.Function(T_INT);			
			bytecodes.add(new Bytecode.Invoke(BIG_INTEGER,"intValue",ftype,Bytecode.VIRTUAL));				
		}
	}

	public void buildCoercion(Type.Char fromType, Type toType, 
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		if(!Type.isSubtype(toType,fromType)) {					
			if(toType == Type.T_REAL) { 
				// coercion required!
				JvmType.Function ftype = new JvmType.Function(BIG_RATIONAL,T_INT);			
				bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL,"valueOf",ftype,Bytecode.STATIC));
			} else {
				bytecodes.add(new Bytecode.Conversion(T_INT, T_LONG));
				JvmType.Function ftype = new JvmType.Function(BIG_INTEGER,T_LONG);			
				bytecodes.add(new Bytecode.Invoke(BIG_INTEGER,"valueOf",ftype,Bytecode.STATIC));				
			}
		} else {
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_CHARACTER,T_CHAR);			
			bytecodes.add(new Bytecode.Invoke(JAVA_LANG_CHARACTER,"valueOf",ftype,Bytecode.STATIC));	
		}
	}
	
	public void buildCoercion(Type.Strung fromType, Type.List toType, 
			int freeSlot, ArrayList<Bytecode> bytecodes) {		
		JvmType.Function ftype = new JvmType.Function(WHILEYLIST,JAVA_LANG_STRING);
		
		if(toType.element() == Type.T_CHAR) {
			bytecodes.add(new Bytecode.Invoke(WHILEYUTIL,"str2cl",ftype,Bytecode.STATIC));	
		} else {
			bytecodes.add(new Bytecode.Invoke(WHILEYUTIL,"str2il",ftype,Bytecode.STATIC));
		}		
	}
	/**
	 * The build coercion method constructs a static final private method which
	 * accepts a value of type "from", and coerces it into a value of type "to".  
	 * 
	 * @param to
	 * @param from
	 * 
	 */
	protected void buildCoercion(Type from, Type to, int id,
			HashMap<Constant, Integer> constants, ClassFile cf) {
		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
		
		int freeSlot = 1;
		bytecodes.add(new Bytecode.Load(0,convertType(from)));				
		buildCoercion(from,to,freeSlot,constants,bytecodes);
		bytecodes.add(new Bytecode.Return(convertType(to)));
		
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PRIVATE);
		modifiers.add(Modifier.ACC_STATIC);
		modifiers.add(Modifier.ACC_SYNTHETIC);
		JvmType.Function ftype = new JvmType.Function(convertType(to),convertType(from));
		String name = "coercion$" + id;
		ClassFile.Method method = new ClassFile.Method(name, ftype, modifiers);
		cf.methods().add(method);
		wyjvm.attributes.Code code = new wyjvm.attributes.Code(bytecodes,new ArrayList(),method);
		method.attributes().add(code);				
	}
	
	protected void buildCoercion(Type from, Type to, int freeSlot,
			HashMap<Constant, Integer> constants, ArrayList<Bytecode> bytecodes) {
		
		// Second, case analysis on the various kinds of coercion
		if(from instanceof Type.Tuple && to instanceof Type.Tuple) {
			buildCoercion((Type.Tuple) from, (Type.Tuple) to, freeSlot, constants, bytecodes);
		} else if(from instanceof Type.Reference && to instanceof Type.Reference) {
			// TODO			
		} else if(from instanceof Type.Set && to instanceof Type.Set) {
			buildCoercion((Type.Set) from, (Type.Set) to, freeSlot, constants, bytecodes);			
		} else if(from instanceof Type.Set && to instanceof Type.Dictionary) {
			buildCoercion((Type.Set) from, (Type.Dictionary) to, freeSlot, constants, bytecodes);			
		} else if(from instanceof Type.List && to instanceof Type.Set) {
			buildCoercion((Type.List) from, (Type.Set) to, freeSlot, constants, bytecodes);			
		} else if(from instanceof Type.Dictionary && to instanceof Type.Dictionary) {
			buildCoercion((Type.Dictionary) from, (Type.Dictionary) to, freeSlot, constants, bytecodes);			
		} else if(from instanceof Type.List && to instanceof Type.Dictionary) {
			buildCoercion((Type.List) from, (Type.Dictionary) to, freeSlot, constants, bytecodes);			
		} else if(from instanceof Type.List && to instanceof Type.List) {
			buildCoercion((Type.List) from, (Type.List) to, freeSlot, constants, bytecodes);			
		} else if(to instanceof Type.Record && from instanceof Type.Record) {
			buildCoercion((Type.Record) from, (Type.Record) to, freeSlot, constants, bytecodes);
		} else if(to instanceof Type.Function && from instanceof Type.Function) {
			// TODO
		} else if(from instanceof Type.Negation || to instanceof Type.Negation) {			
			// no need to do anything, since convertType on a negation returns java/lang/Object
		} else if(from instanceof Type.Union) {			
			buildCoercion((Type.Union) from, to, freeSlot, constants, bytecodes);
		} else if(to instanceof Type.Union) {			
			buildCoercion(from, (Type.Union) to, freeSlot, constants, bytecodes);
		} else {
			throw new RuntimeException("invalid coercion encountered: " + from + " => " + to);
		}						
	}
	
	protected void buildCoercion(Type.Tuple fromType, Type.Tuple toType, 
			int freeSlot, HashMap<Constant, Integer> constants,
			ArrayList<Bytecode> bytecodes) {
		int oldSlot = freeSlot++;
		int newSlot = freeSlot++;		
		bytecodes.add(new Bytecode.Store(oldSlot,WHILEYTUPLE));
		construct(WHILEYTUPLE,freeSlot,bytecodes);
		bytecodes.add(new Bytecode.Store(newSlot,WHILEYTUPLE));
		List<Type> from_elements = fromType.elements();
		List<Type> to_elements = toType.elements();
		for(int i=0;i!=to_elements.size();++i) {
			Type from = from_elements.get(i);
			Type to = to_elements.get(i);
			bytecodes.add(new Bytecode.Load(newSlot,WHILEYTUPLE));			
			bytecodes.add(new Bytecode.Load(oldSlot,WHILEYTUPLE));
			bytecodes.add(new Bytecode.LoadConst(i));
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,T_INT);			
			bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE,"get",ftype,Bytecode.VIRTUAL));								
			addReadConversion(from,bytecodes);							
			// now perform recursive conversion
			addCoercion(from,to,freeSlot,constants,bytecodes);							
			ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);			
			bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE,"add",ftype,Bytecode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(T_BOOL));
		}
		bytecodes.add(new Bytecode.Load(newSlot,WHILEYTUPLE));
	}
	
		
	protected void buildCoercion(Type.List fromType, Type.List toType, 
			int freeSlot, HashMap<Constant, Integer> constants,
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
		addCoercion(fromType.element(), toType.element(), freeSlot,
				constants, bytecodes);			
		ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "add",
				ftype, Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(T_BOOL));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(tmp,WHILEYLIST));
	}
	
	protected void buildCoercion(Type.List fromType, Type.Dictionary toType, 
			int freeSlot, HashMap<Constant, Integer> constants,
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
		bytecodes.add(new Bytecode.Conversion(T_INT,T_LONG));	
		ftype = new JvmType.Function(BIG_INTEGER,T_LONG);
		bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "valueOf",
				ftype, Bytecode.STATIC));				
		bytecodes.add(new Bytecode.Load(source,WHILEYMAP));
		bytecodes.add(new Bytecode.Load(iter,T_INT));
		ftype = new JvmType.Function(JAVA_LANG_OBJECT,T_INT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_LIST, "get",
				ftype, Bytecode.INTERFACE));						
		addReadConversion(fromType.element(),bytecodes);		
		addCoercion(fromType.element(), toType.value(), freeSlot,
				constants, bytecodes);			
		ftype = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYMAP, "put",
				ftype, Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		bytecodes.add(new Bytecode.Iinc(iter,1));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(target,WHILEYMAP));		
	}
	
	protected void buildCoercion(Type.Dictionary fromType, Type.Dictionary toType, 
			int freeSlot, HashMap<Constant, Integer> constants,
			ArrayList<Bytecode> bytecodes) {
		
		if (fromType.key() == Type.T_VOID || toType.key() == Type.T_VOID) {
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
		
		bytecodes.add(new Bytecode.Dup(WHILEYMAP));
		bytecodes.add(new Bytecode.Store(source, WHILEYMAP));
		construct(WHILEYMAP,freeSlot,bytecodes);
		bytecodes.add(new Bytecode.Store(target, WHILEYMAP));
										
		JvmType.Function ftype = new JvmType.Function(JAVA_UTIL_SET);
		bytecodes.add(new Bytecode.Invoke(WHILEYMAP, "keySet",
				ftype, Bytecode.VIRTUAL));
		ftype = new JvmType.Function(JAVA_UTIL_ITERATOR);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_SET, "iterator",
				ftype, Bytecode.INTERFACE));
		bytecodes.add(new Bytecode.Store(iter,
				JAVA_UTIL_ITERATOR));
					
		bytecodes.add(new Bytecode.Label(loopLabel));
		ftype = new JvmType.Function(T_BOOL);
		bytecodes.add(new Bytecode.Load(iter,JAVA_UTIL_ITERATOR));
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "hasNext",
				ftype, Bytecode.INTERFACE));
		bytecodes.add(new Bytecode.If(Bytecode.If.EQ, exitLabel));
		
		bytecodes.add(new Bytecode.Load(target,WHILEYMAP));
		bytecodes.add(new Bytecode.Load(iter,JAVA_UTIL_ITERATOR));
		ftype = new JvmType.Function(JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "next",
				ftype, Bytecode.INTERFACE));							
		addReadConversion(fromType.key(),bytecodes);
		bytecodes.add(new Bytecode.Dup(convertType(fromType.key())));		
		addCoercion(fromType.key(), toType.key(), freeSlot,
				constants, bytecodes);		
		addWriteConversion(toType.key(),bytecodes);
		bytecodes.add(new Bytecode.Swap());		
		bytecodes.add(new Bytecode.Load(source,WHILEYMAP));
		bytecodes.add(new Bytecode.Swap());
		ftype = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYMAP, "get",
				ftype, Bytecode.VIRTUAL));
		addReadConversion(fromType.value(),bytecodes);
		addCoercion(fromType.value(), toType.value(), freeSlot,
				constants, bytecodes);
		addWriteConversion(toType.value(),bytecodes);		
		ftype = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);		
		bytecodes.add(new Bytecode.Invoke(WHILEYMAP, "put",
				ftype, Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(target,WHILEYMAP));
	}
	
	protected void buildCoercion(Type.Set fromType, Type.Dictionary toType, 
			int freeSlot, HashMap<Constant, Integer> constants,
			ArrayList<Bytecode> bytecodes) {
		// this case can only happen in one situation --- when the set is empty.
		
		if (fromType.element() != Type.T_VOID) {
			throw new RuntimeException("invalid coercion encountered: "
					+ fromType + " => " + toType);
		}

		bytecodes.add(new Bytecode.Pop(WHILEYSET));
		construct(WHILEYMAP, freeSlot, bytecodes);					
	}
	
	protected void buildCoercion(Type.List fromType, Type.Set toType,
			int freeSlot, HashMap<Constant,Integer> constants,			
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
		addCoercion(fromType.element(), toType.element(), freeSlot,
				constants, bytecodes);			
		ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "add",
				ftype, Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(T_BOOL));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(tmp,WHILEYSET));
	}
	
	protected void buildCoercion(Type.Set fromType, Type.Set toType,
			int freeSlot, HashMap<Constant,Integer> constants,
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
		addCoercion(fromType.element(), toType.element(), freeSlot,
				constants, bytecodes);			
		ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "add",
				ftype, Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(T_BOOL));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(tmp,WHILEYSET));
	}
	
	public void buildCoercion(Type.Record fromType, Type.Record toType, 
			int freeSlot, HashMap<Constant,Integer> constants,
			ArrayList<Bytecode> bytecodes) {		
		int oldSlot = freeSlot++;
		int newSlot = freeSlot++;		
		bytecodes.add(new Bytecode.Store(oldSlot,WHILEYRECORD));
		construct(WHILEYRECORD,freeSlot,bytecodes);
		bytecodes.add(new Bytecode.Store(newSlot,WHILEYRECORD));
		Map<String,Type> toFields = toType.fields();
		Map<String,Type> fromFields = fromType.fields();
		for(String key : toFields.keySet()) {
			Type to = toFields.get(key);
			Type from = fromFields.get(key);				
			bytecodes.add(new Bytecode.Load(newSlot,WHILEYRECORD));
			bytecodes.add(new Bytecode.LoadConst(key));
			bytecodes.add(new Bytecode.Load(oldSlot,WHILEYRECORD));
			bytecodes.add(new Bytecode.LoadConst(key));
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);			
			bytecodes.add(new Bytecode.Invoke(WHILEYRECORD,"get",ftype,Bytecode.VIRTUAL));								
			// TODO: in cases when the read conversion is a no-op, we can do
			// better here.
			addReadConversion(from,bytecodes);							
			addCoercion(from,to,freeSlot,constants,bytecodes);
			addWriteConversion(from,bytecodes);
			ftype = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);			
			bytecodes.add(new Bytecode.Invoke(WHILEYRECORD,"put",ftype,Bytecode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));			
		}
		bytecodes.add(new Bytecode.Load(newSlot,WHILEYRECORD));		
	}
	
	public void buildCoercion(Type.Union from, Type to, 
			int freeSlot, HashMap<Constant,Integer> constants,
			ArrayList<Bytecode> bytecodes) {	
		
		String exitLabel = freshLabel();
		List<Type> bounds = new ArrayList<Type>(from.bounds());
		ArrayList<String> labels = new ArrayList<String>();				
		
		// basically, we're building a big dispatch table. I think there's no
		// question that this could be more efficient in some cases.
		for(int i=0;i!=bounds.size();++i) {
			Type bound = bounds.get(i);
			if((i+1) == bounds.size()) {
				addReadConversion(bound,bytecodes);
				addCoercion(bound,to,freeSlot,constants,bytecodes);
				bytecodes.add(new Bytecode.Goto(exitLabel));
			} else {
				String label = freshLabel();
				labels.add(label);
				bytecodes.add(new Bytecode.Dup(convertType(from)));				
				translateTypeTest(label,from,bound,bytecodes,constants);				
			}
		}
		
		for(int i=0;i<labels.size();++i) {
			String label = labels.get(i);
			Type bound = bounds.get(i);
			bytecodes.add(new Bytecode.Label(label));
			addReadConversion(bound,bytecodes);
			addCoercion(bound,to,freeSlot,constants,bytecodes);
			bytecodes.add(new Bytecode.Goto(exitLabel));
		}
		
		bytecodes.add(new Bytecode.Label(exitLabel));
	}
	
	public void buildCoercion(Type from, Type.Union to, 
			int freeSlot, HashMap<Constant,Integer> constants,
			ArrayList<Bytecode> bytecodes) {	
		Type.Union t2 = (Type.Union) to;

		// First, check for identical type (i.e. no coercion necessary)
		for (Type b : t2.bounds()) {
			if (from.equals(b)) {
				// nothing to do
				return;
			}
		}

		// Second, check for single non-coercive match
		for (Type b : t2.bounds()) {
			if (Type.isSubtype(b, from)) {
				buildCoercion(from,b,freeSlot,constants,bytecodes);
				return;
			}
		}

		// Third, test for single coercive match
		for (Type b : t2.bounds()) {
			if (Type.isImplicitCoerciveSubtype(b, from)) {
				buildCoercion(from,b,freeSlot,constants,bytecodes);
				return;
			}
		}
		
		// I don't think we should be able to get here!
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
		} else if(et instanceof Type.Byte) {
			bytecodes.add(new Bytecode.CheckCast(JAVA_LANG_BYTE));
			JvmType.Function ftype = new JvmType.Function(T_BYTE);
			bytecodes.add(new Bytecode.Invoke(JAVA_LANG_BYTE,
					"byteValue", ftype, Bytecode.VIRTUAL));
		} else if(et instanceof Type.Char) {
			bytecodes.add(new Bytecode.CheckCast(JAVA_LANG_CHARACTER));
			JvmType.Function ftype = new JvmType.Function(T_CHAR);
			bytecodes.add(new Bytecode.Invoke(JAVA_LANG_CHARACTER,
					"charValue", ftype, Bytecode.VIRTUAL));
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
		} else if(et instanceof Type.Byte) {
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_BYTE,
					T_BYTE);
			bytecodes.add(new Bytecode.Invoke(JAVA_LANG_BYTE, "valueOf", ftype,
					Bytecode.STATIC));
		} else if(et instanceof Type.Char) {
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_CHARACTER,
					T_CHAR);
			bytecodes.add(new Bytecode.Invoke(JAVA_LANG_CHARACTER, "valueOf", ftype,
					Bytecode.STATIC));
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
	 * Return true if this type is, or maybe reference counted.
	 * 
	 * @param t
	 * @return
	 */
	public static boolean isRefCounted(Type t) {
		if (t instanceof Type.Union) {
			Type.Union n = (Type.Union) t;
			for (Type b : n.bounds()) {
				if (isRefCounted(b)) {
					return true;
				}
			}
			return false;
		} else {
			// FIXME: what about negations?
			return t instanceof Type.Any || t instanceof Type.List
					|| t instanceof Type.Tuple || t instanceof Type.Set
					|| t instanceof Type.Dictionary || t instanceof Type.Record;
		}
	}

	/**
	 * Add bytecodes for incrementing the reference count.
	 * 
	 * @param type
	 * @param bytecodes
	 */
	public static void addIncRefs(Type type, ArrayList<Bytecode> bytecodes) {
		if(isRefCounted(type)){			
			JvmType jtype = convertType(type);
			JvmType.Function ftype = new JvmType.Function(jtype,jtype);			
			bytecodes.add(new Bytecode.Invoke(WHILEYUTIL,"incRefs",ftype,Bytecode.STATIC));
		}
	}
	
	public static void addIncRefs(Type.List type, ArrayList<Bytecode> bytecodes) {				
		JvmType.Function ftype = new JvmType.Function(WHILEYLIST,WHILEYLIST);			
		bytecodes.add(new Bytecode.Invoke(WHILEYUTIL,"incRefs",ftype,Bytecode.STATIC));
	}
	
	public static void addIncRefs(Type.Record type, ArrayList<Bytecode> bytecodes) {				
		JvmType.Function ftype = new JvmType.Function(WHILEYRECORD,WHILEYRECORD);			
		bytecodes.add(new Bytecode.Invoke(WHILEYUTIL,"incRefs",ftype,Bytecode.STATIC));
	}
	
	public static void addIncRefs(Type.Dictionary type, ArrayList<Bytecode> bytecodes) {				
		JvmType.Function ftype = new JvmType.Function(WHILEYMAP,WHILEYMAP);			
		bytecodes.add(new Bytecode.Invoke(WHILEYUTIL,"incRefs",ftype,Bytecode.STATIC));
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
		
	public final static Type.Reference WHILEY_SYSTEM_OUT_T = (Type.Reference) Type
			.Reference(Type.T_ANY);

	public final static Type WHILEY_SYSTEM_T = Type.Record(false,
			new HashMap() {
				{
					put("out", WHILEY_SYSTEM_OUT_T);
					put("args", Type.List(Type.T_STRING,false));
				}
			});
		
	public final static JvmType.Clazz WHILEYUTIL = new JvmType.Clazz("wyjc.runtime","Util");
	public final static JvmType.Clazz WHILEYLIST = new JvmType.Clazz("wyjc.runtime","List");	
	public final static JvmType.Clazz WHILEYSET = new JvmType.Clazz("wyjc.runtime","Set");
	public final static JvmType.Clazz WHILEYTUPLE = new JvmType.Clazz("wyjc.runtime","Tuple");
	public final static JvmType.Clazz WHILEYCOLLECTION = new JvmType.Clazz("wyjc.runtime","Collection");
	public final static JvmType.Clazz WHILEYTYPE = new JvmType.Clazz("wyjc.runtime","Type");	
	public final static JvmType.Clazz WHILEYMAP = new JvmType.Clazz("wyjc.runtime","Dictionary");
	public final static JvmType.Clazz WHILEYRECORD = new JvmType.Clazz("wyjc.runtime","Record");	
	public final static JvmType.Clazz WHILEYPROCESS = new JvmType.Clazz(
			"wyjc.runtime", "Actor");
	public final static JvmType.Clazz WHILEYSTRAND = new JvmType.Clazz(
			"wyjc.runtime.concurrency", "Strand");	
	public final static JvmType.Clazz WHILEYMESSAGER = new JvmType.Clazz(
			"wyjc.runtime.concurrency", "Messager");
	public final static JvmType.Clazz WHILEYFUTURE = new JvmType.Clazz(
			"wyjc.runtime.concurrency", "Messager$MessageFuture");
	public final static JvmType.Clazz WHILEYSCHEDULER = new JvmType.Clazz(
			"wyjc.runtime.concurrency", "Scheduler");
	public final static JvmType.Clazz WHILEYEXCEPTION = new JvmType.Clazz("wyjc.runtime","Exception");	
	public final static JvmType.Clazz BIG_INTEGER = new JvmType.Clazz("java.math","BigInteger");
	public final static JvmType.Clazz BIG_RATIONAL = new JvmType.Clazz("wyjc.runtime","BigRational");
	private static final JvmType.Clazz JAVA_LANG_CHARACTER = new JvmType.Clazz("java.lang","Character");
	private static final JvmType.Clazz JAVA_LANG_SYSTEM = new JvmType.Clazz("java.lang","System");
	private static final JvmType.Array JAVA_LANG_OBJECT_ARRAY = new JvmType.Array(JAVA_LANG_OBJECT);
	private static final JvmType.Clazz JAVA_UTIL_LIST = new JvmType.Clazz("java.util","List");
	private static final JvmType.Clazz JAVA_UTIL_SET = new JvmType.Clazz("java.util","Set");
	private static final JvmType.Clazz JAVA_LANG_REFLECT_METHOD = new JvmType.Clazz("java.lang.reflect","Method");
	private static final JvmType.Clazz JAVA_IO_PRINTSTREAM = new JvmType.Clazz("java.io","PrintStream");
	private static final JvmType.Clazz JAVA_LANG_RUNTIMEEXCEPTION = new JvmType.Clazz("java.lang","RuntimeException");
	private static final JvmType.Clazz JAVA_LANG_ASSERTIONERROR = new JvmType.Clazz("java.lang","AssertionError");
	private static final JvmType.Clazz JAVA_UTIL_COLLECTION = new JvmType.Clazz("java.util","Collection");	
	
	public JvmType.Function convertFunType(Type.FunctionOrMethodOrMessage ft) {		
		ArrayList<JvmType> paramTypes = new ArrayList<JvmType>();
		if(ft instanceof Type.Message) {
			Type.Message mt = (Type.Message)ft; 
			if(mt.receiver() != null) {
				paramTypes.add(convertType(mt.receiver()));
			}
		}
		for(Type pt : ft.params()) {
			paramTypes.add(convertType(pt));
		}
		JvmType rt = convertType(ft.ret());			
		return new JvmType.Function(rt,paramTypes);		
	}
	
	public static JvmType convertType(Type t) {
		if(t == Type.T_VOID) {
			return T_VOID;
		} else if(t == Type.T_ANY) {
			return JAVA_LANG_OBJECT;
		} else if(t == Type.T_NULL) {
			return JAVA_LANG_OBJECT;
		} else if(t instanceof Type.Bool) {
			return T_BOOL;
		} else if(t instanceof Type.Byte) {
			return T_BYTE;
		} else if(t instanceof Type.Char) {
			return T_CHAR;
		} else if(t instanceof Type.Int) {
			return BIG_INTEGER;
		} else if(t instanceof Type.Real) {
			return BIG_RATIONAL;
		} else if(t instanceof Type.Meta) {
			return WHILEYTYPE;
		} else if(t instanceof Type.Strung) {
			return JAVA_LANG_STRING;
		} else if(t instanceof Type.EffectiveList) {
			return WHILEYLIST;
		} else if(t instanceof Type.EffectiveSet) {
			return WHILEYSET;
		} else if(t instanceof Type.EffectiveDictionary) {
			return WHILEYMAP;
		} else if(t instanceof Type.EffectiveRecord) {
			return WHILEYRECORD;
		} else if(t instanceof Type.EffectiveTuple) {
			return WHILEYTUPLE;
		} else if(t instanceof Type.Reference) {
			return WHILEYPROCESS;
		} else if(t instanceof Type.Negation) {
			// can we do any better?
			return JAVA_LANG_OBJECT;
		} else if(t instanceof Type.Union) {			
			return JAVA_LANG_OBJECT;			
		} else if(t instanceof Type.Meta) {							
			return JAVA_LANG_OBJECT;			
		} else if(t instanceof Type.FunctionOrMethodOrMessage) {						
			return JAVA_LANG_REFLECT_METHOD;
		}else {
			throw new RuntimeException("unknown type encountered: " + t);
		}		
	}	
			
	protected int label = 0;
	protected String freshLabel() {
		return "cfblab" + label++;
	}	
	
	public static String nameMangle(String name, Type.FunctionOrMethodOrMessage ft) {				
		try {			
			return name + "$" + typeMangle(ft);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
		
	public static String typeMangle(Type.FunctionOrMethodOrMessage ft) throws IOException {		
		JavaIdentifierOutputStream jout = new JavaIdentifierOutputStream();
		BinaryOutputStream binout = new BinaryOutputStream(jout);		
		Type.BinaryWriter tm = new Type.BinaryWriter(binout);
		tm.write(ft);
		binout.close(); // force flush	
		return jout.toString();		
	}	
	
	/**
	 * A constant is some kind of auxillary functionality used in generated code, which can be reused at multiple sites.  This includes value constants, and coercion functions. 
	 * @author David J. Pearce
	 *
	 */
	public abstract static class Constant {}
	public static final class ValueConst extends Constant {
		public final Value value;
		public ValueConst(Value v) {
			value = v;
		}
		public boolean equals(Object o) {
			if(o instanceof ValueConst) {
				ValueConst vc = (ValueConst) o;
				return value.equals(vc.value);
			}
			return false;
		}
		public int hashCode() {
			return value.hashCode();
		}
		public static int get(Value value, HashMap<Constant,Integer> constants) {
			ValueConst vc = new ValueConst(value);
			Integer r = constants.get(vc);
			if(r != null) {
				return r;
			} else {
				int x = constants.size();
				constants.put(vc, x);
				return x;
			}			
		}
	}
	public static final class Coercion extends Constant {
		public final Type from;
		public final Type to;
		public Coercion(Type from, Type to) {
			this.from = from;
			this.to = to;
		}
		public boolean equals(Object o) {
			if(o instanceof Coercion) {
				Coercion c = (Coercion) o;
				return from.equals(c.from) && to.equals(c.to);
			}
			return false;
		}
		public int hashCode() {
			return from.hashCode() + to.hashCode();
		}
		public static int get(Type from, Type to, HashMap<Constant,Integer> constants) {
			Coercion vc = new Coercion(from,to);
			Integer r = constants.get(vc);
			if(r != null) {
				return r;
			} else {
				int x = constants.size();
				constants.put(vc, x);
				return x;
			}			
		}
	}
	
	public static class UnresolvedHandler {
		public String start;
		public String end;
		public String target;
		public JvmType.Clazz exception;

		public UnresolvedHandler(String start, String end, String target,
				JvmType.Clazz exception) {
			this.start = start;
			this.end = end;
			this.target = target;
			this.exception = exception;
		}
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

