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

package wyjc;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import wybs.lang.Build;
import wybs.lang.Builder;
import wycc.lang.Attribute;
import wycc.lang.NameID;
import wycc.lang.SyntaxError;
import wycc.util.Logger;
import wycc.util.Pair;
import static wycc.lang.SyntaxError.*;
import wyautl.util.BigRational;
import wyfs.io.BinaryOutputStream;
import wyfs.lang.Path;
import wyil.lang.*;
import wyil.lang.Constant;
import wyjc.util.WyjcBuildTask;
import jasm.attributes.Code.Handler;
import jasm.attributes.LineNumberTable;
import jasm.attributes.SourceFile;
import jasm.lang.*;
import jasm.lang.Modifier;
//import jasm.lang.Constant;
import jasm.verifier.*;
import wyrl.io.JavaIdentifierOutputStream;
import static jasm.lang.JvmTypes.*;

/**
 * Responsible for converting WYIL files into Java Classfiles. This is a
 * relatively straightforward process, given the all the hard work has already
 * been done by the Whiley-2-Wyil builder.
 * 
 * @author David J. Pearce
 * 
 */
public class Wyil2JavaBuilder implements Builder {
	private static int CLASS_VERSION = 49;
	
	/**
	 * The master project for identifying all resources available to the
	 * builder. This includes all modules declared in the project being verified
	 * and/or defined in external resources (e.g. jar files).
	 */
	protected final Build.Project project;

	/**
	 * For logging information.
	 */
	private Logger logger = Logger.NULL;

	protected String filename;
	protected JvmType.Clazz owner;
	
	public Wyil2JavaBuilder(Build.Project project) {
		this.project = project;
	}
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	public Build.Project project() {
		return project;
	}
		
	public Set<Path.Entry<?>> build(Collection<Pair<Path.Entry<?>,Path.Root>> delta) throws IOException {
		
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();
		long memory = runtime.freeMemory();
	
		// ========================================================================
		// Translate files
		// ========================================================================
		HashSet<Path.Entry<?>> generatedFiles = new HashSet<Path.Entry<?>>();
		
		for(Pair<Path.Entry<?>,Path.Root> p : delta) {
			Path.Root dst = p.second();
			Path.Entry<WyilFile> sf = (Path.Entry<WyilFile>) p.first();
			Path.Entry<ClassFile> df = dst.create(sf.id(), WyjcBuildTask.ContentType);
			generatedFiles.add(df);
						
			// Translate WyilFile into JVM ClassFile
			ArrayList<ClassFile> lambdas = new ArrayList<ClassFile>();
			ClassFile contents = build(sf.read(), lambdas);

			// FIXME: deadCode elimination is currently unsafe because the
			// LineNumberTable and Exceptions attributes do not deal with rewrites
			// properly.

			// eliminate any dead code that was introduced.		
			// new DeadCodeElimination().apply(file);			

			// Write class file into its destination
			df.write(contents);

			// Finally, write out any lambda classes created to support the
			// main class. This is necessary because every occurrence of a
			// lambda expression in the WyilFile generates an inner class
			// responsible for calling the given function.
			Path.ID parent = df.id();
			Path.ID pkg = parent.subpath(0, parent.size() - 1);
			for (int i = 0; i != lambdas.size(); ++i) {
				Path.ID id = pkg.append(parent.last() + "$" + i);
				Path.Entry<ClassFile> lf = dst.create(id, WyjcBuildTask.ContentType);
				lf.write(lambdas.get(i));
				generatedFiles.add(lf);
			}
		}

		// ========================================================================
		// Done
		// ========================================================================

		long endTime = System.currentTimeMillis();
		logger.logTimedMessage("Wyil => Java: compiled " + delta.size() + " file(s)",
				endTime - start, memory - runtime.freeMemory());
		
		return generatedFiles;
	}	
	
	private ClassFile build(WyilFile module,
			ArrayList<ClassFile> lambdas) {		
		owner = new JvmType.Clazz(module.id().parent().toString().replace('.','/'),
				module.id().last());
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);
		modifiers.add(Modifier.ACC_FINAL);
		ClassFile cf = new ClassFile(CLASS_VERSION, owner, JAVA_LANG_OBJECT,
				new ArrayList<JvmType.Clazz>(), modifiers);
	
		this.filename = module.filename();
		
		if(filename != null) {
			cf.attributes().add(new SourceFile(filename));
		}
		
		boolean addMainLauncher = false;		
								
		HashMap<JvmConstant,Integer> constants = new HashMap<JvmConstant,Integer>();
		for(WyilFile.FunctionOrMethodDeclaration method : module.functionOrMethods()) {				
			if(method.name().equals("main")) { 
				addMainLauncher = true;
			}			
			cf.methods().addAll(build(method, constants, lambdas));			
		}		
			
		buildConstants(constants,lambdas,cf);		
				
		if(addMainLauncher) {
			cf.methods().add(buildMainLauncher(owner));
		}
		
		return cf;
	}	
	
	private void buildConstants(HashMap<JvmConstant, Integer> constants,
			ArrayList<ClassFile> lambdas, ClassFile cf) {						
		buildCoercions(constants,cf);
		buildValues(constants,lambdas,cf);
	}
	
	private void buildCoercions(HashMap<JvmConstant,Integer> constants, ClassFile cf) {
		HashSet<JvmConstant> done = new HashSet<JvmConstant>();
		HashMap<JvmConstant,Integer> original = constants;
		// this could be a little more efficient I think!!		
		while(done.size() != constants.size()) {
			// We have to clone the constants map, since it may be expanded as a
			// result of buildCoercion(). This will occur if the coercion
			// constructed requires a helper coercion that was not in the
			// original constants map.  
			HashMap<JvmConstant,Integer> nconstants = new HashMap<JvmConstant,Integer>(constants);		
			for(Map.Entry<JvmConstant,Integer> entry : constants.entrySet()) {
				JvmConstant e = entry.getKey();
				if(!done.contains(e) && e instanceof JvmCoercion) {
					JvmCoercion c = (JvmCoercion) e;
					buildCoercion(c.from,c.to,entry.getValue(),nconstants,cf);
				} 
				done.add(e);
			}
			constants = nconstants;
		}
		original.putAll(constants);
	}
	
	private void buildValues(HashMap<JvmConstant, Integer> constants,
			ArrayList<ClassFile> lambdas, ClassFile cf) {
		int nvalues = 0;
		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
		
		for(Map.Entry<JvmConstant,Integer> entry : constants.entrySet()) {			
			JvmConstant c = entry.getKey();
			if(c instanceof JvmValue) {
				nvalues++;
				Constant constant = ((JvmValue)c).value;
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
				translate(constant,0,lambdas,bytecodes);
				bytecodes.add(new Bytecode.PutField(owner, name, type, Bytecode.FieldMode.STATIC));
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
			jasm.attributes.Code code = new jasm.attributes.Code(bytecodes,new ArrayList(),clinit);
			clinit.attributes().add(code);				
		}
	}
		
	private ClassFile.Method buildMainLauncher(JvmType.Clazz owner) {
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);
		modifiers.add(Modifier.ACC_STATIC);
		modifiers.add(Modifier.ACC_SYNTHETIC);
		JvmType.Function ft1 = new JvmType.Function(T_VOID, new JvmType.Array(
				JAVA_LANG_STRING));
		ClassFile.Method cm = new ClassFile.Method("main", ft1, modifiers);
		JvmType.Array strArr = new JvmType.Array(JAVA_LANG_STRING);
		ArrayList<Bytecode> codes = new ArrayList<Bytecode>();
		ft1 = new JvmType.Function(WHILEYRECORD, new JvmType.Array(
				JAVA_LANG_STRING));
		codes.add(new Bytecode.Load(0, strArr));
		codes.add(new Bytecode.Invoke(WHILEYUTIL, "systemConsole", ft1,
				Bytecode.InvokeMode.STATIC));
		Type.Method wyft = Type.Method(Type.T_VOID, Type.T_VOID,
				WHILEY_SYSTEM_T);
		JvmType.Function ft3 = convertFunType(wyft);
		codes.add(new Bytecode.Invoke(owner, nameMangle("main", wyft), ft3,
				Bytecode.InvokeMode.STATIC));	
		codes.add(new Bytecode.Return(null));

		jasm.attributes.Code code = new jasm.attributes.Code(codes,
				new ArrayList(), cm);
		cm.attributes().add(code);

		return cm;
	}
	
	private List<ClassFile.Method> build(
			WyilFile.FunctionOrMethodDeclaration method,
			HashMap<JvmConstant, Integer> constants,
			ArrayList<ClassFile> lambdas) {
		ArrayList<ClassFile.Method> methods = new ArrayList<ClassFile.Method>();
		int num = 1;
		for (WyilFile.Case c : method.cases()) {
			if (method.hasModifier(wyil.lang.Modifier.NATIVE)) {
				methods.add(buildNativeOrExport(c, method, constants));
			} else {
				if (method.hasModifier(wyil.lang.Modifier.EXPORT)) {
					methods.add(buildNativeOrExport(c, method, constants));
				}
				methods.add(build(num++, c, method, constants, lambdas));
			}
		}
		return methods;
	}
	
	private ClassFile.Method build(int caseNum, WyilFile.Case mcase,
			WyilFile.FunctionOrMethodDeclaration method,
			HashMap<JvmConstant, Integer> constants,
			ArrayList<ClassFile> lambdas) {

		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		if(method.hasModifier(wyil.lang.Modifier.PUBLIC)) {
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
		codes = translate(mcase,constants,lambdas,handlers,lineNumbers);		
		jasm.attributes.Code code = new jasm.attributes.Code(codes,handlers,cm);
		if(!lineNumbers.isEmpty()) {
			code.attributes().add(new LineNumberTable(lineNumbers));
		}						
		cm.attributes().add(code);
		
		return cm;
	}
	
	private ClassFile.Method buildNativeOrExport(WyilFile.Case mcase,
			WyilFile.FunctionOrMethodDeclaration method, HashMap<JvmConstant,Integer> constants) {
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		if (method.hasModifier(wyil.lang.Modifier.PUBLIC)
				|| method.hasModifier(wyil.lang.Modifier.PUBLIC)) {
			modifiers.add(Modifier.ACC_PUBLIC);
		}
		modifiers.add(Modifier.ACC_STATIC);					
		JvmType.Function ft = convertFunType(method.type());		
		
		String name = method.name();
		if(method.hasModifier(wyil.lang.Modifier.NATIVE)) {
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
		jasm.attributes.Code code = new jasm.attributes.Code(codes,handlers,cm);
							
		cm.attributes().add(code);
		
		return cm;
	}
	
	private ArrayList<Bytecode> translateNativeOrExport(WyilFile.FunctionOrMethodDeclaration method) {

		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
		Type.FunctionOrMethod ft = method.type();
		int slot = 0;

		for (Type param : ft.params()) {
			bytecodes.add(new Bytecode.Load(slot++, convertType(param)));
		}

		if (method.hasModifier(wyil.lang.Modifier.NATIVE)) {
			JvmType.Clazz redirect = new JvmType.Clazz(owner.pkg(), owner
					.components().get(0).first(), "native");
			bytecodes.add(new Bytecode.Invoke(redirect, method.name(),
					convertFunType(ft), Bytecode.InvokeMode.STATIC));
		} else {
			JvmType.Clazz redirect = new JvmType.Clazz(owner.pkg(), owner
					.components().get(0).first());
			bytecodes.add(new Bytecode.Invoke(redirect, nameMangle(
					method.name(), method.type()), convertFunType(ft),
					Bytecode.InvokeMode.STATIC));
		}

		if (ft.ret() == Type.T_VOID) {
			bytecodes.add(new Bytecode.Return(null));
		} else {
			bytecodes.add(new Bytecode.Return(convertType(ft.ret())));
		}

		return bytecodes;
	}
	
	private ArrayList<Bytecode> translate(WyilFile.Case mcase,
			HashMap<JvmConstant, Integer> constants,
			ArrayList<ClassFile> lambdas,
			ArrayList<Handler> handlers,
			ArrayList<LineNumberTable.Entry> lineNumbers) {
		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
		Code.Block block = mcase.body();
		translate(block, block.numSlots(), constants, lambdas, handlers,
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
	private void translate(Code.Block blk, int freeSlot,
			HashMap<JvmConstant, Integer> constants,
			ArrayList<ClassFile> lambdas,
			ArrayList<Handler> handlers,
			ArrayList<LineNumberTable.Entry> lineNumbers,
			ArrayList<Bytecode> bytecodes) {
		
		ArrayList<UnresolvedHandler> unresolvedHandlers = new ArrayList<UnresolvedHandler>();
		for (Code.Block.Entry s : blk) {
			Attribute.Source loc = s.attribute(Attribute.Source.class);
			if(loc != null) {				
				lineNumbers.add(new LineNumberTable.Entry(bytecodes.size(),loc.line));
			}
			freeSlot = translate(s, freeSlot, constants, lambdas,
					unresolvedHandlers, bytecodes);
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
	
	private int translate(Code.Block.Entry entry, int freeSlot,
			HashMap<JvmConstant, Integer> constants,
			ArrayList<ClassFile> lambdas,
			ArrayList<UnresolvedHandler> handlers, 
			ArrayList<Bytecode> bytecodes) {
		try {
			Code code = entry.code;
			if(code instanceof Codes.BinaryOperator) {
				 translate((Codes.BinaryOperator)code,entry,freeSlot,bytecodes);
			} else if(code instanceof Codes.Convert) {
				 translate((Codes.Convert)code,freeSlot,constants,bytecodes);
			} else if(code instanceof Codes.Const) {
				translate((Codes.Const) code, freeSlot, constants, lambdas, bytecodes);
			} else if(code instanceof Codes.Debug) {
				 translate((Codes.Debug)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.LoopEnd) {
				 translate((Codes.LoopEnd)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.AssertOrAssume) {
				 translate((Codes.AssertOrAssume)code,entry,freeSlot,bytecodes);
			} else if(code instanceof Codes.Fail) {
				 translate((Codes.Fail)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.FieldLoad) {
				 translate((Codes.FieldLoad)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.ForAll) {
				 freeSlot = translate((Codes.ForAll)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.Goto) {
				 translate((Codes.Goto)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.If) {				
				translateIfGoto((Codes.If) code, entry, freeSlot, bytecodes);
			} else if(code instanceof Codes.IfIs) {
				translate((Codes.IfIs) code, entry, freeSlot, constants, bytecodes);
			} else if(code instanceof Codes.IndirectInvoke) {
				 translate((Codes.IndirectInvoke)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.Invoke) {
				 translate((Codes.Invoke)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.Invert) {
				 translate((Codes.Invert)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.Label) {
				translate((Codes.Label)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.ListOperator) {
				 translate((Codes.ListOperator)code,entry,freeSlot,bytecodes);
			} else if(code instanceof Codes.Lambda) {
				 translate((Codes.Lambda)code,freeSlot,lambdas,bytecodes);
			} else if(code instanceof Codes.LengthOf) {
				 translate((Codes.LengthOf)code,entry,freeSlot,bytecodes);
			} else if(code instanceof Codes.SubList) {
				 translate((Codes.SubList)code,entry,freeSlot,bytecodes);
			} else if(code instanceof Codes.IndexOf) {
				 translate((Codes.IndexOf)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.Assign) {
				 translate((Codes.Assign)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.Loop) {
				 translate((Codes.Loop)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.Move) {
				 translate((Codes.Move)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.Update) {
				 translate((Codes.Update)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.NewMap) {
				 translate((Codes.NewMap)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.NewList) {
				 translate((Codes.NewList)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.NewRecord) {
				 translate((Codes.NewRecord)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.NewSet) {
				 translate((Codes.NewSet)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.NewTuple) {
				 translate((Codes.NewTuple)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.UnaryOperator) {
				 translate((Codes.UnaryOperator)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.Dereference) {
				 translate((Codes.Dereference)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.Return) {
				 translate((Codes.Return)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.Nop) {
				// do nothing
			} else if(code instanceof Codes.SetOperator) {
				 translate((Codes.SetOperator)code,entry,freeSlot,bytecodes);
			} else if(code instanceof Codes.StringOperator) {
				 translate((Codes.StringOperator)code,entry,freeSlot,bytecodes);
			} else if(code instanceof Codes.SubString) {
				 translate((Codes.SubString)code,entry,freeSlot,bytecodes);
			} else if(code instanceof Codes.Switch) {
				 translate((Codes.Switch)code,entry,freeSlot,lambdas,bytecodes);
			} else if(code instanceof Codes.TryCatch) {
				 translate((Codes.TryCatch)code,entry,freeSlot,handlers,constants,bytecodes);
			} else if(code instanceof Codes.NewObject) {
				 translate((Codes.NewObject)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.Throw) {
				 translate((Codes.Throw)code,freeSlot,bytecodes);
			} else if(code instanceof Codes.TupleLoad) {
				 translate((Codes.TupleLoad)code,freeSlot,bytecodes);
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
	
	private void translate(Codes.Const c, int freeSlot, HashMap<JvmConstant,Integer> constants,	
			ArrayList<ClassFile> lambdas, ArrayList<Bytecode> bytecodes) {
		Constant constant = c.constant;
		JvmType jt = convertType(constant.type());
		
		if (constant instanceof Constant.Decimal || constant instanceof Constant.Bool
				|| constant instanceof Constant.Null || constant instanceof Constant.Byte) {
			translate(constant,freeSlot,lambdas,bytecodes);					
		} else {
			int id = JvmValue.get(constant,constants);			
			String name = "constant$" + id;
			bytecodes.add(new Bytecode.GetField(owner, name, jt, Bytecode.FieldMode.STATIC));
			// the following is necessary to prevent in-place updates of our
			// constants!
			addIncRefs(constant.type(),bytecodes);
		}		
		bytecodes.add(new Bytecode.Store(c.target(), jt));
	}
	
	private void translate(Codes.Convert c, int freeSlot,
			HashMap<JvmConstant, Integer> constants, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Load(c.operand(0), convertType(c.type())));
		addCoercion(c.type(), c.result, freeSlot, constants, bytecodes);
		bytecodes.add(new Bytecode.Store(c.target(), convertType(c.result)));
	}
	
	private void translate(Codes.Update code, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Load(code.target(), convertType(code.type())));
		translateUpdate(code.iterator(), code, bytecodes);
		bytecodes.add(new Bytecode.Store(code.target(),
				convertType(code.afterType)));
	}

	/**
	 * Iterate down the chain of updates reading values out, updating them, and
	 * writing them back.
	 * 
	 * @param iterator
	 *            --- update iterator.
	 * @param rhsOperand
	 *            --- register operand of right-hand side
	 * @param bytecodes
	 *            --- list of bytecodes to append to.
	 */
	private void translateUpdate(Iterator<Codes.LVal> iterator, Codes.Update code,
			ArrayList<Bytecode> bytecodes) {
		Codes.LVal lv = iterator.next();
		if(lv instanceof Codes.ListLVal) {
			Codes.ListLVal l = (Codes.ListLVal) lv;
			if(iterator.hasNext()) {
				// In this case, we're partially updating the element at a
				// given position. 
				bytecodes.add(new Bytecode.Dup(WHILEYLIST));											
				bytecodes.add(new Bytecode.Load(l.indexOperand,WHILEYINT));				
				JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
						WHILEYLIST,WHILEYINT);
				bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "internal_get", ftype,
						Bytecode.InvokeMode.STATIC));				
				addReadConversion(l.rawType().element(),bytecodes);
				translateUpdate(iterator,code,bytecodes);		
				bytecodes.add(new Bytecode.Load(l.indexOperand,WHILEYINT));				
				bytecodes.add(new Bytecode.Swap());
			} else {
				bytecodes.add(new Bytecode.Load(l.indexOperand,WHILEYINT));
				bytecodes.add(new Bytecode.Load(code.result(), convertType(l
						.rawType().element())));	
				addWriteConversion(code.rhs(),bytecodes);
			}

			JvmType.Function ftype = new JvmType.Function(WHILEYLIST,
					WHILEYLIST,WHILEYINT,JAVA_LANG_OBJECT);			
			bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "set", ftype,
					Bytecode.InvokeMode.STATIC));	

		} else if(lv instanceof Codes.StringLVal) {
			Codes.StringLVal l = (Codes.StringLVal) lv;
			// assert: level must be zero here
			bytecodes.add(new Bytecode.Load(l.indexOperand, WHILEYINT));
			bytecodes.add(new Bytecode.Load(code.result(), T_CHAR));

			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_STRING,
					JAVA_LANG_STRING, WHILEYINT, T_CHAR);
			bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "set", ftype,
					Bytecode.InvokeMode.STATIC));
		} else if(lv instanceof Codes.MapLVal) {
			Codes.MapLVal l = (Codes.MapLVal) lv;
			JvmType keyType = convertType(l.rawType().key());
			JvmType valueType = convertType(l.rawType().value());
			if(iterator.hasNext()) {
				// In this case, we're partially updating the element at a
				// given position. 				
				bytecodes.add(new Bytecode.Dup(WHILEYMAP));											
				bytecodes.add(new Bytecode.Load(l.keyOperand,keyType));				
				addWriteConversion(l.rawType().key(),bytecodes);				
				JvmType.Function ftype = new JvmType.Function(
						JAVA_LANG_OBJECT, WHILEYMAP, JAVA_LANG_OBJECT);
				bytecodes.add(new Bytecode.Invoke(WHILEYMAP, "internal_get", ftype,
					Bytecode.InvokeMode.STATIC));				
				addReadConversion(l.rawType().value(),bytecodes);
				translateUpdate(iterator,code,bytecodes);
				bytecodes.add(new Bytecode.Load(l.keyOperand,keyType));
				addWriteConversion(l.rawType().key(),bytecodes);		
				bytecodes.add(new Bytecode.Swap());
			} else {				
				bytecodes.add(new Bytecode.Load(l.keyOperand,keyType));	
				addWriteConversion(l.rawType().key(),bytecodes);		
				bytecodes.add(new Bytecode.Load(code.result(), valueType));	
				addWriteConversion(l.rawType().value(),bytecodes);
			}
						
			JvmType.Function ftype = new JvmType.Function(WHILEYMAP,
					WHILEYMAP,JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);						
			bytecodes.add(new Bytecode.Invoke(WHILEYMAP, "put", ftype,
					Bytecode.InvokeMode.STATIC));			
				
		} else if(lv instanceof Codes.RecordLVal) {
			Codes.RecordLVal l = (Codes.RecordLVal) lv;
			Type.EffectiveRecord type = l.rawType();
			
			if (iterator.hasNext()) {
				bytecodes.add(new Bytecode.Dup(WHILEYRECORD));
				bytecodes.add(new Bytecode.LoadConst(l.field));
				JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
						WHILEYRECORD, JAVA_LANG_STRING);
				bytecodes.add(new Bytecode.Invoke(WHILEYRECORD, "internal_get",
						ftype, Bytecode.InvokeMode.STATIC));
				addReadConversion(type.field(l.field), bytecodes);
				translateUpdate(iterator, code, bytecodes);
				bytecodes.add(new Bytecode.LoadConst(l.field));
				bytecodes.add(new Bytecode.Swap());
			} else {
				bytecodes.add(new Bytecode.LoadConst(l.field));
				bytecodes.add(new Bytecode.Load(code.result(), convertType(type
						.field(l.field))));
				addWriteConversion(type.field(l.field), bytecodes);
			}
			
			JvmType.Function ftype = new JvmType.Function(WHILEYRECORD,WHILEYRECORD,JAVA_LANG_STRING,JAVA_LANG_OBJECT);						
			bytecodes.add(new Bytecode.Invoke(WHILEYRECORD,"put",ftype,Bytecode.InvokeMode.STATIC));	
		} else {
			Codes.ReferenceLVal l = (Codes.ReferenceLVal) lv;
			bytecodes.add(new Bytecode.Dup(WHILEYOBJECT));
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT);
			bytecodes.add(new Bytecode.Invoke(WHILEYOBJECT, "state", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			addReadConversion(l.rawType().element(), bytecodes);
			translateUpdate(iterator, code, bytecodes);
			ftype = new JvmType.Function(WHILEYOBJECT, JAVA_LANG_OBJECT);
			bytecodes.add(new Bytecode.Invoke(WHILEYOBJECT, "setState", ftype,
					Bytecode.InvokeMode.VIRTUAL));
		}
	}
	
	private void translate(Codes.Return c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		if (c.type == Type.T_VOID) {
			bytecodes.add(new Bytecode.Return(null));
		} else {
			JvmType jt = convertType(c.type);
			bytecodes.add(new Bytecode.Load(c.operand,jt));
			bytecodes.add(new Bytecode.Return(jt));
		}
	}

	private void translate(Codes.Throw c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {			
		bytecodes.add(new Bytecode.New(WHILEYEXCEPTION));
		bytecodes.add(new Bytecode.Dup(WHILEYEXCEPTION));
		bytecodes.add(new Bytecode.Load(c.operand,convertType(c.type)));		
		JvmType.Function ftype = new JvmType.Function(T_VOID,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYEXCEPTION, "<init>", ftype,
				Bytecode.InvokeMode.SPECIAL));		
		bytecodes.add(new Bytecode.Throw());
	}
	
	private void translate(Codes.TupleLoad c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
				WHILEYTUPLE, T_INT);
		bytecodes.add(new Bytecode.Load(c.operand(0),convertType((Type) c.type())));
		bytecodes.add(new Bytecode.LoadConst(c.index));		
		bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE, "get", ftype,
				Bytecode.InvokeMode.STATIC));		
		addReadConversion(c.type().elements().get(c.index), bytecodes);		
		bytecodes.add(new Bytecode.Store(c.target(),convertType(c.type()
				.element(c.index))));
	}
	
	private void translate(Codes.Switch c, Code.Block.Entry entry, int freeSlot,
			ArrayList<ClassFile> lambdas, ArrayList<Bytecode> bytecodes) {

		ArrayList<jasm.util.Pair<Integer, String>> cases = new ArrayList();
		boolean canUseSwitchBytecode = true;
		for (Pair<Constant, String> p : c.branches) {
			// first, check whether the switch value is indeed an integer.
			Constant v = (Constant) p.first();
			if (!(v instanceof Constant.Integer)) {
				canUseSwitchBytecode = false;
				break;
			}
			// second, check whether integer value can fit into a Java int
			Constant.Integer vi = (Constant.Integer) v;
			int iv = vi.value.intValue();
			if (!BigInteger.valueOf(iv).equals(vi.value)) {
				canUseSwitchBytecode = false;
				break;
			}
			// ok, we're all good so far
			cases.add(new jasm.util.Pair(iv, p.second()));
		}
		
		if (canUseSwitchBytecode) {
			JvmType.Function ftype = new JvmType.Function(T_INT);
			bytecodes.add(new Bytecode.Load(c.operand,convertType((Type) c.type)));
			bytecodes.add(new Bytecode.Invoke(WHILEYINT, "intValue", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			bytecodes.add(new Bytecode.Switch(c.defaultTarget, cases));
		} else {
			// ok, in this case we have to fall back to series of the if
			// conditions. Not ideal.  			
			for (Pair<Constant, String> p : c.branches) {
				Constant value = p.first();
				String target = p.second();
				translate(value, freeSlot, lambdas, bytecodes);
				bytecodes
						.add(new Bytecode.Load(c.operand, convertType(c.type)));				
				translateIfGoto(value.type(), Codes.Comparator.EQ, target, entry,
						freeSlot + 1, bytecodes);
			}
			bytecodes.add(new Bytecode.Goto(c.defaultTarget));
		}
	}

	private void translate(Codes.TryCatch c, Code.Block.Entry entry, int freeSlot,
			ArrayList<UnresolvedHandler> handlers,
			HashMap<JvmConstant, Integer> constants, ArrayList<Bytecode> bytecodes) {
		
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
		bytecodes.add(new Bytecode.GetField(WHILEYEXCEPTION, "value", JAVA_LANG_OBJECT, Bytecode.FieldMode.NONSTATIC));
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
			bytecodes.add(new Bytecode.Store(c.operand, convertType(handler
					.first())));
			bytecodes.add(new Bytecode.Goto(handler.second()));			
		}
		bytecodes.add(new Bytecode.Label(start));
		
		UnresolvedHandler trampolineHandler = new UnresolvedHandler(start,
				c.target, trampolineStart, WHILEYEXCEPTION);
		handlers.add(trampolineHandler);
	}
	
	private void translateIfGoto(Codes.If code, Code.Block.Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {	
		JvmType jt = convertType(code.type);
		bytecodes.add(new Bytecode.Load(code.leftOperand,jt));
		bytecodes.add(new Bytecode.Load(code.rightOperand,jt));		
		translateIfGoto(code.type,code.op,code.target,stmt,freeSlot,bytecodes);
	}
	
	private void translateIfGoto(Type c_type, Codes.Comparator cop, String target, Code.Block.Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {	
				
		JvmType type = convertType(c_type);
		if(c_type == Type.T_BOOL) {
			// boolean is a special case, since it is not implemented as an
			// object on the JVM stack. Therefore, we need to use the "if_cmp"
			// bytecode, rather than calling .equals() and using "if" bytecode.
			switch(cop) {
			case EQ:				
				bytecodes.add(new Bytecode.IfCmp(Bytecode.IfCmp.EQ, type, target));
				break;			
			case NEQ:				
				bytecodes.add(new Bytecode.IfCmp(Bytecode.IfCmp.NE, type, target));				
				break;			
			}
		} else if(c_type == Type.T_CHAR || c_type == Type.T_BYTE) {
			int op;
			switch(cop) {
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
			bytecodes.add(new Bytecode.IfCmp(op, T_BYTE,target));
		} else {
			// Non-primitive case. Just use the Object.equals() method, followed
			// by "if" bytecode.			
			Bytecode.IfMode op;
			switch(cop) {
			case EQ:
			{				
				if(Type.isSubtype(c_type, Type.T_NULL)) {
					// this indicates an interesting special case. The left
					// handside of this equality can be null. Therefore, we
					// cannot directly call "equals()" on this method, since
					// this would cause a null pointer exception!
					JvmType.Function ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);
					bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "equals", ftype,
							Bytecode.InvokeMode.STATIC));
				} else {
					JvmType.Function ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
					bytecodes.add(new Bytecode.Invoke((JvmType.Clazz)type, "equals", ftype,
							Bytecode.InvokeMode.VIRTUAL));								
				}
				op = Bytecode.IfMode.NE;
				break;
			}
			case NEQ:
			{
				if (Type.isSubtype(c_type, Type.T_NULL)) {
					// this indicates an interesting special case. The left
					// handside of this equality can be null. Therefore, we
					// cannot directly call "equals()" on this method, since
					// this would cause a null pointer exception!
					JvmType.Function ftype = new JvmType.Function(T_BOOL,
							JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
					bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "equals",
							ftype, Bytecode.InvokeMode.STATIC));
				} else {
					JvmType.Function ftype = new JvmType.Function(T_BOOL,
							JAVA_LANG_OBJECT);
					bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type,
							"equals", ftype, Bytecode.InvokeMode.VIRTUAL));
				}
				op = Bytecode.IfMode.EQ;
				break;
			}
			case LT:
			{							
				JvmType.Function ftype = new JvmType.Function(T_INT,type);
				bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type, "compareTo", ftype,
						Bytecode.InvokeMode.VIRTUAL));				
				op = Bytecode.IfMode.LT;			
				break;
			}
			case LTEQ:
			{			
				JvmType.Function ftype = new JvmType.Function(T_INT,type);
				bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type,
						"compareTo", ftype, Bytecode.InvokeMode.VIRTUAL));			
				op = Bytecode.IfMode.LE;
				break;
			}
			case GT:
			{						
				JvmType.Function ftype = new JvmType.Function(T_INT, type);
				bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type,
						"compareTo", ftype, Bytecode.InvokeMode.VIRTUAL));				
				op = Bytecode.IfMode.GT;
				break;
			}
			case GTEQ:
			{						
				JvmType.Function ftype = new JvmType.Function(T_INT,type);
				bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type,
						"compareTo", ftype, Bytecode.InvokeMode.VIRTUAL));				
				op = Bytecode.IfMode.GE;
				break;
			}
			case SUBSETEQ:
			{
				JvmType.Function ftype = new JvmType.Function(T_BOOL,WHILEYSET,WHILEYSET);
				bytecodes.add(new Bytecode.Invoke(WHILEYSET, "subsetEq", ftype,
						Bytecode.InvokeMode.STATIC));
				op = Bytecode.IfMode.NE;
				break;
			}
			case SUBSET:
			{
				JvmType.Function ftype = new JvmType.Function(T_BOOL,WHILEYSET,WHILEYSET);
				bytecodes.add(new Bytecode.Invoke(WHILEYSET, "subset", ftype,
						Bytecode.InvokeMode.STATIC));
				op = Bytecode.IfMode.NE;
				break;
			}
			case IN:
			{
				JvmType.Function ftype = new JvmType.Function(T_BOOL,
						JAVA_LANG_OBJECT);
				bytecodes.add(new Bytecode.Swap());
				bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_COLLECTION, "contains",
						ftype, Bytecode.InvokeMode.INTERFACE));
				op = Bytecode.IfMode.NE;
				break;
			}			
			
			default:
				syntaxError("unknown if condition encountered",filename,stmt);
				return;
			}
			
			// do the jump
			bytecodes.add(new Bytecode.If(op, target));
		}
	}
	
	private void translate(Codes.IfIs c, Code.Block.Entry stmt, int freeSlot,
			HashMap<JvmConstant,Integer> constants, ArrayList<Bytecode> bytecodes) {						
		
		// In this case, we're updating the type of a local variable. To
		// make this work, we must update the JVM type of that slot as well
		// using a checkcast. 
		String exitLabel = freshLabel();
		String trueLabel = freshLabel();

		bytecodes.add(new Bytecode.Load(c.operand, convertType(c.type)));
		translateTypeTest(trueLabel, c.type, c.rightOperand, bytecodes, constants);

		Type gdiff = Type.intersect(c.type,Type.Negation(c.rightOperand));			
		bytecodes.add(new Bytecode.Load(c.operand, convertType(c.type)));
		// now, add checkcast									
		addReadConversion(gdiff,bytecodes);			
		bytecodes.add(new Bytecode.Store(c.operand,convertType(gdiff)));							
		bytecodes.add(new Bytecode.Goto(exitLabel));
		bytecodes.add(new Bytecode.Label(trueLabel));			
		Type glb = Type.intersect(c.type, c.rightOperand);			
		bytecodes.add(new Bytecode.Load(c.operand, convertType(c.type)));
		// now, add checkcast						
		addReadConversion(glb,bytecodes);
		bytecodes.add(new Bytecode.Store(c.operand,convertType(glb)));			
		bytecodes.add(new Bytecode.Goto(c.target));
		bytecodes.add(new Bytecode.Label(exitLabel));		
	}
	
	// The purpose of this method is to translate a type test. We're testing to
	// see whether what's on the top of the stack (the value) is a subtype of
	// the type being tested.  
	protected void translateTypeTest(String trueTarget, Type src, Type test,
			ArrayList<Bytecode> bytecodes, HashMap<JvmConstant,Integer> constants) {		
		
		// First, try for the easy cases
		
		if (test instanceof Type.Null) {
			// Easy case		
			bytecodes.add(new Bytecode.If(Bytecode.IfMode.NULL, trueTarget));
		} else if(test instanceof Type.Bool) {
			bytecodes.add(new Bytecode.InstanceOf(JAVA_LANG_BOOLEAN));			
			bytecodes.add(new Bytecode.If(Bytecode.IfMode.NE, trueTarget));
		} else if(test instanceof Type.Char) {
			bytecodes.add(new Bytecode.InstanceOf(JAVA_LANG_CHARACTER));			
			bytecodes.add(new Bytecode.If(Bytecode.IfMode.NE, trueTarget));			
		} else if(test instanceof Type.Int) {
			bytecodes.add(new Bytecode.InstanceOf(WHILEYINT));			
			bytecodes.add(new Bytecode.If(Bytecode.IfMode.NE, trueTarget));
		} else if(test instanceof Type.Real) {
			bytecodes.add(new Bytecode.InstanceOf(WHILEYRAT));			
			bytecodes.add(new Bytecode.If(Bytecode.IfMode.NE, trueTarget));
		} else if(test instanceof Type.Strung) {
			bytecodes.add(new Bytecode.InstanceOf(JAVA_LANG_STRING));			
			bytecodes.add(new Bytecode.If(Bytecode.IfMode.NE, trueTarget));
			
		} else {
			// Fall-back to an external (recursive) check			
			Constant constant = Constant.V_TYPE(test);
			int id = JvmValue.get(constant,constants);			
			String name = "constant$" + id;

			bytecodes.add(new Bytecode.GetField(owner, name, WHILEYTYPE, Bytecode.FieldMode.STATIC));

			JvmType.Function ftype = new JvmType.Function(T_BOOL,convertType(src),WHILEYTYPE);
			bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "instanceOf",
					ftype, Bytecode.InvokeMode.STATIC));
			bytecodes.add(new Bytecode.If(Bytecode.IfMode.NE, trueTarget));
		}
	}	

	private void translate(Codes.Loop c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Label(c.target + "$head"));
	}
	
	protected void translate(Codes.LoopEnd end,			
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Goto(end.label + "$head"));
		bytecodes.add(new Bytecode.Label(end.label));
	}
	
	private int translate(Codes.ForAll c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {	
		
		Type elementType = c.type.element();		

		bytecodes.add(new Bytecode.Load(c.sourceOperand, convertType((Type) c.type)));
		JvmType.Function ftype = new JvmType.Function(JAVA_UTIL_ITERATOR,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYCOLLECTION, "iterator", ftype, Bytecode.InvokeMode.STATIC));
		ftype = new JvmType.Function(JAVA_UTIL_ITERATOR);
		bytecodes.add(new Bytecode.Store(freeSlot, JAVA_UTIL_ITERATOR));
		bytecodes.add(new Bytecode.Label(c.target + "$head"));
		ftype = new JvmType.Function(T_BOOL);
		bytecodes.add(new Bytecode.Load(freeSlot, JAVA_UTIL_ITERATOR));
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "hasNext", ftype,
				Bytecode.InvokeMode.INTERFACE));
		bytecodes.add(new Bytecode.If(Bytecode.IfMode.EQ, c.target));
		bytecodes.add(new Bytecode.Load(freeSlot, JAVA_UTIL_ITERATOR));
		ftype = new JvmType.Function(JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "next", ftype,
				Bytecode.InvokeMode.INTERFACE));
		addReadConversion(elementType, bytecodes);
		bytecodes.add(new Bytecode.Store(c.indexOperand, convertType(elementType)));
		
		// we need to increase the freeSlot, since we've allocated one slot to
		// hold the iterator.
		
		return freeSlot + 1;
	}
	
	private void translate(Codes.Goto c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Goto(c.target));
	}
	private void translate(Codes.Label c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Label(c.label));
	}
	
	private void translate(Codes.Debug c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(T_VOID,JAVA_LANG_STRING);
		bytecodes.add(new Bytecode.Load(c.operand, JAVA_LANG_STRING));
		bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "debug", ftype,
				Bytecode.InvokeMode.STATIC));
	}
	
	private void translate(Codes.AssertOrAssume c, Code.Block.Entry entry, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		// essentially a no-op for now
	}
	
	private void translate(Codes.Assign c, int freeSlot, ArrayList<Bytecode> bytecodes) {
		JvmType jt = convertType(c.type());
		bytecodes.add(new Bytecode.Load(c.operand(0), jt));
		addIncRefs(c.type(),bytecodes);
		bytecodes.add(new Bytecode.Store(c.target(), jt));
	}
	
	private void translate(Codes.Move c, int freeSlot, ArrayList<Bytecode> bytecodes) {
		JvmType jt = convertType(c.type());
		bytecodes.add(new Bytecode.Load(c.operand(0), jt));
		bytecodes.add(new Bytecode.Store(c.target(), jt));
	}
		
	private void translate(Codes.ListOperator c, Code.Block.Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {						
		JvmType leftType;
		JvmType rightType;
		
		switch(c.kind) {
		case APPEND:
			leftType = WHILEYLIST;
			rightType = WHILEYLIST;
			bytecodes.add(new Bytecode.Load(c.operand(0), leftType));			
			bytecodes.add(new Bytecode.Load(c.operand(1), rightType));			
			break;
		case LEFT_APPEND:			
			leftType = WHILEYLIST;
			rightType = JAVA_LANG_OBJECT;
			bytecodes.add(new Bytecode.Load(c.operand(0), leftType));			
			bytecodes.add(new Bytecode.Load(c.operand(1), convertType(c.type().element())));
			addWriteConversion(c.type().element(),bytecodes);
			break;
		case RIGHT_APPEND:
			leftType = JAVA_LANG_OBJECT;
			rightType = WHILEYLIST;
			bytecodes.add(new Bytecode.Load(c.operand(0), convertType(c.type().element())));
			addWriteConversion(c.type().element(),bytecodes);
			bytecodes.add(new Bytecode.Load(c.operand(1), rightType));
			break;
		default:
			internalFailure("unknown list operation",filename,stmt);
			return;
		}			
		
		JvmType.Function ftype = new JvmType.Function(WHILEYLIST,leftType,rightType);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "append", ftype,
				Bytecode.InvokeMode.STATIC));	
		bytecodes.add(new Bytecode.Store(c.target(), WHILEYLIST));
	}
	
	private void translate(Codes.LengthOf c, Code.Block.Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Load(c.operand(0), convertType((Type) c.type())));
		JvmType.Clazz ctype = JAVA_LANG_OBJECT;
		JvmType.Function ftype = new JvmType.Function(WHILEYINT, ctype);
		bytecodes.add(new Bytecode.Invoke(WHILEYCOLLECTION, "length", ftype,
				Bytecode.InvokeMode.STATIC));
		bytecodes.add(new Bytecode.Store(c.target(), WHILEYINT));
	}
	
	private void translate(Codes.SubList c, Code.Block.Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Load(c.operands()[0], WHILEYLIST));
		bytecodes.add(new Bytecode.Load(c.operands()[1], WHILEYINT));
		bytecodes.add(new Bytecode.Load(c.operands()[2], WHILEYINT));
		
		JvmType.Function ftype = new JvmType.Function(WHILEYLIST, WHILEYLIST,
				WHILEYINT, WHILEYINT);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "sublist", ftype,
				Bytecode.InvokeMode.STATIC));
		
		bytecodes.add(new Bytecode.Store(c.target(), WHILEYLIST));
	}	
	
	private void translate(Codes.IndexOf c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		
		bytecodes.add(new Bytecode.Load(c.operand(0), WHILEYLIST));
		bytecodes.add(new Bytecode.Load(c.operand(1), convertType(c.type().key())));
		addWriteConversion(c.type().key(),bytecodes);
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
				JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYCOLLECTION, "indexOf", ftype,
				Bytecode.InvokeMode.STATIC));
		addReadConversion(c.type().value(), bytecodes);
		
		bytecodes.add(new Bytecode.Store(c.target(),
				convertType(c.type().element())));
	}
	
	private void translate(Codes.Fail c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.New(JAVA_LANG_RUNTIMEEXCEPTION));
		bytecodes.add(new Bytecode.Dup(JAVA_LANG_RUNTIMEEXCEPTION));
		bytecodes.add(new Bytecode.LoadConst(c.message.value));
		JvmType.Function ftype = new JvmType.Function(T_VOID, JAVA_LANG_STRING);
		bytecodes.add(new Bytecode.Invoke(JAVA_LANG_RUNTIMEEXCEPTION, "<init>",
				ftype, Bytecode.InvokeMode.SPECIAL));
		bytecodes.add(new Bytecode.Throw());
	}
	
	private void translate(Codes.FieldLoad c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		
		bytecodes.add(new Bytecode.Load(c.operand(0), WHILEYRECORD));
		
		bytecodes.add(new Bytecode.LoadConst(c.field));
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,WHILEYRECORD,JAVA_LANG_STRING);
		bytecodes.add(new Bytecode.Invoke(WHILEYRECORD,"get",ftype,Bytecode.InvokeMode.STATIC));						
		addReadConversion(c.fieldType(),bytecodes);
		
		bytecodes.add(new Bytecode.Store(c.target(), convertType(c.fieldType())));
	}

	private void translate(Codes.BinaryOperator c, Code.Block.Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {				
						
		JvmType type = convertType(c.type());
		JvmType.Function ftype = new JvmType.Function(type,type);
		
		// first, load operands
		switch(c.kind) {
			case ADD:
			case SUB:	
			case MUL:
			case DIV:
			case REM:
			case BITWISEAND:
			case BITWISEOR:
			case BITWISEXOR:
				bytecodes.add(new Bytecode.Load(c.operand(0), type));
				bytecodes.add(new Bytecode.Load(c.operand(1), type));
				break;
			case LEFTSHIFT:
			case RIGHTSHIFT:
				bytecodes.add(new Bytecode.Load(c.operand(0), type));
				bytecodes.add(new Bytecode.Load(c.operand(1), WHILEYINT));
				break;
			case RANGE:
				bytecodes.add(new Bytecode.Load(c.operand(0), WHILEYINT));
				bytecodes.add(new Bytecode.Load(c.operand(1), WHILEYINT));
				break;
		}
		
		// second, apply operation
		switch(c.kind) {
		case ADD:			
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz)type, "add", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			break;
		case SUB:			
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz)type, "subtract", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			break;
		case MUL:			
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz)type, "multiply", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			break;
		case DIV:			
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz)type, "divide", ftype,
					Bytecode.InvokeMode.VIRTUAL));			
			break;
		case REM:									
				bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type,
						"remainder", ftype, Bytecode.InvokeMode.VIRTUAL));			
			break;
		case RANGE:
			ftype = new JvmType.Function(WHILEYLIST,WHILEYINT,WHILEYINT);
			bytecodes.add(new Bytecode.Invoke(WHILEYUTIL,
					"range", ftype, Bytecode.InvokeMode.STATIC));
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
			ftype = new JvmType.Function(type,type,WHILEYINT);
			bytecodes.add(new Bytecode.Invoke(WHILEYUTIL,
					"leftshift", ftype, Bytecode.InvokeMode.STATIC));
			break;
		case RIGHTSHIFT:
			ftype = new JvmType.Function(type,type,WHILEYINT);
			bytecodes.add(new Bytecode.Invoke(WHILEYUTIL,
					"rightshift", ftype, Bytecode.InvokeMode.STATIC));
			break;
		default:
			internalFailure("unknown binary expression encountered",filename,stmt);
		}		
		
		bytecodes.add(new Bytecode.Store(c.target(), type));
	}

	private void translate(Codes.SetOperator c, Code.Block.Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {		
		
		JvmType leftType;
		JvmType rightType;
		
		// First, load operands
		switch(c.kind) {
			case UNION:
			case DIFFERENCE:
			case INTERSECTION:
				leftType = WHILEYSET;
				rightType = WHILEYSET;
				bytecodes.add(new Bytecode.Load(c.operand(0), leftType));		
				bytecodes.add(new Bytecode.Load(c.operand(1), rightType));				
				break;
			case LEFT_UNION:
			case LEFT_DIFFERENCE:
			case LEFT_INTERSECTION:
				leftType = WHILEYSET;
				rightType = JAVA_LANG_OBJECT;				
				bytecodes.add(new Bytecode.Load(c.operand(0), leftType));		
				bytecodes.add(new Bytecode.Load(c.operand(1), convertType(c.type().element())));
				addWriteConversion(c.type().element(),bytecodes);
				break;
			case RIGHT_UNION:			
			case RIGHT_INTERSECTION:
				leftType = JAVA_LANG_OBJECT;
				rightType = WHILEYSET;							
				bytecodes.add(new Bytecode.Load(c.operand(0), convertType(c.type().element())));
				addWriteConversion(c.type().element(),bytecodes);
				bytecodes.add(new Bytecode.Load(c.operand(1), rightType));				
				break;
			default:
				internalFailure("Unknown set operation encountered: ",filename,stmt);
				return; // dead-code
		}

		JvmType.Function ftype= new JvmType.Function(WHILEYSET,leftType,rightType);
		
		// Second, select operation
		String operation;
		switch(c.kind) {
		case UNION: 						
		case LEFT_UNION:			
		case RIGHT_UNION:			
			operation = "union";
			break;
		case INTERSECTION: 			
		case LEFT_INTERSECTION:			
		case RIGHT_INTERSECTION:
			operation = "intersect";
			break;
		case DIFFERENCE: 			
		case LEFT_DIFFERENCE:
			operation = "difference";
			break;
		default:
			internalFailure("Unknown set operation encountered: ",filename,stmt);
			return; // dead-code
		}
					
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, operation, ftype,
				Bytecode.InvokeMode.STATIC));
		
		bytecodes.add(new Bytecode.Store(c.target(), WHILEYSET));
	}	
		
	private void translate(Codes.StringOperator c, Code.Block.Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {						
		JvmType leftType;
		JvmType rightType;
		
		switch(c.kind) {
		case APPEND:
			leftType = JAVA_LANG_STRING;
			rightType = JAVA_LANG_STRING;
			break;
		case LEFT_APPEND:
			leftType = JAVA_LANG_STRING;
			rightType = T_CHAR;
			break;
		case RIGHT_APPEND:
			leftType = T_CHAR;
			rightType = JAVA_LANG_STRING;
			break;
		default:
			internalFailure("Unknown string operation encountered: ",filename,stmt);
			return; // dead-code
		}

		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_STRING,leftType,rightType);		
		bytecodes.add(new Bytecode.Load(c.operand(0), leftType));
		bytecodes.add(new Bytecode.Load(c.operand(1), rightType));
		
		bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "append", ftype,
				Bytecode.InvokeMode.STATIC));
		
		bytecodes.add(new Bytecode.Store(c.target(), JAVA_LANG_STRING));
	}
		
	private void translate(Codes.SubString c, Code.Block.Entry stmt, int freeSlot,
			ArrayList<Bytecode> bytecodes) {					
		bytecodes.add(new Bytecode.Load(c.operands()[0], JAVA_LANG_STRING));
		bytecodes.add(new Bytecode.Load(c.operands()[1], WHILEYINT));
		bytecodes.add(new Bytecode.Load(c.operands()[2], WHILEYINT));
		
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_STRING,JAVA_LANG_STRING,
				WHILEYINT, WHILEYINT);
		
		bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "substring", ftype,
				Bytecode.InvokeMode.STATIC));
		
		bytecodes.add(new Bytecode.Store(c.target(), JAVA_LANG_STRING));
	}	
	
	private void translate(Codes.Invert c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		JvmType type = convertType(c.type());
		bytecodes.add(new Bytecode.Load(c.operand(0), type));
		bytecodes.add(new Bytecode.LoadConst(-1));
		bytecodes.add(new Bytecode.BinOp(Bytecode.BinOp.XOR, T_INT));
		bytecodes.add(new Bytecode.Store(c.target(), type));
	}
	
	private void translate(Codes.UnaryOperator c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {										
		JvmType srcType = convertType(c.type());
		JvmType targetType = null;
		String name = null;
		switch(c.kind) {
			case NEG:
				targetType = srcType;
				name = "negate";
				break;
			case NUMERATOR:
				targetType = WHILEYINT;
				name = "numerator";
				break;
			case DENOMINATOR:
				targetType = WHILEYINT;
				name = "denominator";
				break;
		}
		JvmType.Function ftype = new JvmType.Function(targetType);
		bytecodes.add(new Bytecode.Load(c.operand(0), srcType));
		bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) srcType, name,
				ftype, Bytecode.InvokeMode.VIRTUAL));		
		bytecodes.add(new Bytecode.Store(c.target(), targetType));
	}
	
	private void translate(Codes.NewObject c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {			
		JvmType type = convertType(c.type());		
		bytecodes.add(new Bytecode.New(WHILEYOBJECT));			
		bytecodes.add(new Bytecode.Dup(WHILEYOBJECT));	
		bytecodes.add(new Bytecode.Load(c.operand(0), convertType(c.type().element())));
		addWriteConversion(c.type().element(),bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_VOID,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYOBJECT, "<init>", ftype,
				Bytecode.InvokeMode.SPECIAL));
		bytecodes.add(new Bytecode.Store(c.target(), type));
	}
	
	private void translate(Codes.Dereference c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		JvmType type = convertType(c.type());
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT);		
		bytecodes.add(new Bytecode.Load(c.operand(0), type));
		bytecodes.add(new Bytecode.Invoke(WHILEYOBJECT, "state", ftype,
				Bytecode.InvokeMode.VIRTUAL));
		// finally, we need to cast the object we got back appropriately.		
		Type.Reference pt = (Type.Reference) c.type();						
		addReadConversion(pt.element(), bytecodes);
		bytecodes.add(new Bytecode.Store(c.target(), convertType(c.type().element())));
	}
	
	protected void translate(Codes.NewList c, int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.New(WHILEYLIST));		
		bytecodes.add(new Bytecode.Dup(WHILEYLIST));
		bytecodes.add(new Bytecode.LoadConst(c.operands().length));
		JvmType.Function ftype = new JvmType.Function(T_VOID,T_INT);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "<init>", ftype,
				Bytecode.InvokeMode.SPECIAL));
		
		ftype = new JvmType.Function(WHILEYLIST, WHILEYLIST, JAVA_LANG_OBJECT);
		for (int i = 0; i != c.operands().length; ++i) {
			bytecodes.add(new Bytecode.Load(c.operands()[i], convertType(c.type()
					.element())));
			addWriteConversion(c.type().element(), bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "internal_add",
					ftype, Bytecode.InvokeMode.STATIC));
		}
		
		bytecodes.add(new Bytecode.Store(c.target(), WHILEYLIST));
	}
		
	protected void translate(Codes.NewMap c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		
		construct(WHILEYMAP, freeSlot, bytecodes);
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
				JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);		
		JvmType keyType = convertType(c.type().key());
		JvmType valueType = convertType(c.type().value());

		for (int i = 0; i != c.operands().length; i=i+2) {
			bytecodes.add(new Bytecode.Dup(WHILEYMAP));
			bytecodes.add(new Bytecode.Load(c.operands()[i], keyType));
			addWriteConversion(c.type().key(), bytecodes);
			bytecodes.add(new Bytecode.Load(c.operands()[i + 1],valueType));
			addWriteConversion(c.type().value(), bytecodes);			
			bytecodes.add(new Bytecode.Invoke(WHILEYMAP, "put", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		}

		bytecodes.add(new Bytecode.Store(c.target(), WHILEYMAP));		
	}
	
	private void translate(Codes.NewRecord code, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		construct(WHILEYRECORD, freeSlot, bytecodes);				
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
				JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
		
		HashMap<String,Type> fields = code.type().fields();
		ArrayList<String> keys = new ArrayList<String>(fields.keySet());
		Collections.sort(keys);
		for (int i = 0; i != code.operands().length; i++) {
			int register = code.operands()[i];
			String key = keys.get(i);
			Type fieldType = fields.get(key);				
			bytecodes.add(new Bytecode.Dup(WHILEYRECORD));
			bytecodes.add(new Bytecode.LoadConst(key));
			bytecodes.add(new Bytecode.Load(register, convertType(fieldType)));
			addWriteConversion(fieldType,bytecodes);			
			bytecodes.add(new Bytecode.Invoke(WHILEYRECORD,"put",ftype,Bytecode.InvokeMode.VIRTUAL));						
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		}
		
		bytecodes.add(new Bytecode.Store(code.target(), WHILEYRECORD));
	}
	
	protected void translate(Codes.NewSet c, int freeSlot, ArrayList<Bytecode> bytecodes) {
		construct(WHILEYSET, freeSlot, bytecodes);		
		JvmType.Function ftype = new JvmType.Function(WHILEYSET,
				WHILEYSET,JAVA_LANG_OBJECT);
		
		for(int i=0;i!=c.operands().length;++i) {
			bytecodes.add(new Bytecode.Load(c.operands()[i], convertType(c.type()
					.element())));					
			addWriteConversion(c.type().element(),bytecodes);			
			bytecodes.add(new Bytecode.Invoke(WHILEYSET,"internal_add",ftype,Bytecode.InvokeMode.STATIC));
		}
		
		bytecodes.add(new Bytecode.Store(c.target(), WHILEYSET));
	}
	
	protected void translate(Codes.NewTuple c, int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.New(WHILEYTUPLE ));		
		bytecodes.add(new Bytecode.Dup(WHILEYTUPLE ));
		bytecodes.add(new Bytecode.LoadConst(c.operands().length));
		JvmType.Function ftype = new JvmType.Function(T_VOID,T_INT);
		bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE , "<init>", ftype,
				Bytecode.InvokeMode.SPECIAL));
		
		ftype = new JvmType.Function(WHILEYTUPLE , WHILEYTUPLE , JAVA_LANG_OBJECT);
		for (int i = 0; i != c.operands().length; ++i) {
			Type elementType = c.type().elements().get(i);
			bytecodes.add(new Bytecode.Load(c.operands()[i], convertType(elementType)));
			addWriteConversion(elementType, bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE , "internal_add",
					ftype, Bytecode.InvokeMode.STATIC));
		}
		
		bytecodes.add(new Bytecode.Store(c.target(), WHILEYTUPLE));		
	}
	
	private void translate(Codes.Lambda c, int freeSlot,
			ArrayList<ClassFile> lambdas,
			ArrayList<Bytecode> bytecodes) {
		
		// First, build and register lambda class which calls the given function
		// or method. This class will extend class wyjc.runtime.WyLambda.
		int lambda_id = lambdas.size();
		lambdas.add(buildLambda(c.name, c.type(), lambda_id));

		// Second, create and duplicate new lambda object. This will then stay
		// on the stack (whilst the parameters are constructed) until the
		// object's constructor is called.
		JvmType.Clazz lambdaClassType = new JvmType.Clazz(owner.pkg(), owner
				.lastComponent().first(), Integer.toString(lambda_id));
		
		bytecodes.add(new Bytecode.New(lambdaClassType));		
		bytecodes.add(new Bytecode.Dup(lambdaClassType));
		
		// Third, construct the parameter for lambda class constructor. In the
		// case that a binding is given for this lambda, then we need to supply
		// this as an argument to the lambda class constructor; otherwise, we
		// just pass null. To do this, we first check whether or not a binding
		// is required.
		boolean hasBinding = false;
		
		for(int operand : c.operands()) {
			if(operand != Codes.NULL_REG) {
				hasBinding = true;
				break;
			}
		}
		
		if(hasBinding) {
			// Yes, binding is required.
			bytecodes.add(new Bytecode.LoadConst(c.operands().length));
			bytecodes.add(new Bytecode.New(JAVA_LANG_OBJECT_ARRAY));
			
			for (int i = 0; i != c.operands().length; ++i) {
				bytecodes.add(new Bytecode.Dup(JAVA_LANG_OBJECT_ARRAY));
				bytecodes.add(new Bytecode.LoadConst(i));
				int operand = c.operands()[i];

				if (operand != Codes.NULL_REG) {
					Type pt = c.type().params().get(i);
					bytecodes.add(new Bytecode.Load(operand, convertType(pt)));
					addWriteConversion(pt, bytecodes);
				} else {
					bytecodes.add(new Bytecode.LoadConst(null));
				}
				bytecodes.add(new Bytecode.ArrayStore(JAVA_LANG_OBJECT_ARRAY));
			}
		} else {
			// No, binding not required.
			bytecodes.add(new Bytecode.LoadConst(null));
		}

		// Fifth, invoke lambda class constructor
		JvmType.Function ftype = new JvmType.Function(T_VOID, JAVA_LANG_OBJECT_ARRAY);
		bytecodes.add(new Bytecode.Invoke(lambdaClassType, "<init>", ftype,
				Bytecode.InvokeMode.SPECIAL));

		// Sixth, assign newly created lambda object to target register
		JvmType.Clazz clazz = (JvmType.Clazz) convertType(c.type());
		bytecodes.add(new Bytecode.Store(c.target(), clazz));
	}
	
	private void translate(Codes.Invoke c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		
		for (int i = 0; i != c.operands().length; ++i) {
			int register = c.operands()[i];
			JvmType parameterType = convertType(c.type().params().get(i));
			bytecodes.add(new Bytecode.Load(register, parameterType));
		}
		
		Path.ID mid = c.name.module();
		String mangled = nameMangle(c.name.name(), c.type());
		JvmType.Clazz owner = new JvmType.Clazz(mid.parent().toString()
				.replace('/', '.'), mid.last());
		JvmType.Function type = convertFunType(c.type());
		bytecodes
				.add(new Bytecode.Invoke(owner, mangled, type, Bytecode.InvokeMode.STATIC));

		// now, handle the case of an invoke which returns a value that should
		// be discarded. 
		if(c.target() != Codes.NULL_REG){
			bytecodes.add(new Bytecode.Store(c.target(), convertType(c.type().ret())));
		} else if(c.target() == Codes.NULL_REG && c.type().ret() != Type.T_VOID) {
			bytecodes.add(new Bytecode.Pop(convertType(c.type().ret())));
		}
	}
	
	private void translate(Codes.IndirectInvoke c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {				
		
		Type.FunctionOrMethod ft = c.type();		
		JvmType.Clazz owner = (JvmType.Clazz) convertType(ft);
		bytecodes.add(new Bytecode.Load(c.reference(),convertType(ft)));
		bytecodes.add(new Bytecode.LoadConst(ft.params().size()));
		bytecodes.add(new Bytecode.New(JAVA_LANG_OBJECT_ARRAY));
				
		int[] parameters = c.parameters();
		for (int i = 0; i != parameters.length; ++i) {			
			int register = parameters[i];
			Type pt = c.type().params().get(i);
			JvmType jpt = convertType(pt);
			bytecodes.add(new Bytecode.Dup(JAVA_LANG_OBJECT_ARRAY));
			bytecodes.add(new Bytecode.LoadConst(i));
			bytecodes.add(new Bytecode.Load(register, jpt));
			addWriteConversion(pt,bytecodes);
			bytecodes.add(new Bytecode.ArrayStore(JAVA_LANG_OBJECT_ARRAY));
		}		
						
		JvmType.Function type = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT_ARRAY);		
		
		bytecodes.add(new Bytecode.Invoke(owner, "call", type,
				Bytecode.InvokeMode.VIRTUAL));	
		
		// now, handle the case of an invoke which returns a value that should
		// be discarded.
		if (c.target() != Codes.NULL_REG) {
			addReadConversion(ft.ret(),bytecodes);
			bytecodes.add(new Bytecode.Store(c.target(),
					convertType(c.type().ret())));
		} else if (c.target() == Codes.NULL_REG) {
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		}
	}

	private void translate(Constant v, int freeSlot,
			ArrayList<ClassFile> lambdas,
			ArrayList<Bytecode> bytecodes) {
		if(v instanceof Constant.Null) {
			translate((Constant.Null)v,freeSlot,bytecodes);
		} else if(v instanceof Constant.Bool) {
			translate((Constant.Bool)v,freeSlot,bytecodes);
		} else if(v instanceof Constant.Byte) {
			translate((Constant.Byte)v,freeSlot,bytecodes);
		} else if(v instanceof Constant.Char) {
			translate((Constant.Char)v,freeSlot,bytecodes);
		} else if(v instanceof Constant.Integer) {
			translate((Constant.Integer)v,freeSlot,bytecodes);
		} else if(v instanceof Constant.Type) {
			translate((Constant.Type)v,freeSlot,bytecodes);
		} else if(v instanceof Constant.Decimal) {
			translate((Constant.Decimal)v,freeSlot,bytecodes);
		} else if(v instanceof Constant.Strung) {
			translate((Constant.Strung)v,freeSlot,bytecodes);
		} else if(v instanceof Constant.Set) {
			translate((Constant.Set)v,freeSlot,lambdas,bytecodes);
		} else if(v instanceof Constant.List) {
			translate((Constant.List)v,freeSlot,lambdas,bytecodes);
		} else if(v instanceof Constant.Record) {
			translate((Constant.Record)v,freeSlot,lambdas,bytecodes);
		} else if(v instanceof Constant.Map) {
			translate((Constant.Map)v,freeSlot,lambdas,bytecodes);
		} else if(v instanceof Constant.Tuple) {
			translate((Constant.Tuple)v,freeSlot,lambdas,bytecodes);
		} else if(v instanceof Constant.Lambda) {
			translate((Constant.Lambda)v,freeSlot,lambdas,bytecodes);
		} else {
			throw new IllegalArgumentException("unknown value encountered:" + v);
		}
	}
	
	protected void translate(Constant.Null e, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.LoadConst(null));
	}
	
	protected void translate(Constant.Bool e, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		if (e.value) {
			bytecodes.add(new Bytecode.LoadConst(1));
		} else {
			bytecodes.add(new Bytecode.LoadConst(0));
		}
	}

	protected void translate(Constant.Type e, int freeSlot,
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
				Bytecode.InvokeMode.STATIC));		
	}
	
	protected void translate(Constant.Byte e, int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.LoadConst(e.value));		
	}
	
	protected void translate(Constant.Char e, int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.LoadConst(e.value));		
	}
			
	protected void translate(Constant.Integer e, int freeSlot,			
			ArrayList<Bytecode> bytecodes) {		
		BigInteger num = e.value;
		
		if(num.bitLength() < 32) {			
			bytecodes.add(new Bytecode.LoadConst(num.intValue()));				
			bytecodes.add(new Bytecode.Conversion(T_INT,T_LONG));
			JvmType.Function ftype = new JvmType.Function(WHILEYINT,T_LONG);
			bytecodes.add(new Bytecode.Invoke(WHILEYINT, "valueOf", ftype,
					Bytecode.InvokeMode.STATIC));
		} else if(num.bitLength() < 64) {			
			bytecodes.add(new Bytecode.LoadConst(num.longValue()));				
			JvmType.Function ftype = new JvmType.Function(WHILEYINT,T_LONG);
			bytecodes.add(new Bytecode.Invoke(WHILEYINT, "valueOf", ftype,
					Bytecode.InvokeMode.STATIC));
		} else {
			// in this context, we need to use a byte array to construct the
			// integer object.
			byte[] bytes = num.toByteArray();
			JvmType.Array bat = new JvmType.Array(JvmTypes.T_BYTE);

			bytecodes.add(new Bytecode.New(WHILEYINT));		
			bytecodes.add(new Bytecode.Dup(WHILEYINT));			
			bytecodes.add(new Bytecode.LoadConst(bytes.length));
			bytecodes.add(new Bytecode.New(bat));
			for(int i=0;i!=bytes.length;++i) {
				bytecodes.add(new Bytecode.Dup(bat));
				bytecodes.add(new Bytecode.LoadConst(i));
				bytecodes.add(new Bytecode.LoadConst(bytes[i]));
				bytecodes.add(new Bytecode.ArrayStore(bat));
			}			

			JvmType.Function ftype = new JvmType.Function(T_VOID,bat);						
			bytecodes.add(new Bytecode.Invoke(WHILEYINT, "<init>", ftype,
					Bytecode.InvokeMode.SPECIAL));								
		}	
	
	}
	
	protected void translate(Constant.Decimal e, int freeSlot,
			ArrayList<Bytecode> bytecodes) {		
		BigRational rat = new BigRational(e.value);
		BigInteger den = rat.denominator();
		BigInteger num = rat.numerator();
		if(rat.isInteger()) {
			// this 
			if(num.bitLength() < 32) {			
				bytecodes.add(new Bytecode.LoadConst(num.intValue()));				
				JvmType.Function ftype = new JvmType.Function(WHILEYRAT,T_INT);
				bytecodes.add(new Bytecode.Invoke(WHILEYRAT, "valueOf", ftype,
						Bytecode.InvokeMode.STATIC));
			} else if(num.bitLength() < 64) {			
				bytecodes.add(new Bytecode.LoadConst(num.longValue()));				
				JvmType.Function ftype = new JvmType.Function(WHILEYRAT,T_LONG);
				bytecodes.add(new Bytecode.Invoke(WHILEYRAT, "valueOf", ftype,
						Bytecode.InvokeMode.STATIC));
			} else {
				// in this context, we need to use a byte array to construct the
				// integer object.
				byte[] bytes = num.toByteArray();
				JvmType.Array bat = new JvmType.Array(JvmTypes.T_BYTE);

				bytecodes.add(new Bytecode.New(WHILEYRAT));		
				bytecodes.add(new Bytecode.Dup(WHILEYRAT));			
				bytecodes.add(new Bytecode.LoadConst(bytes.length));
				bytecodes.add(new Bytecode.New(bat));
				for(int i=0;i!=bytes.length;++i) {
					bytecodes.add(new Bytecode.Dup(bat));
					bytecodes.add(new Bytecode.LoadConst(i));
					bytecodes.add(new Bytecode.LoadConst(bytes[i]));
					bytecodes.add(new Bytecode.ArrayStore(bat));
				}			

				JvmType.Function ftype = new JvmType.Function(T_VOID,bat);						
				bytecodes.add(new Bytecode.Invoke(WHILEYRAT, "<init>", ftype,
						Bytecode.InvokeMode.SPECIAL));								
			}	
		} else if(num.bitLength() < 32 && den.bitLength() < 32) {			
			bytecodes.add(new Bytecode.LoadConst(num.intValue()));
			bytecodes.add(new Bytecode.LoadConst(den.intValue()));
			JvmType.Function ftype = new JvmType.Function(WHILEYRAT,T_INT,T_INT);
			bytecodes.add(new Bytecode.Invoke(WHILEYRAT, "valueOf", ftype,
					Bytecode.InvokeMode.STATIC));
		} else if(num.bitLength() < 64 && den.bitLength() < 64) {			
			bytecodes.add(new Bytecode.LoadConst(num.longValue()));
			bytecodes.add(new Bytecode.LoadConst(den.longValue()));
			JvmType.Function ftype = new JvmType.Function(WHILEYRAT,T_LONG,T_LONG);
			bytecodes.add(new Bytecode.Invoke(WHILEYRAT, "valueOf", ftype,
					Bytecode.InvokeMode.STATIC));
		} else {
			// First, do numerator bytes
			byte[] bytes = num.toByteArray();
			JvmType.Array bat = new JvmType.Array(JvmTypes.T_BYTE);

			bytecodes.add(new Bytecode.New(WHILEYRAT));		
			bytecodes.add(new Bytecode.Dup(WHILEYRAT));			
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
			bytecodes.add(new Bytecode.Invoke(WHILEYRAT, "<init>", ftype,
					Bytecode.InvokeMode.SPECIAL));			
		}		
	}
	
	protected void translate(Constant.Strung e, int freeSlot,
			ArrayList<Bytecode> bytecodes) {		
		bytecodes.add(new Bytecode.LoadConst(e.value));
	}
	
	protected void translate(Constant.Set lv, int freeSlot,
			ArrayList<ClassFile> lambdas,
			ArrayList<Bytecode> bytecodes) {	
		bytecodes.add(new Bytecode.New(WHILEYSET));		
		bytecodes.add(new Bytecode.Dup(WHILEYSET));
		JvmType.Function ftype = new JvmType.Function(T_VOID);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "<init>", ftype,
				Bytecode.InvokeMode.SPECIAL));
		
		ftype = new JvmType.Function(T_BOOL, JAVA_LANG_OBJECT);		
		for (Constant e : lv.values) {
			bytecodes.add(new Bytecode.Dup(WHILEYSET));
			translate(e, freeSlot, lambdas, bytecodes);
			addWriteConversion(e.type(), bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYSET, "add", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(T_BOOL));
		}		
	}

	protected void translate(Constant.List lv, int freeSlot,
			ArrayList<ClassFile> lambdas,
			ArrayList<Bytecode> bytecodes) {		
		bytecodes.add(new Bytecode.New(WHILEYLIST));		
		bytecodes.add(new Bytecode.Dup(WHILEYLIST));
		bytecodes.add(new Bytecode.LoadConst(lv.values.size()));
		JvmType.Function ftype = new JvmType.Function(T_VOID,T_INT);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "<init>", ftype,
				Bytecode.InvokeMode.SPECIAL));
		
		ftype = new JvmType.Function(T_BOOL, JAVA_LANG_OBJECT);		
		for (Constant e : lv.values) {	
			bytecodes.add(new Bytecode.Dup(WHILEYLIST));
			translate(e, freeSlot, lambdas, bytecodes);
			addWriteConversion(e.type(), bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "add", ftype,
					Bytecode.InvokeMode.VIRTUAL));			
			bytecodes.add(new Bytecode.Pop(T_BOOL));
		}				
	}

	protected void translate(Constant.Tuple lv, int freeSlot,
			ArrayList<ClassFile> lambdas,
			ArrayList<Bytecode> bytecodes) {		
		bytecodes.add(new Bytecode.New(WHILEYTUPLE));		
		bytecodes.add(new Bytecode.Dup(WHILEYTUPLE));
		bytecodes.add(new Bytecode.LoadConst(lv.values.size()));
		JvmType.Function ftype = new JvmType.Function(T_VOID,T_INT);
		bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE, "<init>", ftype,
				Bytecode.InvokeMode.SPECIAL));
		
		ftype = new JvmType.Function(T_BOOL, JAVA_LANG_OBJECT);		
		for (Constant e : lv.values) {	
			bytecodes.add(new Bytecode.Dup(WHILEYTUPLE));
			translate(e, freeSlot, lambdas, bytecodes);
			addWriteConversion(e.type(), bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE, "add", ftype,
					Bytecode.InvokeMode.VIRTUAL));			
			bytecodes.add(new Bytecode.Pop(T_BOOL));
		}				
	}
	
	protected void translate(Constant.Record expr, int freeSlot,
			ArrayList<ClassFile> lambdas,
			ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
				JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
		construct(WHILEYRECORD, freeSlot, bytecodes);
		for (Map.Entry<String, Constant> e : expr.values.entrySet()) {
			Type et = e.getValue().type();
			bytecodes.add(new Bytecode.Dup(WHILEYRECORD));
			bytecodes.add(new Bytecode.LoadConst(e.getKey()));
			translate(e.getValue(), freeSlot, lambdas, bytecodes);
			addWriteConversion(et, bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYRECORD, "put", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		}
	}
	
	protected void translate(Constant.Map expr, int freeSlot,
			ArrayList<ClassFile> lambdas,
			ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
				JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
		
		construct(WHILEYMAP, freeSlot, bytecodes);
		
		for (Map.Entry<Constant, Constant> e : expr.values.entrySet()) {
			Type kt = e.getKey().type();
			Type vt = e.getValue().type();
			bytecodes.add(new Bytecode.Dup(WHILEYMAP));			
			translate(e.getKey(), freeSlot, lambdas, bytecodes);
			addWriteConversion(kt, bytecodes);
			translate(e.getValue(), freeSlot, lambdas, bytecodes);
			addWriteConversion(vt, bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYMAP, "put", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		}
	}
	
	protected void translate(Constant.Lambda c, int freeSlot,
			ArrayList<ClassFile> lambdas,
			ArrayList<Bytecode> bytecodes) {
		
		// First, build and register lambda class which calls the given function
		// or method. This class will extend class wyjc.runtime.WyLambda.
		int lambda_id = lambdas.size();
		lambdas.add(buildLambda(c.name, c.type, lambda_id));

		// Second, create and duplicate new lambda object. This will then stay
		// on the stack (whilst the parameters are constructed) until the
		// object's constructor is called.
		JvmType.Clazz lambdaClassType = new JvmType.Clazz(owner.pkg(), owner
				.lastComponent().first(), Integer.toString(lambda_id));

		bytecodes.add(new Bytecode.New(lambdaClassType));
		bytecodes.add(new Bytecode.Dup(lambdaClassType));

		// Third, invoke lambda class constructor
		JvmType.Function ftype = new JvmType.Function(T_VOID,
				JAVA_LANG_OBJECT_ARRAY);
		bytecodes.add(new Bytecode.LoadConst(null));
		bytecodes.add(new Bytecode.Invoke(lambdaClassType, "<init>", ftype,
				Bytecode.InvokeMode.SPECIAL));
	}

	/**
	 * Construct a class which implements a lambda expression. This must be a
	 * subtype of wyjc.runtime.WyLambda and must call the given function, whilst
	 * decoding and passing through the appropriate parameters.
	 * 
	 * @param name
	 *            Name of function or method which this lambda should invoke.
	 * @param type
	 *            Type of function or method which this lambda should invoke.
	 * @return
	 */
	protected ClassFile buildLambda(NameID name, Type.FunctionOrMethod type,
			int id) {
		// === (1) Determine the fully qualified type of the lambda class ===

		// start with fully qualified type of this class.
		JvmType.Clazz lambdaClassType = new JvmType.Clazz(owner.pkg(), owner
				.lastComponent().first(), Integer.toString(id));

		// === (2) Construct an empty class ===
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);
		modifiers.add(Modifier.ACC_FINAL);
		ClassFile cf = new ClassFile(CLASS_VERSION, lambdaClassType, WHILEYLAMBDA,
				new ArrayList<JvmType.Clazz>(), modifiers);

		// === (3) Add constructor ===
		modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);
		JvmType.Function constructorType = new JvmType.Function(JvmTypes.T_VOID,JAVA_LANG_OBJECT_ARRAY);
		// Create constructor method
		ClassFile.Method constructor = new ClassFile.Method("<init>", constructorType, modifiers);
		cf.methods().add(constructor);
		// Create body of constructor 
		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
		bytecodes.add(new Bytecode.Load(0, lambdaClassType));
		bytecodes.add(new Bytecode.Load(1, JAVA_LANG_OBJECT_ARRAY));
		bytecodes.add(new Bytecode.Invoke(WHILEYLAMBDA, "<init>",
				constructorType, Bytecode.InvokeMode.SPECIAL));
		bytecodes.add(new Bytecode.Return(null));
		// Add code attribute to constructor		
		jasm.attributes.Code code = new jasm.attributes.Code(bytecodes,new ArrayList<Handler>(),constructor);
		constructor.attributes().add(code);				
		
		// === (4) Add implementation of WyLambda.call(Object[]) ===
		modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);
		modifiers.add(Modifier.ACC_FINAL);
		JvmType.Function callFnType = new JvmType.Function(JvmTypes.JAVA_LANG_OBJECT,JAVA_LANG_OBJECT_ARRAY);
		// Create constructor method
		ClassFile.Method callFn = new ClassFile.Method("call", callFnType, modifiers);
		cf.methods().add(callFn);
		// Create body of call method 
		bytecodes = new ArrayList<Bytecode>();
		// Call WyFunction.bindParameters()
		bytecodes.add(new Bytecode.Load(0,lambdaClassType));
		bytecodes.add(new Bytecode.Load(1,JAVA_LANG_OBJECT_ARRAY));
		bytecodes.add(new Bytecode.Invoke(WHILEYLAMBDA, "bindParameters",
				new JvmType.Function(JAVA_LANG_OBJECT_ARRAY,
						JAVA_LANG_OBJECT_ARRAY), Bytecode.InvokeMode.VIRTUAL));
		bytecodes.add(new Bytecode.Store(1,JAVA_LANG_OBJECT_ARRAY));
		// Load parameters onto stack
		List<Type> type_params = type.params();
		for (int i = 0; i != type_params.size(); ++i) {
			bytecodes.add(new Bytecode.Load(1,JAVA_LANG_OBJECT_ARRAY));
			bytecodes.add(new Bytecode.LoadConst(i));
			bytecodes.add(new Bytecode.ArrayLoad(JAVA_LANG_OBJECT_ARRAY));
			addReadConversion(type_params.get(i),bytecodes);
		}
		
		Path.ID mid = name.module();
		String mangled = nameMangle(name.name(), type);
		JvmType.Clazz owner = new JvmType.Clazz(mid.parent().toString()
				.replace('/', '.'), mid.last());
		JvmType.Function fnType = convertFunType(type);
		bytecodes
				.add(new Bytecode.Invoke(owner, mangled, fnType, Bytecode.InvokeMode.STATIC));
		if(type.ret() instanceof Type.Void) {
			// Called function doesn't return anything, but we have to.
			// Therefore, push on dummy null value.
			bytecodes.add(new Bytecode.LoadConst(null));
		} else {
			addWriteConversion(type.ret(),bytecodes);
		}
		
		bytecodes.add(new Bytecode.Return(JAVA_LANG_OBJECT));
		
		// Add code attribute to call method	
		code = new jasm.attributes.Code(bytecodes,new ArrayList<Handler>(),callFn);
		callFn.attributes().add(code);
		
		// Done
		return cf;
	}
	
	protected void addCoercion(Type from, Type to, int freeSlot,
			HashMap<JvmConstant, Integer> constants, ArrayList<Bytecode> bytecodes) {
		
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
			int id = JvmCoercion.get(from,to,constants);
			String name = "coercion$" + id;
			JvmType.Function ft = new JvmType.Function(convertType(to), convertType(from));
			bytecodes.add(new Bytecode.Invoke(owner, name, ft, Bytecode.InvokeMode.STATIC));
		}
	}

	private void buildCoercion(Type.Bool fromType, Type toType, 
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_BOOLEAN,T_BOOL);			
		bytecodes.add(new Bytecode.Invoke(JAVA_LANG_BOOLEAN,"valueOf",ftype,Bytecode.InvokeMode.STATIC));			
		// done deal!
	}
	
	private void buildCoercion(Type.Byte fromType, Type toType,
			int freeSlot, ArrayList<Bytecode> bytecodes) {		
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_BYTE,T_BYTE);			
		bytecodes.add(new Bytecode.Invoke(JAVA_LANG_BYTE,"valueOf",ftype,Bytecode.InvokeMode.STATIC));			
		// done deal!
	}
	
	private void buildCoercion(Type.Int fromType, Type toType, 
			int freeSlot, ArrayList<Bytecode> bytecodes) {		
		Type glb = Type.intersect(Type.T_REAL, toType);
		if(glb == Type.T_REAL) { 
			// coercion required!
			JvmType.Function ftype = new JvmType.Function(WHILEYRAT,WHILEYINT);			
			bytecodes.add(new Bytecode.Invoke(WHILEYRAT,"valueOf",ftype,Bytecode.InvokeMode.STATIC));
		} else {				
			// must be => char
			JvmType.Function ftype = new JvmType.Function(T_INT);			
			bytecodes.add(new Bytecode.Invoke(WHILEYINT,"intValue",ftype,Bytecode.InvokeMode.VIRTUAL));				
		}
	}

	private void buildCoercion(Type.Char fromType, Type toType, 
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		if(!Type.isSubtype(toType,fromType)) {					
			if(toType == Type.T_REAL) { 
				// coercion required!
				JvmType.Function ftype = new JvmType.Function(WHILEYRAT,T_INT);			
				bytecodes.add(new Bytecode.Invoke(WHILEYRAT,"valueOf",ftype,Bytecode.InvokeMode.STATIC));
			} else {
				bytecodes.add(new Bytecode.Conversion(T_INT, T_LONG));
				JvmType.Function ftype = new JvmType.Function(WHILEYINT,T_LONG);			
				bytecodes.add(new Bytecode.Invoke(WHILEYINT,"valueOf",ftype,Bytecode.InvokeMode.STATIC));				
			}
		} else {
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_CHARACTER,T_CHAR);			
			bytecodes.add(new Bytecode.Invoke(JAVA_LANG_CHARACTER,"valueOf",ftype,Bytecode.InvokeMode.STATIC));	
		}
	}
	
	private void buildCoercion(Type.Strung fromType, Type.List toType, 
			int freeSlot, ArrayList<Bytecode> bytecodes) {		
		JvmType.Function ftype = new JvmType.Function(WHILEYLIST,JAVA_LANG_STRING);
		
		if(toType.element() == Type.T_CHAR) {
			bytecodes.add(new Bytecode.Invoke(WHILEYUTIL,"str2cl",ftype,Bytecode.InvokeMode.STATIC));	
		} else {
			bytecodes.add(new Bytecode.Invoke(WHILEYUTIL,"str2il",ftype,Bytecode.InvokeMode.STATIC));
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
			HashMap<JvmConstant, Integer> constants, ClassFile cf) {
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
		jasm.attributes.Code code = new jasm.attributes.Code(bytecodes,new ArrayList(),method);
		method.attributes().add(code);				
	}
	
	protected void buildCoercion(Type from, Type to, int freeSlot,
			HashMap<JvmConstant, Integer> constants, ArrayList<Bytecode> bytecodes) {
		
		// Second, case analysis on the various kinds of coercion
		if(from instanceof Type.Tuple && to instanceof Type.Tuple) {
			buildCoercion((Type.Tuple) from, (Type.Tuple) to, freeSlot, constants, bytecodes);
		} else if(from instanceof Type.Reference && to instanceof Type.Reference) {
			// TODO			
		} else if(from instanceof Type.Set && to instanceof Type.Set) {
			buildCoercion((Type.Set) from, (Type.Set) to, freeSlot, constants, bytecodes);			
		} else if(from instanceof Type.Set && to instanceof Type.Map) {
			buildCoercion((Type.Set) from, (Type.Map) to, freeSlot, constants, bytecodes);			
		} else if(from instanceof Type.List && to instanceof Type.Set) {
			buildCoercion((Type.List) from, (Type.Set) to, freeSlot, constants, bytecodes);			
		} else if(from instanceof Type.Map && to instanceof Type.Map) {
			buildCoercion((Type.Map) from, (Type.Map) to, freeSlot, constants, bytecodes);			
		} else if(from instanceof Type.List && to instanceof Type.Map) {
			buildCoercion((Type.List) from, (Type.Map) to, freeSlot, constants, bytecodes);			
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
			int freeSlot, HashMap<JvmConstant, Integer> constants,
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
			bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE,"get",ftype,Bytecode.InvokeMode.VIRTUAL));								
			addReadConversion(from,bytecodes);							
			// now perform recursive conversion
			addCoercion(from,to,freeSlot,constants,bytecodes);							
			ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);			
			bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE,"add",ftype,Bytecode.InvokeMode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(T_BOOL));
		}
		bytecodes.add(new Bytecode.Load(newSlot,WHILEYTUPLE));
	}
	
		
	protected void buildCoercion(Type.List fromType, Type.List toType, 
			int freeSlot, HashMap<JvmConstant, Integer> constants,
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
				ftype, Bytecode.InvokeMode.INTERFACE));
		bytecodes.add(new Bytecode.Store(iter,
				JAVA_UTIL_ITERATOR));
		construct(WHILEYLIST,freeSlot,bytecodes);
		bytecodes.add(new Bytecode.Store(tmp, WHILEYLIST));
		bytecodes.add(new Bytecode.Label(loopLabel));
		ftype = new JvmType.Function(T_BOOL);
		bytecodes.add(new Bytecode.Load(iter,JAVA_UTIL_ITERATOR));
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "hasNext",
				ftype, Bytecode.InvokeMode.INTERFACE));
		bytecodes.add(new Bytecode.If(Bytecode.IfMode.EQ, exitLabel));
		bytecodes.add(new Bytecode.Load(tmp,WHILEYLIST));
		bytecodes.add(new Bytecode.Load(iter,JAVA_UTIL_ITERATOR));
		ftype = new JvmType.Function(JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "next",
				ftype, Bytecode.InvokeMode.INTERFACE));						
		addReadConversion(fromType.element(),bytecodes);
		addCoercion(fromType.element(), toType.element(), freeSlot,
				constants, bytecodes);			
		ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "add",
				ftype, Bytecode.InvokeMode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(T_BOOL));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(tmp,WHILEYLIST));
	}
	
	protected void buildCoercion(Type.List fromType, Type.Map toType, 
			int freeSlot, HashMap<JvmConstant, Integer> constants,
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
				ftype, Bytecode.InvokeMode.INTERFACE));
		bytecodes.add(new Bytecode.IfCmp(Bytecode.IfCmp.GE, T_INT, exitLabel));
		bytecodes.add(new Bytecode.Load(target,WHILEYSET));
		bytecodes.add(new Bytecode.Load(iter,T_INT));
		bytecodes.add(new Bytecode.Conversion(T_INT,T_LONG));	
		ftype = new JvmType.Function(WHILEYINT,T_LONG);
		bytecodes.add(new Bytecode.Invoke(WHILEYINT, "valueOf",
				ftype, Bytecode.InvokeMode.STATIC));				
		bytecodes.add(new Bytecode.Load(source,WHILEYMAP));
		bytecodes.add(new Bytecode.Load(iter,T_INT));
		ftype = new JvmType.Function(JAVA_LANG_OBJECT,T_INT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_LIST, "get",
				ftype, Bytecode.InvokeMode.INTERFACE));						
		addReadConversion(fromType.element(),bytecodes);		
		addCoercion(fromType.element(), toType.value(), freeSlot,
				constants, bytecodes);			
		ftype = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYMAP, "put",
				ftype, Bytecode.InvokeMode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		bytecodes.add(new Bytecode.Iinc(iter,1));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(target,WHILEYMAP));		
	}
	
	protected void buildCoercion(Type.Map fromType, Type.Map toType, 
			int freeSlot, HashMap<JvmConstant, Integer> constants,
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
				ftype, Bytecode.InvokeMode.VIRTUAL));
		ftype = new JvmType.Function(JAVA_UTIL_ITERATOR);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_SET, "iterator",
				ftype, Bytecode.InvokeMode.INTERFACE));
		bytecodes.add(new Bytecode.Store(iter,
				JAVA_UTIL_ITERATOR));
					
		bytecodes.add(new Bytecode.Label(loopLabel));
		ftype = new JvmType.Function(T_BOOL);
		bytecodes.add(new Bytecode.Load(iter,JAVA_UTIL_ITERATOR));
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "hasNext",
				ftype, Bytecode.InvokeMode.INTERFACE));
		bytecodes.add(new Bytecode.If(Bytecode.IfMode.EQ, exitLabel));
		
		bytecodes.add(new Bytecode.Load(target,WHILEYMAP));
		bytecodes.add(new Bytecode.Load(iter,JAVA_UTIL_ITERATOR));
		ftype = new JvmType.Function(JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "next",
				ftype, Bytecode.InvokeMode.INTERFACE));							
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
				ftype, Bytecode.InvokeMode.VIRTUAL));
		addReadConversion(fromType.value(),bytecodes);
		addCoercion(fromType.value(), toType.value(), freeSlot,
				constants, bytecodes);
		addWriteConversion(toType.value(),bytecodes);		
		ftype = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);		
		bytecodes.add(new Bytecode.Invoke(WHILEYMAP, "put",
				ftype, Bytecode.InvokeMode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(target,WHILEYMAP));
	}
	
	protected void buildCoercion(Type.Set fromType, Type.Map toType, 
			int freeSlot, HashMap<JvmConstant, Integer> constants,
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
			int freeSlot, HashMap<JvmConstant,Integer> constants,			
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
				ftype, Bytecode.InvokeMode.INTERFACE));
		bytecodes.add(new Bytecode.Store(iter,
				JAVA_UTIL_ITERATOR));
		construct(WHILEYSET,freeSlot,bytecodes);
		bytecodes.add(new Bytecode.Store(tmp, WHILEYSET));
		bytecodes.add(new Bytecode.Label(loopLabel));
		ftype = new JvmType.Function(T_BOOL);
		bytecodes.add(new Bytecode.Load(iter,JAVA_UTIL_ITERATOR));
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "hasNext",
				ftype, Bytecode.InvokeMode.INTERFACE));
		bytecodes.add(new Bytecode.If(Bytecode.IfMode.EQ, exitLabel));
		bytecodes.add(new Bytecode.Load(tmp,WHILEYSET));
		bytecodes.add(new Bytecode.Load(iter,JAVA_UTIL_ITERATOR));
		ftype = new JvmType.Function(JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "next",
				ftype, Bytecode.InvokeMode.INTERFACE));						
		addReadConversion(fromType.element(),bytecodes);
		addCoercion(fromType.element(), toType.element(), freeSlot,
				constants, bytecodes);			
		ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "add",
				ftype, Bytecode.InvokeMode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(T_BOOL));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(tmp,WHILEYSET));
	}
	
	protected void buildCoercion(Type.Set fromType, Type.Set toType,
			int freeSlot, HashMap<JvmConstant,Integer> constants,
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
				ftype, Bytecode.InvokeMode.INTERFACE));
		bytecodes.add(new Bytecode.Store(iter,
				JAVA_UTIL_ITERATOR));
		construct(WHILEYSET,freeSlot,bytecodes);
		bytecodes.add(new Bytecode.Store(tmp, WHILEYSET));
		bytecodes.add(new Bytecode.Label(loopLabel));
		ftype = new JvmType.Function(T_BOOL);
		bytecodes.add(new Bytecode.Load(iter,JAVA_UTIL_ITERATOR));
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "hasNext",
				ftype, Bytecode.InvokeMode.INTERFACE));
		bytecodes.add(new Bytecode.If(Bytecode.IfMode.EQ, exitLabel));
		bytecodes.add(new Bytecode.Load(tmp,WHILEYSET));
		bytecodes.add(new Bytecode.Load(iter,JAVA_UTIL_ITERATOR));
		ftype = new JvmType.Function(JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "next",
				ftype, Bytecode.InvokeMode.INTERFACE));
		addReadConversion(fromType.element(),bytecodes);
		addCoercion(fromType.element(), toType.element(), freeSlot,
				constants, bytecodes);			
		ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "add",
				ftype, Bytecode.InvokeMode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(T_BOOL));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(tmp,WHILEYSET));
	}
	
	private void buildCoercion(Type.Record fromType, Type.Record toType, 
			int freeSlot, HashMap<JvmConstant,Integer> constants,
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
			bytecodes.add(new Bytecode.Invoke(WHILEYRECORD,"get",ftype,Bytecode.InvokeMode.VIRTUAL));								
			// TODO: in cases when the read conversion is a no-op, we can do
			// better here.
			addReadConversion(from,bytecodes);							
			addCoercion(from,to,freeSlot,constants,bytecodes);
			addWriteConversion(from,bytecodes);
			ftype = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);			
			bytecodes.add(new Bytecode.Invoke(WHILEYRECORD,"put",ftype,Bytecode.InvokeMode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));			
		}
		bytecodes.add(new Bytecode.Load(newSlot,WHILEYRECORD));		
	}
	
	private void buildCoercion(Type.Union from, Type to, 
			int freeSlot, HashMap<JvmConstant,Integer> constants,
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
	
	private void buildCoercion(Type from, Type.Union to, 
			int freeSlot, HashMap<JvmConstant,Integer> constants,
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
	private void addReadConversion(Type et, ArrayList<Bytecode> bytecodes) {
		if(et instanceof Type.Bool) {
			bytecodes.add(new Bytecode.CheckCast(JAVA_LANG_BOOLEAN));
			JvmType.Function ftype = new JvmType.Function(T_BOOL);
			bytecodes.add(new Bytecode.Invoke(JAVA_LANG_BOOLEAN,
					"booleanValue", ftype, Bytecode.InvokeMode.VIRTUAL));
		} else if(et instanceof Type.Byte) {
			bytecodes.add(new Bytecode.CheckCast(JAVA_LANG_BYTE));
			JvmType.Function ftype = new JvmType.Function(T_BYTE);
			bytecodes.add(new Bytecode.Invoke(JAVA_LANG_BYTE,
					"byteValue", ftype, Bytecode.InvokeMode.VIRTUAL));
		} else if(et instanceof Type.Char) {
			bytecodes.add(new Bytecode.CheckCast(JAVA_LANG_CHARACTER));
			JvmType.Function ftype = new JvmType.Function(T_CHAR);
			bytecodes.add(new Bytecode.Invoke(JAVA_LANG_CHARACTER,
					"charValue", ftype, Bytecode.InvokeMode.VIRTUAL));
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
	private void addWriteConversion(Type et, ArrayList<Bytecode> bytecodes) {
		if(et instanceof Type.Bool) {
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_BOOLEAN,T_BOOL);
			bytecodes.add(new Bytecode.Invoke(JAVA_LANG_BOOLEAN,
					"valueOf", ftype, Bytecode.InvokeMode.STATIC));
		} else if(et instanceof Type.Byte) {
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_BYTE,
					T_BYTE);
			bytecodes.add(new Bytecode.Invoke(JAVA_LANG_BYTE, "valueOf", ftype,
					Bytecode.InvokeMode.STATIC));
		} else if(et instanceof Type.Char) {
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_CHARACTER,
					T_CHAR);
			bytecodes.add(new Bytecode.Invoke(JAVA_LANG_CHARACTER, "valueOf", ftype,
					Bytecode.InvokeMode.STATIC));
		}
	}

	private void addCheckCast(JvmType type, ArrayList<Bytecode> bytecodes) {
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
	private static boolean isRefCounted(Type t) {
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
					|| t instanceof Type.Map || t instanceof Type.Record;
		}
	}

	/**
	 * Add bytecodes for incrementing the reference count.
	 * 
	 * @param type
	 * @param bytecodes
	 */
	private static void addIncRefs(Type type, ArrayList<Bytecode> bytecodes) {
		if(isRefCounted(type)){			
			JvmType jtype = convertType(type);
			JvmType.Function ftype = new JvmType.Function(jtype,jtype);			
			bytecodes.add(new Bytecode.Invoke(WHILEYUTIL,"incRefs",ftype,Bytecode.InvokeMode.STATIC));
		}
	}
	
	private static void addIncRefs(Type.List type, ArrayList<Bytecode> bytecodes) {				
		JvmType.Function ftype = new JvmType.Function(WHILEYLIST,WHILEYLIST);			
		bytecodes.add(new Bytecode.Invoke(WHILEYUTIL,"incRefs",ftype,Bytecode.InvokeMode.STATIC));
	}
	
	private static void addIncRefs(Type.Record type, ArrayList<Bytecode> bytecodes) {				
		JvmType.Function ftype = new JvmType.Function(WHILEYRECORD,WHILEYRECORD);			
		bytecodes.add(new Bytecode.Invoke(WHILEYUTIL,"incRefs",ftype,Bytecode.InvokeMode.STATIC));
	}
	
	private static void addIncRefs(Type.Map type, ArrayList<Bytecode> bytecodes) {				
		JvmType.Function ftype = new JvmType.Function(WHILEYMAP,WHILEYMAP);			
		bytecodes.add(new Bytecode.Invoke(WHILEYUTIL,"incRefs",ftype,Bytecode.InvokeMode.STATIC));
	}		
	
	/**
	 * The construct method provides a generic way to construct a Java object.
	 * 
	 * @param owner
	 * @param freeSlot
	 * @param bytecodes
	 * @param params
	 */
	private void construct(JvmType.Clazz owner, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.New(owner));		
		bytecodes.add(new Bytecode.Dup(owner));
		ArrayList<JvmType> paramTypes = new ArrayList<JvmType>();		
		JvmType.Function ftype = new JvmType.Function(T_VOID,paramTypes);
		bytecodes.add(new Bytecode.Invoke(owner, "<init>", ftype,
				Bytecode.InvokeMode.SPECIAL));
	}		 	
		
	private final static Type.Record WHILEY_PRINTWRITER_T = Type.Record(false,
			new HashMap() {
		{
			put("print", Type.Method(Type.T_VOID, Type.T_VOID, Type.T_ANY));
			put("println", Type.Method(Type.T_VOID, Type.T_VOID, Type.T_ANY));
		}
	});

	private final static Type WHILEY_SYSTEM_T = Type.Record(false,
			new HashMap() {
				{
					put("out", WHILEY_PRINTWRITER_T);
					put("args", Type.List(Type.T_STRING,false));
				}
			});
		
	private final static JvmType.Clazz WHILEYUTIL = new JvmType.Clazz("wyjc.runtime","Util");
	private final static JvmType.Clazz WHILEYLIST = new JvmType.Clazz("wyjc.runtime","WyList");	
	private final static JvmType.Clazz WHILEYSET = new JvmType.Clazz("wyjc.runtime","WySet");
	private final static JvmType.Clazz WHILEYTUPLE = new JvmType.Clazz("wyjc.runtime","WyTuple");
	private final static JvmType.Clazz WHILEYCOLLECTION = new JvmType.Clazz("wyjc.runtime","WyCollection");
	private final static JvmType.Clazz WHILEYTYPE = new JvmType.Clazz("wyjc.runtime","WyType");	
	private final static JvmType.Clazz WHILEYMAP = new JvmType.Clazz("wyjc.runtime","WyMap");
	private final static JvmType.Clazz WHILEYRECORD = new JvmType.Clazz("wyjc.runtime","WyRecord");	
	private final static JvmType.Clazz WHILEYOBJECT = new JvmType.Clazz("wyjc.runtime", "WyObject");	
	private final static JvmType.Clazz WHILEYEXCEPTION = new JvmType.Clazz("wyjc.runtime","WyException");	
	private final static JvmType.Clazz WHILEYINT = new JvmType.Clazz("java.math","BigInteger");
	private final static JvmType.Clazz WHILEYRAT = new JvmType.Clazz("wyjc.runtime","WyRat");
	private final static JvmType.Clazz WHILEYLAMBDA = new JvmType.Clazz("wyjc.runtime","WyLambda");
	
	private static final JvmType.Clazz JAVA_LANG_CHARACTER = new JvmType.Clazz("java.lang","Character");
	private static final JvmType.Clazz JAVA_LANG_SYSTEM = new JvmType.Clazz("java.lang","System");
	private static final JvmType.Array JAVA_LANG_OBJECT_ARRAY = new JvmType.Array(JAVA_LANG_OBJECT);
	private static final JvmType.Clazz JAVA_UTIL_LIST = new JvmType.Clazz("java.util","List");
	private static final JvmType.Clazz JAVA_UTIL_SET = new JvmType.Clazz("java.util","Set");
	//private static final JvmType.Clazz JAVA_LANG_REFLECT_METHOD = new JvmType.Clazz("java.lang.reflect","Method");
	private static final JvmType.Clazz JAVA_IO_PRINTSTREAM = new JvmType.Clazz("java.io","PrintStream");
	private static final JvmType.Clazz JAVA_LANG_RUNTIMEEXCEPTION = new JvmType.Clazz("java.lang","RuntimeException");
	private static final JvmType.Clazz JAVA_LANG_ASSERTIONERROR = new JvmType.Clazz("java.lang","AssertionError");
	private static final JvmType.Clazz JAVA_UTIL_COLLECTION = new JvmType.Clazz("java.util","Collection");	
	
	private JvmType.Function convertFunType(Type.FunctionOrMethod ft) {		
		ArrayList<JvmType> paramTypes = new ArrayList<JvmType>();
		for(Type pt : ft.params()) {
			paramTypes.add(convertType(pt));
		}
		JvmType rt = convertType(ft.ret());			
		return new JvmType.Function(rt,paramTypes);		
	}
	
	private static JvmType convertType(Type t) {
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
			return WHILEYINT;
		} else if(t instanceof Type.Real) {
			return WHILEYRAT;
		} else if(t instanceof Type.Meta) {
			return WHILEYTYPE;
		} else if(t instanceof Type.Strung) {
			return JAVA_LANG_STRING;
		} else if(t instanceof Type.EffectiveList) {
			return WHILEYLIST;
		} else if(t instanceof Type.EffectiveSet) {
			return WHILEYSET;
		} else if(t instanceof Type.EffectiveMap) {
			return WHILEYMAP;
		} else if(t instanceof Type.EffectiveRecord) {
			return WHILEYRECORD;
		} else if(t instanceof Type.EffectiveTuple) {
			return WHILEYTUPLE;
		} else if(t instanceof Type.Reference) {
			return WHILEYOBJECT;
		} else if(t instanceof Type.Negation) {
			// can we do any better?
			return JAVA_LANG_OBJECT;
		} else if(t instanceof Type.Union) {			
			return JAVA_LANG_OBJECT;			
		} else if(t instanceof Type.Meta) {							
			return JAVA_LANG_OBJECT;			
		} else if(t instanceof Type.FunctionOrMethod) {						
			return WHILEYLAMBDA;
		} else {
			throw new RuntimeException("unknown type encountered: " + t);
		}		
	}	
			
	protected int label = 0;
	protected String freshLabel() {
		return "cfblab" + label++;
	}	
	
	private static String nameMangle(String name, Type.FunctionOrMethod ft) {				
		try {			
			return name + "$" + typeMangle(ft);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
		
	private static String typeMangle(Type.FunctionOrMethod ft) throws IOException {		
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
	private abstract static class JvmConstant {}
	
	private static final class JvmValue extends JvmConstant {
		public final Constant value;
		public JvmValue(Constant v) {
			value = v;
		}
		public boolean equals(Object o) {
			if(o instanceof JvmValue) {
				JvmValue vc = (JvmValue) o;
				return value.equals(vc.value);
			}
			return false;
		}
		public int hashCode() {
			return value.hashCode();
		}
		public static int get(Constant value, HashMap<JvmConstant,Integer> constants) {
			JvmValue vc = new JvmValue(value);
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
	private static final class JvmCoercion extends JvmConstant {
		public final Type from;
		public final Type to;
		public JvmCoercion(Type from, Type to) {
			this.from = from;
			this.to = to;
		}
		public boolean equals(Object o) {
			if(o instanceof JvmCoercion) {
				JvmCoercion c = (JvmCoercion) o;
				return from.equals(c.from) && to.equals(c.to);
			}
			return false;
		}
		public int hashCode() {
			return from.hashCode() + to.hashCode();
		}
		public static int get(Type from, Type to, HashMap<JvmConstant,Integer> constants) {
			JvmCoercion vc = new JvmCoercion(from,to);
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
	
	private static class UnresolvedHandler {
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

