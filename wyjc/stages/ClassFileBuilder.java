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

package wyjc.stages;

import java.math.BigInteger;
import java.util.*;

import wyjc.*;
import wyjc.ast.*;
import wyjc.ast.ResolvedWhileyFile.*;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.integer.*;
import wyjc.ast.exprs.list.*;
import wyjc.ast.exprs.logic.*;
import wyjc.ast.exprs.process.*;
import wyjc.ast.exprs.real.*;
import wyjc.ast.exprs.set.*;
import wyjc.ast.exprs.tuple.*;
import wyjc.ast.stmts.*;
import wyjc.ast.types.*;
import wyjc.jvm.attributes.WhileyCondition;
import wyjc.jvm.attributes.WhileyDefine;
import wyjc.jvm.attributes.WhileyVersion;
import wyjc.jvm.rt.*;
import wyjc.util.*;
import wyjvm.attributes.*;
import wyjvm.lang.*;
import wyjvm.lang.Modifier;
import wyjvm.util.DeadCodeElimination;
import static wyjc.util.SyntaxError.*;
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
	protected Simplifier simplifier = new Simplifier();
	
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
		HashMap<Triple<String,Type,FunType>,List<FunDecl>> methodCases = new HashMap();
		
		for(Decl d : wf.declarations()) {
			if(d instanceof FunDecl) {
				FunDecl fd = (FunDecl) d;				
				if(fd.name().equals("main")) { 
					addMainLauncher = true;
				}
				FunType ft = (FunType) fd.type();
				Type pt = fd.receiver() == null ? null : fd.receiver().type();
				Triple<String,Type,FunType> key = new Triple(fd.name(),pt,ft);
				List<FunDecl> cases = methodCases.get(key);
				if(cases == null) {
					cases = new ArrayList<FunDecl>();
					methodCases.put(key,cases);
				}
				cases.add(fd);				
			} else if(d instanceof TypeDecl) {
				TypeDecl td = (TypeDecl) d;
				Type t = td.type();
				WhileyDefine wd = new WhileyDefine(td.name(),t,td.constraint());
				cf.attributes().add(wd);
			} else if(d instanceof ConstDecl) {
				ConstDecl cd = (ConstDecl) d;				
				WhileyDefine wd = new WhileyDefine(cd.name(),null,cd.constant());
				cf.attributes().add(wd);
			}
		}

		for(Triple<String,Type,FunType> c : methodCases.keySet()) {
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
	
	public ClassFile.Method buildCaseDispatch(String name, Type receiver, FunType funType,
			List<FunDecl> cases, ModuleID mid) {
		
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		// FIXME: case dispatch should only be public is cases are public
		modifiers.add(Modifier.ACC_PUBLIC);
		modifiers.add(Modifier.ACC_STATIC);
		modifiers.add(Modifier.ACC_SYNTHETIC);	
		JvmType.Function ft = convertType(receiver,funType);			
		ClassFile.Method cm = new ClassFile.Method(nameMangle(name,receiver,funType),ft,modifiers);
		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
		bytecodes.add(new Bytecode.LoadConst(0));
		
		if(cases.size() > 32) {
			syntaxError(
					"implementation currently does not support more than 32 method cases",
					cases.get(32));
		}
		
		ArrayList<Pair<Integer,String>> dispatchTable = new ArrayList();
		String baseLabel = freshLabel();
		int cn = 1;
		for (FunDecl fd : cases) {
			String falseLabel = freshLabel();
			HashMap<String, Integer> slots = new HashMap<String, Integer>();
			HashMap<String, Type> environment = new HashMap<String, Type>();
			int slot = 0;
			
			for (FunDecl.Parameter p : fd.parameters()) {
				slots.put(p.name(), slot);
				environment.put(p.name(), p.type());
				slot = slot + 1;				
			}			
			Condition condition = fd.preCondition();
			if(condition != null) {				
				translateCondition(new Not(condition), falseLabel,
						slots, environment, bytecodes);				
			}
			bytecodes.add(new Bytecode.LoadConst(cn));
			bytecodes.add(new Bytecode.BinOp(Bytecode.BinOp.OR,T_INT));
			dispatchTable.add(new Pair<Integer,String>(cn, baseLabel + "$" + cn));
			// add code for identifying dispatch here
			cn = cn << 1;
			bytecodes.add(new Bytecode.Label(falseLabel));
		}
		
		String defaultLabel = freshLabel();
		bytecodes.add(new Bytecode.Switch(defaultLabel,dispatchTable));
		
		cn = 1;
		int idx = 1;			
		JvmType.Clazz owner = new JvmType.Clazz(mid.pkg().toString(),mid.module());
		for (FunDecl fd : cases) {
			bytecodes.add(new Bytecode.Label(baseLabel + "$" + cn));
			// first, load receiver onto stack (if present)
			int pi = 0;
			if(fd.receiver() != null) {
				bytecodes.add(new Bytecode.Load(pi++, convertType(fd.receiver().type())));
			}
			// second, load parameters onto stack
			for (Type t : funType.parameters()) {
				bytecodes.add(new Bytecode.Load(pi++, convertType(t)));
			}
			// second, call the given case
			String mangle;			
			mangle = nameMangle(name + "#" + idx++, receiver, funType);			
			bytecodes.add(new Bytecode.Invoke(owner, mangle, ft,
					Bytecode.STATIC));
			// finally, return value (if there is one)
			if (funType.returnType() == Types.T_VOID) {
				bytecodes.add(new Bytecode.Return(null));
			} else {
				bytecodes.add(new Bytecode.Return(ft.returnType()));
			}
			cn = cn << 1;
		}
		bytecodes.add(new Bytecode.Label(defaultLabel));
		// FIXME: add message and source code location
		construct(JAVA_LANG_RUNTIMEEXCEPTION,new HashMap(),new HashMap(),bytecodes);
		bytecodes.add(new Bytecode.Throw());
		
		Code code = new Code(bytecodes,new ArrayList(),cm);
		cm.attributes().add(code);
		
		return cm;
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
	
	public ClassFile.Method build(int idx, FunDecl fd, ResolvedWhileyFile wf) {		
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		if(fd.isPublic()) {
			modifiers.add(Modifier.ACC_PUBLIC);
		}
		modifiers.add(Modifier.ACC_STATIC);		
		FunType flatType = fd.type();
		Type recType = fd.receiver() != null ? fd.receiver().type() : null;
		JvmType.Function ft = convertType(recType,flatType);		
		String name = idx == 0 ? fd.name() : fd.name() + "#" + idx;
		Type pt = fd.receiver() == null ? null : fd.receiver().type();
		name = nameMangle(name,pt,flatType);
		
		ClassFile.Method cm = new ClassFile.Method(name,ft,modifiers);		
		ArrayList<Bytecode> codes = translate(fd);
		Code code = new Code(codes,new ArrayList(),cm);
		cm.attributes().add(code);
		
		Condition preCondition = fd.preCondition();
		if(preCondition != null) {			
			HashMap<String,Expr> binding = new HashMap<String,Expr>();
			int pidx = 0;
			for(FunDecl.Parameter p : fd.parameters()) {
				binding.put(p.name(), new Variable("p" + pidx++));
			}
			preCondition = preCondition.substitute(binding);
			
			WhileyCondition wc = new WhileyCondition("WhileyPreCondition",preCondition);
			cm.attributes().add(wc);
		}
		Condition postCondition = fd.postCondition();
		if(postCondition != null) {
			WhileyCondition wc = new WhileyCondition("WhileyPostCondition",postCondition);
			cm.attributes().add(wc);
		}
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
	
	public void translate(Stmt stmt, HashMap<String, Integer> slots,
			HashMap<String, Type> environment,
			FunDecl fd, ArrayList<Bytecode> bytecodes) {
		try {
			if(stmt instanceof Assertion) {
				translate((Assertion)stmt,slots, environment, bytecodes);
			} else if(stmt instanceof Check) {
				translate((Check)stmt,slots, environment, bytecodes);
			}else if(stmt instanceof Assign) {
				translate((Assign)stmt,slots, environment, bytecodes);
			} else if(stmt instanceof IfElse) {
				translate((IfElse)stmt,slots, environment, fd, bytecodes);
			} else if(stmt instanceof Print) {
				translate((Print)stmt,slots, environment, bytecodes);
			} else if(stmt instanceof Return) {
				translate((Return)stmt,slots, environment, fd, bytecodes);
			} else if(stmt instanceof ExternJvm) {
				translate((ExternJvm)stmt,slots, environment, bytecodes);				
			} else if(stmt instanceof Skip) {
				// do nothing.
			} else if(stmt instanceof VarDecl) {
				translate((VarDecl)stmt,slots, environment, bytecodes);
			} else if(stmt instanceof Invoke) {
				translate((Invoke)stmt,slots, environment, bytecodes, true);				
			} else if(stmt instanceof Spawn) {
				translate((Spawn)stmt,slots, environment, bytecodes);				
			} else {
				syntaxError("unknown statement encountered",stmt);		
			}
		} catch(SyntaxError ex) {
			throw ex;
		} catch(Exception ex) {
			syntaxError("internal failure",stmt,ex);
		}
	}
	
	private void translate(Assertion stmt,
			HashMap<String, Integer> slots, HashMap<String, Type> environment,ArrayList<Bytecode> bytecodes) {
		translate(stmt.condition(),slots,environment,bytecodes);		
		String label = freshLabel();
		bytecodes.add(new Bytecode.If(Bytecode.If.NE,label));
		// FIXME: add message and source code location
		construct(JAVA_LANG_ASSERTIONERROR,slots,environment,bytecodes);
		bytecodes.add(new Bytecode.Throw());
		bytecodes.add(new Bytecode.Label(label));	
	}
			
	private void translate(Check stmt,
			HashMap<String, Integer> slots, HashMap<String, Type> environment,ArrayList<Bytecode> bytecodes) {
		translate(stmt.condition(),slots,environment,bytecodes);		
		String label = freshLabel();
		bytecodes.add(new Bytecode.If(Bytecode.If.NE,label));
		// FIXME: add message and source code location
		construct(JAVA_LANG_RUNTIMEEXCEPTION,slots,environment,bytecodes);
		bytecodes.add(new Bytecode.Throw());
		bytecodes.add(new Bytecode.Label(label));	
	}
	
	private void translate(Assign stmt,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		Type t = stmt.lhs().type(environment);
		
		if(stmt.lhs() instanceof Variable) {
			Variable v = (Variable) stmt.lhs();
			int slot = slots.get(v.name());						
			translate(stmt.rhs(), slots, environment, bytecodes);
			Type rhs_t = stmt.rhs().type(environment);
			cloneRHS(rhs_t,bytecodes);
			Bytecode.Store b = new Bytecode.Store(slot, convertType(rhs_t));
			bytecodes.add(b);
			// finally, update rhs for type inference
			environment.put(v.name(), rhs_t);
			
		} else if(stmt.lhs() instanceof ListAccess) {
			ListAccess la = (ListAccess) stmt.lhs();
			translate(la.source(), slots, environment, bytecodes);
			translate(la.index(), slots, environment, bytecodes);
			translate(stmt.rhs(), slots, environment, bytecodes);
			cloneRHS(t,bytecodes);			
			addWriteConversion(t,bytecodes);			
			JvmType.Function ftype = new JvmType.Function(T_VOID,BIG_INTEGER,JAVA_LANG_OBJECT);
			bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "set", ftype,
					Bytecode.VIRTUAL));		
		} else if(stmt.lhs() instanceof TupleAccess) {
			TupleAccess la = (TupleAccess) stmt.lhs();
			translate(la.source(), slots, environment, bytecodes);
			// FIXME: new any type is a hack, but it works
			convert(Types.T_ANY,la.source(), slots, environment, bytecodes);
			bytecodes.add(new Bytecode.LoadConst(la.name()));
			translate(stmt.rhs(), slots, environment, bytecodes);
			cloneRHS(t,bytecodes);			
			addWriteConversion(t,bytecodes);			
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);
			bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE, "put", ftype,
					Bytecode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		} else {
			syntaxError("assignment of type not implemented: " + stmt.lhs(),stmt);
		}
	}

	private void cloneRHS(Type t, ArrayList<Bytecode> bytecodes) {
		// Now, for list, set and tuple types we need to clone the object in
		// question. In fact, this could be optimised in some situations
		// where we know the old variable is not live.
		if(t instanceof ListType) {
			JvmType.Function ftype = new JvmType.Function(WHILEYLIST);
			bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "clone", ftype,
					Bytecode.VIRTUAL));				
		} else if(t instanceof SetType) {
			JvmType.Function ftype = new JvmType.Function(WHILEYSET);
			bytecodes.add(new Bytecode.Invoke(WHILEYSET, "clone", ftype,
					Bytecode.VIRTUAL));				
		} else if(t instanceof TupleType) {
			JvmType.Function ftype = new JvmType.Function(WHILEYTUPLE);
			bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE, "clone", ftype,
					Bytecode.VIRTUAL));				
		}
	}
	
	private void translate(IfElse stmt, HashMap<String, Integer> slots,
			HashMap<String, Type> environment, FunDecl fd,
			ArrayList<Bytecode> bytecodes) {
		
		if(stmt.falseBranch() == null) {
			String falseLabel = freshLabel();
			Condition cond = simplify(new Not(stmt.condition()));			
			translateCondition(cond,falseLabel,slots,environment,bytecodes);
			
			// simplest case						
			for(Stmt s : stmt.trueBranch()) {
				translate(s,slots,environment,fd, bytecodes);				
			}
			bytecodes.add(new Bytecode.Label(falseLabel));
		} else {
			String trueLabel = freshLabel();
			Condition cond = simplify(stmt.condition());
			translateCondition(cond,trueLabel,slots,environment, bytecodes);
			
			// more complex case			
			String exitLabel = freshLabel();
			for(Stmt s : stmt.falseBranch()) {
				translate(s,slots,environment,fd,bytecodes);				
			}
			bytecodes.add(new Bytecode.Goto(exitLabel));
			bytecodes.add(new Bytecode.Label(trueLabel));
			for(Stmt s : stmt.trueBranch()) {
				translate(s,slots,environment,fd,bytecodes);				
			}
			bytecodes.add(new Bytecode.Label(exitLabel));
		}
		 
	}
	
	private void translate(Print stmt,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {							
		translate(stmt.expr(),slots,environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_VOID,WHILEYLIST);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "println", ftype,
				Bytecode.STATIC));	
		return;		
	}
	
	private void translate(Return stmt,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, FunDecl fd, ArrayList<Bytecode> bytecodes) {
		if(stmt.expr() != null) {
			translate(stmt.expr(),slots,environment,bytecodes);			
			convert(fd.type().returnType(),stmt.expr(),slots,environment,bytecodes);
			bytecodes.add(new Bytecode.Return(convertType(fd.type().returnType())));
		} else {
			bytecodes.add(new Bytecode.Return(null));
		}
	}
	
	private void translate(VarDecl vd, HashMap<String, Integer> slots,
			HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {

		int slot = slots.keySet().size();

		if (vd.initialiser() != null) {
			Type t = vd.initialiser().type(environment);
			environment.put(vd.name(), t);
			translate(vd.initialiser(), slots, environment, bytecodes);
			Bytecode.Store b = new Bytecode.Store(slot, convertType(t));
			bytecodes.add(b);
		} else {
			environment.put(vd.name(),vd.type());
		}

		// FIXME: this could be certainly improved.
		slots.put(vd.name(), slot);
	}
	
	private void translate(ExternJvm stmt, HashMap<String, Integer> slots,
			HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		bytecodes.addAll(stmt.bytecodes());
	}
	
	private void translateCondition(Condition cond, String trueLabel,
			HashMap<String, Integer> slots, HashMap<String, Type> environment,
			ArrayList<Bytecode> bytecodes) {

		if(cond instanceof And) {
			String falseLabel = freshLabel();
			And bop = (And) cond;
			Condition lhsc = simplifier.simplify(new Not(bop.lhs()));
			translateCondition(lhsc,falseLabel,slots,environment,bytecodes);
			translateCondition(bop.rhs(),trueLabel,slots,environment,bytecodes);
			bytecodes.add(new Bytecode.Label(falseLabel));	
		} else if(cond instanceof Or) {
			Or bop = (Or) cond;
			translateCondition(bop.lhs(),trueLabel,slots,environment,bytecodes);
			translateCondition(bop.rhs(),trueLabel,slots,environment,bytecodes);						
		} else if(cond instanceof Not) {
			Not not = (Not) cond;
			String falseLabel = freshLabel();
			translateCondition(not.mhs(),falseLabel,slots,environment,bytecodes);		
			bytecodes.add(new Bytecode.Goto(trueLabel));			
			bytecodes.add(new Bytecode.Label(falseLabel));
		} else if(cond instanceof Some) {
			translateCondition((Some)cond,trueLabel,slots,environment, bytecodes);			
		} else if(cond instanceof None) {
			translateCondition((None)cond,trueLabel,slots,environment, bytecodes);			
		} else if(cond instanceof BoolEquals) {
			translateCondition((BoolEquals)cond,trueLabel,slots,environment, bytecodes);			
		} else if(cond instanceof BoolNotEquals) {
			translateCondition((BoolNotEquals)cond,trueLabel,slots,environment, bytecodes);			
		} else if(cond instanceof IntNotEquals) {
			translateCondition((IntNotEquals)cond,trueLabel,slots,environment, bytecodes);			
		} else if(cond instanceof IntLessThan) {
			translateCondition((IntLessThan)cond,trueLabel,slots,environment, bytecodes);
		} else if(cond instanceof IntLessThanEquals) {
			translateCondition((IntLessThanEquals)cond,trueLabel,slots,environment, bytecodes);
		} else if(cond instanceof IntGreaterThan) {
			translateCondition((IntGreaterThan)cond,trueLabel,slots,environment, bytecodes);
		} else if(cond instanceof IntGreaterThanEquals) {
			translateCondition((IntGreaterThanEquals)cond,trueLabel,slots,environment, bytecodes);
		} else if(cond instanceof RealEquals) {
			translateCondition((RealEquals)cond,trueLabel,slots,environment, bytecodes);			
		} else if(cond instanceof RealNotEquals) {
			translateCondition((RealNotEquals)cond,trueLabel,slots,environment, bytecodes);			
		} else if(cond instanceof RealLessThan) {
			translateCondition((RealLessThan)cond,trueLabel,slots,environment, bytecodes);
		} else if(cond instanceof RealLessThanEquals) {
			translateCondition((RealLessThanEquals)cond,trueLabel,slots,environment, bytecodes);
		} else if(cond instanceof RealGreaterThan) {
			translateCondition((RealGreaterThan)cond,trueLabel,slots,environment, bytecodes);
		} else if(cond instanceof RealGreaterThanEquals) {
			translateCondition((RealGreaterThanEquals)cond,trueLabel,slots,environment, bytecodes);
		} else {
			// default case
			translate(cond,slots,environment,bytecodes);
			bytecodes.add(new Bytecode.If(Bytecode.If.NE,trueLabel));	
		}
	}
	protected void translateCondition(Some e, String trueLabel,
			HashMap<String, Integer> slots, HashMap<String, Type> environment,
			ArrayList<Bytecode> bytecodes) {
		translate(e.mhs(), slots, environment, bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_INT);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "size", ftype,
				Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.If(Bytecode.If.NE, trueLabel));
	}

	protected void translateCondition(None e, String trueLabel,
			HashMap<String, Integer> slots, HashMap<String, Type> environment,
			ArrayList<Bytecode> bytecodes) {
		translate(e.mhs(), slots, environment, bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_INT);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "size", ftype,
				Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.If(Bytecode.If.EQ, trueLabel));
	}	
	protected void translateCondition(BoolEquals e,
			String trueLabel,
			HashMap<String, Integer> slots, HashMap<String, Type> environment,
			ArrayList<Bytecode> bytecodes) {
		if(e.lhs() instanceof BoolVal) {
			BoolVal bv = (BoolVal) e.lhs();						
			translate(e.rhs(), slots, environment,bytecodes);			
			if(bv.value()) {
				bytecodes.add(new Bytecode.If(Bytecode.If.NE,trueLabel));
			} else {
				bytecodes.add(new Bytecode.If(Bytecode.If.EQ,trueLabel));
			}
		} else if(e.rhs() instanceof BoolVal) {
			BoolVal bv = (BoolVal) e.rhs();						
			translate(e.lhs(), slots, environment,bytecodes);			
			if(bv.value()) {
				bytecodes.add(new Bytecode.If(Bytecode.If.NE,trueLabel));
			} else {
				bytecodes.add(new Bytecode.If(Bytecode.If.EQ,trueLabel));
			}
		} else {
			translate(e.lhs(), slots, environment,bytecodes);			
			translate(e.rhs(), slots, environment,bytecodes);			
			bytecodes.add(new Bytecode.IfCmp(Bytecode.IfCmp.EQ,T_BOOL,trueLabel));		
		}
	}
	protected void translateCondition(BoolNotEquals e,
			String trueLabel,
			HashMap<String, Integer> slots, HashMap<String, Type> environment,
			ArrayList<Bytecode> bytecodes) {
		if(e.lhs() instanceof BoolVal) {
			BoolVal bv = (BoolVal) e.lhs();						
			translate(e.rhs(), slots, environment,bytecodes);			
			if(bv.value()) {
				bytecodes.add(new Bytecode.If(Bytecode.If.EQ,trueLabel));
			} else {
				bytecodes.add(new Bytecode.If(Bytecode.If.NE,trueLabel));
			}
		} else if(e.rhs() instanceof BoolVal) {
			BoolVal bv = (BoolVal) e.rhs();						
			translate(e.lhs(), slots, environment,bytecodes);			
			if(bv.value()) {
				bytecodes.add(new Bytecode.If(Bytecode.If.EQ,trueLabel));
			} else {
				bytecodes.add(new Bytecode.If(Bytecode.If.NE,trueLabel));
			}
		} else {
			translate(e.lhs(), slots, environment,bytecodes);			
			translate(e.rhs(), slots, environment,bytecodes);			
			bytecodes.add(new Bytecode.IfCmp(Bytecode.IfCmp.NE,T_BOOL,trueLabel));		
		}
	}
	protected void translateCondition(IntLessThan e,
			String trueLabel,
			HashMap<String, Integer> slots, HashMap<String, Type> environment,
			ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_INT,BIG_INTEGER);
		bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "compareTo", ftype,
				Bytecode.VIRTUAL));		
		bytecodes.add(new Bytecode.If(Bytecode.If.LT,trueLabel));		
	}
	protected void translateCondition(IntNotEquals e,
			String trueLabel,
			HashMap<String, Integer> slots, HashMap<String, Type> environment,
			ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "equals", ftype,
				Bytecode.VIRTUAL));	
		bytecodes.add(new Bytecode.If(Bytecode.If.EQ,trueLabel));		
	}
	protected void translateCondition(IntLessThanEquals e,
			String trueLabel,
			HashMap<String, Integer> slots, HashMap<String, Type> environment,
			ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_INT,BIG_INTEGER);
		bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "compareTo", ftype,
				Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.If(Bytecode.If.LE,trueLabel));		
	}
	protected void translateCondition(IntGreaterThan e,
			String trueLabel,
			HashMap<String, Integer> slots, HashMap<String, Type> environment,
			ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_INT,BIG_INTEGER);
		bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "compareTo", ftype,
				Bytecode.VIRTUAL));		
		bytecodes.add(new Bytecode.If(Bytecode.If.GT,trueLabel));		
	}
	protected void translateCondition(IntGreaterThanEquals e,
			String trueLabel,
			HashMap<String, Integer> slots, HashMap<String, Type> environment,
			ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_INT,BIG_INTEGER);
		bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "compareTo", ftype,
				Bytecode.VIRTUAL));
		
		bytecodes.add(new Bytecode.If(Bytecode.If.GE,trueLabel));
	}
	protected void translateCondition(RealEquals e, String trueLabel,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.lhs(), slots, environment, bytecodes);		
		translate(e.rhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.rhs(), slots, environment, bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "equals", ftype,
				Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.If(Bytecode.If.NE,trueLabel));
	}
	protected void translateCondition(RealNotEquals e, String trueLabel,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.lhs(), slots, environment, bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.rhs(), slots, environment, bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "equals", ftype,
				Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.If(Bytecode.If.EQ,trueLabel));
	}
	protected void translateCondition(RealLessThan e, String trueLabel,
			HashMap<String, Integer> slots, HashMap<String, Type> environment,
			ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.lhs(), slots, environment, bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.rhs(), slots, environment, bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_INT,BIG_RATIONAL);
		bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "compareTo", ftype,
				Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.If(Bytecode.If.LT,trueLabel));
	}
	protected void translateCondition(RealLessThanEquals e, String trueLabel,
			HashMap<String, Integer> slots, HashMap<String, Type> environment,
			ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.lhs(), slots, environment, bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.rhs(), slots, environment, bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_INT,BIG_RATIONAL);
		bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "compareTo", ftype,
				Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.If(Bytecode.If.LE,trueLabel));
	}
	protected void translateCondition(RealGreaterThan e, String trueLabel,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.lhs(), slots, environment, bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.rhs(), slots, environment, bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_INT,BIG_RATIONAL);
		bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "compareTo", ftype,
				Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.If(Bytecode.If.GT,trueLabel));
	}
	protected void translateCondition(RealGreaterThanEquals e, String trueLabel,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.lhs(), slots, environment, bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.rhs(), slots, environment, bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_INT,BIG_RATIONAL);
		bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "compareTo", ftype,
				Bytecode.VIRTUAL));		
		bytecodes.add(new Bytecode.If(Bytecode.If.GE,trueLabel));
	}
	
	private void translate(Expr expr, HashMap<String, Integer> slots,
			HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		
		try {
			
			// System.out.println("*** GOT: " + expr.getClass().getName());
			
			if(expr instanceof BoolVal) {
				translate((BoolVal)expr, slots, environment, bytecodes);
			} else if(expr instanceof Not) {
				translate((Not)expr, slots, environment, bytecodes);
			} else if(expr instanceof Or) {
				translate((Or)expr, slots, environment, bytecodes);
			} else if(expr instanceof And) {
				translate((And)expr, slots, environment, bytecodes);
			} else if(expr instanceof Some) {
				translate((Some)expr, slots, environment, bytecodes);
			} else if(expr instanceof None) {
				translate((None)expr, slots, environment, bytecodes);
			} else if(expr instanceof BoolEquals) {			
				translate((BoolEquals)expr, slots, environment, bytecodes);
			} else if(expr instanceof BoolNotEquals) {			
				translate((BoolNotEquals)expr, slots, environment, bytecodes);
			} else if(expr instanceof IntVal) {			
				translate((IntVal)expr, slots, environment, bytecodes);
			} else if(expr instanceof IntAdd) {
				translate((IntAdd)expr, slots, environment, bytecodes);
			} else if(expr instanceof IntMul) {
				translate((IntMul)expr, slots, environment, bytecodes);
			} else if(expr instanceof IntSub) {
				translate((IntSub)expr, slots, environment, bytecodes);
			} else if(expr instanceof IntDiv) {
				translate((IntDiv)expr, slots, environment, bytecodes);
			} else if(expr instanceof IntEquals) {
				translate((IntEquals)expr, slots, environment, bytecodes);
			} else if(expr instanceof IntNotEquals) {
				translate((IntNotEquals)expr, slots, environment, bytecodes);
			} else if(expr instanceof IntLessThan) {
				translate((IntLessThan)expr, slots, environment, bytecodes);
			} else if(expr instanceof IntLessThanEquals) {
				translate((IntLessThanEquals)expr, slots, environment, bytecodes);
			} else if(expr instanceof IntGreaterThan) {
				translate((IntGreaterThan)expr, slots, environment, bytecodes);
			} else if(expr instanceof IntGreaterThanEquals) {
				translate((IntGreaterThanEquals)expr, slots, environment, bytecodes);
			} else if(expr instanceof IntNegate) {
				translate((IntNegate)expr, slots, environment, bytecodes);
			} else if(expr instanceof RealVal) {			
				translate((RealVal)expr, slots, environment, bytecodes);
			} else if(expr instanceof RealAdd) {
				translate((RealAdd)expr, slots, environment, bytecodes);
			} else if(expr instanceof RealMul) {
				translate((RealMul)expr, slots, environment, bytecodes);
			} else if(expr instanceof RealSub) {
				translate((RealSub)expr, slots, environment, bytecodes);
			} else if(expr instanceof RealDiv) {
				translate((RealDiv)expr, slots, environment, bytecodes);
			} else if(expr instanceof RealEquals) {
				translate((RealEquals)expr, slots, environment, bytecodes);
			} else if(expr instanceof RealNotEquals) {
				translate((RealNotEquals)expr, slots, environment, bytecodes);
			} else if(expr instanceof RealLessThan) {
				translate((RealLessThan)expr, slots, environment, bytecodes);
			} else if(expr instanceof RealLessThanEquals) {
				translate((RealLessThanEquals)expr, slots, environment, bytecodes);
			} else if(expr instanceof RealGreaterThan) {
				translate((RealGreaterThan)expr, slots, environment, bytecodes);
			} else if(expr instanceof RealGreaterThanEquals) {
				translate((RealGreaterThanEquals)expr, slots, environment, bytecodes);
			} else if(expr instanceof RealNegate) {
				translate((RealNegate)expr, slots, environment, bytecodes);
			} else if(expr instanceof SetVal) {
				translate((SetVal)expr, slots, environment, bytecodes);
			} else if(expr instanceof SetGenerator) {
				translate((SetGenerator)expr, slots, environment, bytecodes);
			} else if(expr instanceof SetComprehension) {
				translate((SetComprehension)expr, slots, environment, bytecodes);
			} else if(expr instanceof SetLength) {
				translate((SetLength)expr, slots, environment, bytecodes);
			} else if(expr instanceof SetEquals) {
				translate((SetEquals)expr, slots, environment, bytecodes);
			} else if(expr instanceof SetNotEquals) {
				translate((SetNotEquals)expr, slots, environment, bytecodes);
			} else if(expr instanceof SetUnion) {
				translate((SetUnion)expr, slots, environment, bytecodes);
			} else if(expr instanceof SetIntersection) {
				translate((SetIntersection)expr, slots, environment, bytecodes);
			} else if(expr instanceof SetDifference) {
				translate((SetDifference)expr, slots, environment, bytecodes);
			} else if(expr instanceof SetElementOf) {
				translate((SetElementOf)expr, slots, environment, bytecodes);
			} else if(expr instanceof SubsetEq) {
				translate((SubsetEq)expr, slots, environment, bytecodes);
			} else if(expr instanceof Subset) {
				translate((Subset)expr, slots, environment, bytecodes);
			} else if(expr instanceof ListVal) {
				translate((ListVal)expr, slots, environment, bytecodes);
			} else if(expr instanceof RangeVal) {			
				translate((RangeVal)expr, slots, environment, bytecodes);
			} else if(expr instanceof RangeGenerator) {
				translate((RangeGenerator)expr, slots, environment, bytecodes);
			} else if(expr instanceof ListGenerator) {
				translate((ListGenerator)expr, slots, environment, bytecodes);
			} else if(expr instanceof ListAccess) {
				translate((ListAccess)expr, slots, environment, bytecodes);
			} else if(expr instanceof ListSublist) {
				translate((ListSublist)expr, slots, environment, bytecodes);
			} else if(expr instanceof ListAppend) {
				translate((ListAppend)expr, slots, environment, bytecodes);
			} else if(expr instanceof ListEquals) {
				translate((ListEquals)expr, slots, environment, bytecodes);
			} else if(expr instanceof ListNotEquals) {
				translate((ListNotEquals)expr, slots, environment, bytecodes);
			} else if(expr instanceof ListLength) {
				translate((ListLength)expr, slots, environment, bytecodes);
			} else if(expr instanceof ListElementOf) {
				translate((ListElementOf)expr, slots, environment, bytecodes);
			} else if(expr instanceof TupleVal) {
				translate((TupleVal)expr, slots, environment, bytecodes);
			} else if(expr instanceof TupleGenerator) {
				translate((TupleGenerator)expr, slots, environment, bytecodes);
			} else if(expr instanceof TupleAccess) {
				translate((TupleAccess)expr, slots, environment, bytecodes);
			} else if(expr instanceof TupleEquals) {
				translate((TupleEquals)expr, slots, environment, bytecodes);
			} else if(expr instanceof TupleNotEquals) {
				translate((TupleNotEquals)expr, slots, environment, bytecodes);
			} else if(expr instanceof Invoke) {
				translate((Invoke)expr, slots, environment, bytecodes, false);
			} else if(expr instanceof Variable) {
				translate((Variable)expr, slots, environment, bytecodes);
			} else if(expr instanceof Spawn) {
				translate((Spawn)expr, slots, environment, bytecodes);
			} else if(expr instanceof TypeGate) {
				translate((TypeGate)expr, slots, environment, bytecodes);
			} else if(expr instanceof ProcessAccess) {
				translate((ProcessAccess)expr, slots, environment, bytecodes);
			} else {
				syntaxError("unknown expression encountered (" + expr.getClass().getName() + ")",expr);			
			}
		} catch(SyntaxError ex) {
			throw ex;
		} catch(Exception ex) {
			syntaxError("internal failure",expr,ex);
		}
	}
	
	protected void translate(BoolVal e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		if(e.value()) {			
			bytecodes.add(new Bytecode.LoadConst(1));				
		} else {			
			bytecodes.add(new Bytecode.LoadConst(0));
		}
	}
	protected void translate(Not e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.mhs(), slots, environment,bytecodes);				
		String exitLabel = freshLabel();
		String trueLabel = freshLabel();		
		bytecodes.add(new Bytecode.If(Bytecode.If.EQ,trueLabel));
		bytecodes.add(new Bytecode.LoadConst(0));
		bytecodes.add(new Bytecode.Goto(exitLabel));
		bytecodes.add(new Bytecode.Label(trueLabel));
		bytecodes.add(new Bytecode.LoadConst(1));
		bytecodes.add(new Bytecode.Label(exitLabel));
	}
	protected void translate(Or e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);		
		
		String exitLabel = freshLabel();
		String trueLabel = freshLabel();
		bytecodes.add(new Bytecode.If(Bytecode.If.NE,trueLabel));
		translate(e.rhs(), slots, environment,bytecodes);
		bytecodes.add(new Bytecode.If(Bytecode.If.NE,trueLabel));
		bytecodes.add(new Bytecode.LoadConst(0));
		bytecodes.add(new Bytecode.Goto(exitLabel));
		bytecodes.add(new Bytecode.Label(trueLabel));
		bytecodes.add(new Bytecode.LoadConst(1));
		bytecodes.add(new Bytecode.Label(exitLabel));
	}
	protected void translate(And e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);		
		
		String exitLabel = freshLabel();
		String falseLabel = freshLabel();
		bytecodes.add(new Bytecode.If(Bytecode.If.EQ,falseLabel));
		translate(e.rhs(), slots, environment,bytecodes);
		bytecodes.add(new Bytecode.If(Bytecode.If.EQ,falseLabel));
		bytecodes.add(new Bytecode.LoadConst(1));
		bytecodes.add(new Bytecode.Goto(exitLabel));
		bytecodes.add(new Bytecode.Label(falseLabel));
		bytecodes.add(new Bytecode.LoadConst(0));
		bytecodes.add(new Bytecode.Label(exitLabel));
	}
	protected void translate(Some e, HashMap<String, Integer> slots, HashMap<String, Type> environment,
			ArrayList<Bytecode> bytecodes) {
		translate(e.mhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_INT);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "size", ftype,
				Bytecode.VIRTUAL));
		String exitLabel = freshLabel();
		String falseLabel = freshLabel();
		bytecodes.add(new Bytecode.If(Bytecode.If.EQ,falseLabel));
		bytecodes.add(new Bytecode.LoadConst(1));
		bytecodes.add(new Bytecode.Goto(exitLabel));
		bytecodes.add(new Bytecode.Label(falseLabel));
		bytecodes.add(new Bytecode.LoadConst(0));
		bytecodes.add(new Bytecode.Label(exitLabel));
	}
	protected void translate(None e, HashMap<String, Integer> slots, HashMap<String, Type> environment,
			ArrayList<Bytecode> bytecodes) {
		translate(e.mhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_INT);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "size", ftype,
				Bytecode.VIRTUAL));
		String exitLabel = freshLabel();
		String falseLabel = freshLabel();
		bytecodes.add(new Bytecode.If(Bytecode.If.NE,falseLabel));
		bytecodes.add(new Bytecode.LoadConst(1));
		bytecodes.add(new Bytecode.Goto(exitLabel));
		bytecodes.add(new Bytecode.Label(falseLabel));
		bytecodes.add(new Bytecode.LoadConst(0));
		bytecodes.add(new Bytecode.Label(exitLabel));
	}
	protected void translate(BoolEquals e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {		
		String falseLabel = freshLabel();
		if(e.lhs() instanceof BoolVal) {
			BoolVal bv = (BoolVal) e.lhs();						
			translate(e.rhs(), slots, environment,bytecodes);
			if(bv.value()) {
				bytecodes.add(new Bytecode.If(Bytecode.If.EQ,falseLabel));
			} else {
				bytecodes.add(new Bytecode.If(Bytecode.If.NE,falseLabel));
			}
		} else if(e.rhs() instanceof BoolVal) {
			BoolVal bv = (BoolVal) e.rhs();						
			translate(e.lhs(), slots, environment,bytecodes);
			if(bv.value()) {
				bytecodes.add(new Bytecode.If(Bytecode.If.EQ,falseLabel));
			} else {
				bytecodes.add(new Bytecode.If(Bytecode.If.NE,falseLabel));
			}
		} else {
			translate(e.lhs(), slots, environment,bytecodes);
			translate(e.rhs(), slots, environment,bytecodes);
			bytecodes.add(new Bytecode.IfCmp(Bytecode.IfCmp.NE,T_BOOL,falseLabel));
		}
		String exitLabel = freshLabel();
		bytecodes.add(new Bytecode.LoadConst(1));
		bytecodes.add(new Bytecode.Goto(exitLabel));
		bytecodes.add(new Bytecode.Label(falseLabel));
		bytecodes.add(new Bytecode.LoadConst(0));
		bytecodes.add(new Bytecode.Label(exitLabel));			
	}
	protected void translate(BoolNotEquals e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		String falseLabel = freshLabel();
		if(e.lhs() instanceof BoolVal) {
			BoolVal bv = (BoolVal) e.lhs();						
			translate(e.rhs(), slots, environment,bytecodes);
			if(bv.value()) {
				bytecodes.add(new Bytecode.If(Bytecode.If.NE,falseLabel));
			} else {
				bytecodes.add(new Bytecode.If(Bytecode.If.EQ,falseLabel));
			}
		} else if(e.rhs() instanceof BoolVal) {
			BoolVal bv = (BoolVal) e.rhs();						
			translate(e.lhs(), slots, environment,bytecodes);
			if(bv.value()) {
				bytecodes.add(new Bytecode.If(Bytecode.If.NE,falseLabel));
			} else {
				bytecodes.add(new Bytecode.If(Bytecode.If.EQ,falseLabel));
			}
		} else {
			translate(e.lhs(), slots, environment,bytecodes);
			translate(e.rhs(), slots, environment,bytecodes);				
			bytecodes.add(new Bytecode.IfCmp(Bytecode.IfCmp.EQ,T_BOOL,falseLabel));
		}
		String exitLabel = freshLabel();
		bytecodes.add(new Bytecode.LoadConst(1));
		bytecodes.add(new Bytecode.Goto(exitLabel));
		bytecodes.add(new Bytecode.Label(falseLabel));
		bytecodes.add(new Bytecode.LoadConst(0));
		bytecodes.add(new Bytecode.Label(exitLabel));		
	}
	protected void translate(IntVal e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		BigInteger v = e.value();
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
	protected void translate(IntAdd e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(BIG_INTEGER,BIG_INTEGER);
		bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "add", ftype,
				Bytecode.VIRTUAL));
	}
	protected void translate(IntSub e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(BIG_INTEGER,BIG_INTEGER);
		bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "subtract", ftype,
				Bytecode.VIRTUAL));
	}
	protected void translate(IntMul e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(BIG_INTEGER,BIG_INTEGER);
		bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "multiply", ftype,
				Bytecode.VIRTUAL));
	}
	protected void translate(IntDiv e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(BIG_INTEGER,BIG_INTEGER);
		bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "divide", ftype,
				Bytecode.VIRTUAL));
	}
	protected void translate(IntEquals e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);		
		translate(e.rhs(), slots, environment,bytecodes);		
		JvmType.Function ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "equals", ftype,
				Bytecode.VIRTUAL));
	}
	protected void translate(IntNotEquals e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "equals", ftype,
				Bytecode.VIRTUAL));
		
		String exitLabel = freshLabel();
		String trueLabel = freshLabel();
		bytecodes.add(new Bytecode.If(Bytecode.If.EQ,trueLabel));
		bytecodes.add(new Bytecode.LoadConst(0));
		bytecodes.add(new Bytecode.Goto(exitLabel));
		bytecodes.add(new Bytecode.Label(trueLabel));
		bytecodes.add(new Bytecode.LoadConst(1));
		bytecodes.add(new Bytecode.Label(exitLabel));
	}
	protected void translate(IntLessThan e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_INT,BIG_INTEGER);
		bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "compareTo", ftype,
				Bytecode.VIRTUAL));
		
		String exitLabel = freshLabel();
		String trueLabel = freshLabel();
		bytecodes.add(new Bytecode.If(Bytecode.If.LT,trueLabel));
		bytecodes.add(new Bytecode.LoadConst(0));
		bytecodes.add(new Bytecode.Goto(exitLabel));
		bytecodes.add(new Bytecode.Label(trueLabel));
		bytecodes.add(new Bytecode.LoadConst(1));
		bytecodes.add(new Bytecode.Label(exitLabel));
	}
	protected void translate(IntLessThanEquals e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_INT,BIG_INTEGER);
		bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "compareTo", ftype,
				Bytecode.VIRTUAL));
		
		String exitLabel = freshLabel();
		String trueLabel = freshLabel();
		bytecodes.add(new Bytecode.If(Bytecode.If.LE,trueLabel));
		bytecodes.add(new Bytecode.LoadConst(0));
		bytecodes.add(new Bytecode.Goto(exitLabel));
		bytecodes.add(new Bytecode.Label(trueLabel));
		bytecodes.add(new Bytecode.LoadConst(1));
		bytecodes.add(new Bytecode.Label(exitLabel));
	}
	protected void translate(IntGreaterThan e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_INT,BIG_INTEGER);
		bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "compareTo", ftype,
				Bytecode.VIRTUAL));
		
		String exitLabel = freshLabel();
		String trueLabel = freshLabel();
		bytecodes.add(new Bytecode.If(Bytecode.If.GT,trueLabel));
		bytecodes.add(new Bytecode.LoadConst(0));
		bytecodes.add(new Bytecode.Goto(exitLabel));
		bytecodes.add(new Bytecode.Label(trueLabel));
		bytecodes.add(new Bytecode.LoadConst(1));
		bytecodes.add(new Bytecode.Label(exitLabel));
	}
	protected void translate(IntGreaterThanEquals e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_INT,BIG_INTEGER);
		bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "compareTo", ftype,
				Bytecode.VIRTUAL));
		
		String exitLabel = freshLabel();
		String trueLabel = freshLabel();
		bytecodes.add(new Bytecode.If(Bytecode.If.GE,trueLabel));
		bytecodes.add(new Bytecode.LoadConst(0));
		bytecodes.add(new Bytecode.Goto(exitLabel));
		bytecodes.add(new Bytecode.Label(trueLabel));
		bytecodes.add(new Bytecode.LoadConst(1));
		bytecodes.add(new Bytecode.Label(exitLabel));
	}
	protected void translate(IntNegate e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.mhs(), slots, environment,bytecodes);		
		JvmType.Function ftype = new JvmType.Function(BIG_INTEGER);
		bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "negate", ftype,
				Bytecode.VIRTUAL));
	}	
	protected void translate(RealVal e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		BigRational rat = e.value();
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
	protected void translate(RealAdd e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(BIG_RATIONAL,BIG_RATIONAL);
		bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "add", ftype,
				Bytecode.VIRTUAL));
	}
	protected void translate(RealSub e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(BIG_RATIONAL,BIG_RATIONAL);
		bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "subtract", ftype,
				Bytecode.VIRTUAL));
	}
	protected void translate(RealMul e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(BIG_RATIONAL,BIG_RATIONAL);
		bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "multiply", ftype,
				Bytecode.VIRTUAL));
	}
	protected void translate(RealDiv e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(BIG_RATIONAL,BIG_RATIONAL);
		bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "divide", ftype,
				Bytecode.VIRTUAL));
	}
	protected void translate(RealEquals e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.lhs(), slots, environment,bytecodes);		
		translate(e.rhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "equals", ftype,
				Bytecode.VIRTUAL));
	}
	protected void translate(RealNotEquals e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.lhs(), slots, environment,bytecodes);		
		translate(e.rhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "equals", ftype,
				Bytecode.VIRTUAL));
		
		String exitLabel = freshLabel();
		String trueLabel = freshLabel();
		bytecodes.add(new Bytecode.If(Bytecode.If.EQ,trueLabel));
		bytecodes.add(new Bytecode.LoadConst(0));
		bytecodes.add(new Bytecode.Goto(exitLabel));
		bytecodes.add(new Bytecode.Label(trueLabel));
		bytecodes.add(new Bytecode.LoadConst(1));
		bytecodes.add(new Bytecode.Label(exitLabel));
	}
	protected void translate(RealLessThan e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_INT,BIG_RATIONAL);
		bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "compareTo", ftype,
				Bytecode.VIRTUAL));
		
		String exitLabel = freshLabel();
		String trueLabel = freshLabel();
		bytecodes.add(new Bytecode.If(Bytecode.If.LT,trueLabel));
		bytecodes.add(new Bytecode.LoadConst(0));
		bytecodes.add(new Bytecode.Goto(exitLabel));
		bytecodes.add(new Bytecode.Label(trueLabel));
		bytecodes.add(new Bytecode.LoadConst(1));
		bytecodes.add(new Bytecode.Label(exitLabel));
	}
	protected void translate(RealLessThanEquals e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_INT,BIG_RATIONAL);
		bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "compareTo", ftype,
				Bytecode.VIRTUAL));
		
		String exitLabel = freshLabel();
		String trueLabel = freshLabel();
		bytecodes.add(new Bytecode.If(Bytecode.If.LE,trueLabel));
		bytecodes.add(new Bytecode.LoadConst(0));
		bytecodes.add(new Bytecode.Goto(exitLabel));
		bytecodes.add(new Bytecode.Label(trueLabel));
		bytecodes.add(new Bytecode.LoadConst(1));
		bytecodes.add(new Bytecode.Label(exitLabel));
	}
	protected void translate(RealGreaterThan e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_INT,BIG_RATIONAL);
		bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "compareTo", ftype,
				Bytecode.VIRTUAL));
		
		String exitLabel = freshLabel();
		String trueLabel = freshLabel();
		bytecodes.add(new Bytecode.If(Bytecode.If.GT,trueLabel));
		bytecodes.add(new Bytecode.LoadConst(0));
		bytecodes.add(new Bytecode.Goto(exitLabel));
		bytecodes.add(new Bytecode.Label(trueLabel));
		bytecodes.add(new Bytecode.LoadConst(1));
		bytecodes.add(new Bytecode.Label(exitLabel));
	}
	protected void translate(RealGreaterThanEquals e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		convert(Types.T_REAL,e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_INT,BIG_RATIONAL);
		bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "compareTo", ftype,
				Bytecode.VIRTUAL));
		
		String exitLabel = freshLabel();
		String trueLabel = freshLabel();
		bytecodes.add(new Bytecode.If(Bytecode.If.GE,trueLabel));
		bytecodes.add(new Bytecode.LoadConst(0));
		bytecodes.add(new Bytecode.Goto(exitLabel));
		bytecodes.add(new Bytecode.Label(trueLabel));
		bytecodes.add(new Bytecode.LoadConst(1));
		bytecodes.add(new Bytecode.Label(exitLabel));
	}
	protected void translate(RealNegate e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.mhs(), slots, environment,bytecodes);		
		convert(Types.T_REAL,e.mhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(BIG_RATIONAL);
		bytecodes.add(new Bytecode.Invoke(BIG_RATIONAL, "negate", ftype,
				Bytecode.VIRTUAL));
	}
	
	protected void translate(RangeGenerator lv,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {					
		translate(lv.start(),slots,environment,bytecodes);
		translate(lv.end(),slots,environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(WHILEYLIST,BIG_INTEGER,BIG_INTEGER);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "range", ftype,
				Bytecode.STATIC));					
	}
	
	protected void translate(RangeVal lv,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {					
		translate(lv.start(),slots,environment,bytecodes);
		translate(lv.end(),slots,environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(WHILEYLIST,BIG_INTEGER,BIG_INTEGER);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "range", ftype,
				Bytecode.STATIC));					
	}

	protected void translate(ListVal lv,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		construct(WHILEYLIST, slots, environment,bytecodes);		
		JvmType.Function ftype = new JvmType.Function(T_BOOL,
				JAVA_LANG_OBJECT);  
		for(Expr e : lv.getValues()) {
			Type et = e.type(environment);
			bytecodes.add(new Bytecode.Dup(WHILEYLIST));
			translate(e, slots, environment,bytecodes);
			addWriteConversion(et,bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYLIST,"add",ftype,Bytecode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(WHILEYLIST));
		}		
	}
	protected void translate(ListGenerator lv,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {						
		construct(WHILEYLIST, slots, environment,bytecodes);		
		JvmType.Function ftype = new JvmType.Function(T_BOOL,
				JAVA_LANG_OBJECT);  
		for(Expr e : lv.getValues()) {
			Type et = e.type(environment);			
			bytecodes.add(new Bytecode.Dup(WHILEYLIST));
			translate(e, slots, environment,bytecodes);
			addWriteConversion(et,bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYLIST,"add",ftype,Bytecode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(WHILEYLIST));
		}
	}
	protected void translate(ListAccess e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		Type et = e.type(environment);		
		translate(e.source(), slots, environment,bytecodes);
		translate(e.index(), slots, environment,bytecodes);		
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,BIG_INTEGER);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "get", ftype,
				Bytecode.VIRTUAL));
		addReadConversion(et,bytecodes);			
	}
	protected void translate(ListSublist e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {			
		translate(e.source(), slots, environment,bytecodes);
		translate(e.start(), slots, environment,bytecodes);		
		translate(e.end(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(WHILEYLIST,BIG_INTEGER,BIG_INTEGER);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "sublist", ftype,
				Bytecode.VIRTUAL));					
	}
	protected void translate(ListAppend e,			
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		Type et = e.type(environment);
		Type lhs_t = e.lhs().type(environment);
		Type rhs_t = e.lhs().type(environment);
		translate(e.lhs(), slots, environment,bytecodes);
		convert(et,e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		convert(et,e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(WHILEYLIST,WHILEYLIST);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "append", ftype,
				Bytecode.VIRTUAL));					
	}
	protected void translate(ListElementOf e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.rhs(), slots, environment,bytecodes);
		translate(e.lhs(), slots, environment,bytecodes);		
		JvmType.Function ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "contains", ftype,
				Bytecode.VIRTUAL));
	}
	protected void translate(ListLength e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.mhs(), slots, environment,bytecodes);			
		JvmType.Function ftype = new JvmType.Function(T_INT);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "size", ftype,
				Bytecode.VIRTUAL));
		bytecodes.add(new Bytecode.Conversion(T_INT,T_LONG));
		ftype = new JvmType.Function(BIG_INTEGER,T_LONG);
		bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "valueOf", ftype,
				Bytecode.STATIC));
	}
	protected void translate(ListEquals e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		Type lt = e.lhs().type(environment);
		Type rt = e.rhs().type(environment);		
		Type target = Types.leastUpperBound(lt,rt);
		translate(e.lhs(), slots, environment,bytecodes);		
		convert(target,e.lhs(), slots, environment, bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		convert(target,e.rhs(), slots, environment, bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_BOOL,WHILEYLIST);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "equals", ftype,
				Bytecode.VIRTUAL));
	}
	protected void translate(ListNotEquals e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		Type lt = e.lhs().type(environment);
		Type rt = e.rhs().type(environment);		
		Type target = Types.leastUpperBound(lt,rt);		
		translate(e.lhs(), slots, environment,bytecodes);
		convert(target,e.lhs(), slots, environment, bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		convert(target,e.rhs(), slots, environment, bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_BOOL,WHILEYLIST);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "notEquals", ftype,
				Bytecode.VIRTUAL));		
	}
	protected void translate(SetVal lv,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		construct(WHILEYSET, slots, environment,bytecodes);		
		JvmType.Function ftype = new JvmType.Function(T_BOOL,
				JAVA_LANG_OBJECT);  
		for(Expr e : lv.getValues()) {
			// FIXME: there is a bug here for bool lists
			bytecodes.add(new Bytecode.Dup(WHILEYSET));
			translate(e, slots, environment,bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYSET,"add",ftype,Bytecode.VIRTUAL));
			// FIXME: there is a bug here for bool lists
			bytecodes.add(new Bytecode.Pop(WHILEYSET));
		}
	}
	protected void translate(SetGenerator lv,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		construct(WHILEYSET, slots, environment,bytecodes);		
		JvmType.Function ftype = new JvmType.Function(T_BOOL,
				JAVA_LANG_OBJECT);  
		for(Expr e : lv.getValues()) {
			// FIXME: there is a bug here for bool lists
			bytecodes.add(new Bytecode.Dup(WHILEYSET));
			translate(e, slots, environment,bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYSET,"add",ftype,Bytecode.VIRTUAL));
			// FIXME: there is a bug here for bool lists
			bytecodes.add(new Bytecode.Pop(WHILEYSET));
		}
	}	
	protected void translate(SetLength e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.mhs(), slots, environment,bytecodes);			
		JvmType.Function ftype = new JvmType.Function(T_INT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_COLLECTION, "size", ftype,
				Bytecode.INTERFACE));
		bytecodes.add(new Bytecode.Conversion(T_INT,T_LONG));
		ftype = new JvmType.Function(BIG_INTEGER,T_LONG);
		bytecodes.add(new Bytecode.Invoke(BIG_INTEGER, "valueOf", ftype,
				Bytecode.STATIC));
	}
	protected void translate(SetElementOf e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.rhs(), slots, environment,bytecodes);
		translate(e.lhs(), slots, environment,bytecodes);		
		JvmType.Function ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_COLLECTION, "contains", ftype,
				Bytecode.INTERFACE));
	}
	protected void translate(SetEquals e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		SetType target = (SetType) Types.leastUpperBound(e.lhs().type(environment),e.rhs().type(environment));
		translate(e.lhs(), slots, environment,bytecodes);
		convert(target,e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		convert(target,e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_BOOL,WHILEYSET);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "equals", ftype,
				Bytecode.VIRTUAL));
	}
	protected void translate(SetNotEquals e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		SetType target = (SetType) Types.leastUpperBound(e.lhs().type(environment),e.rhs().type(environment));
		translate(e.lhs(), slots, environment,bytecodes);
		convert(target,e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		convert(target,e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_BOOL,WHILEYSET);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "notEquals", ftype,
				Bytecode.VIRTUAL));		
	}
	protected void translate(Subset e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		SetType target = (SetType) Types.leastUpperBound(e.lhs().type(environment),e.rhs().type(environment));
		translate(e.lhs(), slots, environment,bytecodes);	
		convert(target,e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		convert(target,e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_BOOL,WHILEYSET);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "subset", ftype,
				Bytecode.VIRTUAL));				
	}
	protected void translate(SubsetEq e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		SetType target = (SetType) Types.leastUpperBound(e.lhs().type(environment),e.rhs().type(environment));
		translate(e.lhs(), slots, environment,bytecodes);
		convert(target,e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		convert(target,e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_BOOL,WHILEYSET);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "subsetEq", ftype,
				Bytecode.VIRTUAL));				
	}
	protected void translate(SetUnion e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		SetType target = (SetType) Types.leastUpperBound(e.lhs().type(environment),e.rhs().type(environment));				
		translate(e.rhs(), slots, environment,bytecodes);
		convert(target,e.rhs(), slots, environment,bytecodes);
		translate(e.lhs(), slots, environment,bytecodes);	
		convert(target,e.lhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(WHILEYSET,WHILEYSET);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "union", ftype,
				Bytecode.VIRTUAL));				
	}
	protected void translate(SetIntersection e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		SetType target = (SetType) Types.leastUpperBound(e.lhs().type(environment),e.rhs().type(environment));
		translate(e.rhs(), slots, environment,bytecodes);
		convert(target,e.rhs(), slots, environment,bytecodes);
		translate(e.lhs(), slots, environment,bytecodes);	
		convert(target,e.lhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(WHILEYSET,WHILEYSET);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "intersect", ftype,
				Bytecode.VIRTUAL));				
	}
	protected void translate(SetDifference e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		SetType target = (SetType) Types.leastUpperBound(e.lhs().type(environment),e.rhs().type(environment));
		translate(e.rhs(), slots, environment,bytecodes);
		convert(target,e.rhs(), slots, environment,bytecodes);
		translate(e.lhs(), slots, environment,bytecodes);	
		convert(target,e.lhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(WHILEYSET,WHILEYSET);
		bytecodes.add(new Bytecode.Invoke(WHILEYSET, "difference", ftype,
				Bytecode.VIRTUAL));				
	}
	protected void translate(SetComprehension e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {		
		String tmp = freshVar(slots);
		construct(WHILEYSET, slots, environment,bytecodes);
		bytecodes.add(new Bytecode.Store(slots.get(tmp), WHILEYSET));
		HashMap<String,Integer> nslots = new HashMap(slots);
		ArrayList<Pair<String,Expr>> nsources = new ArrayList();
		HashMap<String,Type> nenv = new HashMap(environment);
		for(Pair<String,Expr> p : e.sources()) {
			nsources.add(new Pair<String,Expr>(p.first(),p.second()));
			SetType st = (SetType) p.second().type(nenv);			
			nenv.put(p.first(), st.element());
			nslots.put(p.first(),nslots.size());
		}
		
		translateSetCompHelper(tmp,e,nsources, nslots, nenv, bytecodes);
		// Finally, load the result onto the stack
		bytecodes.add(new Bytecode.Load(slots.get(tmp), WHILEYSET));		
	}
	protected void translateSetCompHelper(String list, SetComprehension sc,
			ArrayList<Pair<String, Expr>> srcs,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {		
		
		if(srcs.size() == 0) {
			// base case --- evaluate condition and add value if true
			String falseLabel = freshLabel();
			if(sc.condition() != null) {
				translateCondition(new Not(sc.condition()), falseLabel,
					slots, environment, bytecodes);
			}
			// true case --- add value to list
			bytecodes.add(new Bytecode.Load(slots.get(list), JAVA_LANG_OBJECT));
			translate(sc.sign(), slots, environment,bytecodes);
			JvmType.Function ftype = new JvmType.Function(T_BOOL,JAVA_LANG_OBJECT);
			bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_COLLECTION, "add",
					ftype, Bytecode.INTERFACE));
			bytecodes.add(new Bytecode.Pop(T_BOOL));
			bytecodes.add(new Bytecode.Label(falseLabel));
		} else {
			// recursive case --- evaluate source and iterate
			String loopLabel = freshLabel();
			String exitLabel = freshLabel();			
			String iter = freshVar(slots);
			Pair<String,Expr> src = srcs.get(0);
			srcs.remove(0);
			translate(src.second(), slots, environment,bytecodes);			
			String srcVar = src.first();						
			Type srcType = flattern(src.second().type(environment));			
			Type elementType;
			if(srcType instanceof SetType) {
				elementType = ((SetType) srcType).element();
			} else {
				elementType = ((ListType) srcType).element();
			}
			
			JvmType.Function ftype = new JvmType.Function(JAVA_UTIL_ITERATOR);
			bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_COLLECTION, "iterator",
					ftype, Bytecode.INTERFACE));
			bytecodes.add(new Bytecode.Store(slots.get(iter),
					JAVA_UTIL_ITERATOR));
			bytecodes.add(new Bytecode.Label(loopLabel));
			ftype = new JvmType.Function(T_BOOL);
			bytecodes.add(new Bytecode.Load(slots.get(iter),JAVA_UTIL_ITERATOR));
			bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "hasNext",
					ftype, Bytecode.INTERFACE));
			bytecodes.add(new Bytecode.If(Bytecode.If.EQ, exitLabel));			
			bytecodes.add(new Bytecode.Load(slots.get(iter),JAVA_UTIL_ITERATOR));
			ftype = new JvmType.Function(JAVA_LANG_OBJECT);
			bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "next",
					ftype, Bytecode.INTERFACE));
			addReadConversion(elementType,bytecodes);					
			bytecodes.add(new Bytecode.Store(slots.get(srcVar),JAVA_LANG_OBJECT));
			
			translateSetCompHelper(list,sc,srcs, slots, environment,bytecodes);
			bytecodes.add(new Bytecode.Goto(loopLabel));
			bytecodes.add(new Bytecode.Label(exitLabel));		
		}
	}
	protected void translate(TupleVal expr,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);
		construct(WHILEYTUPLE, slots, environment,bytecodes);		
		for(Map.Entry<String,Value> e : expr.values().entrySet()) {
			Type et = e.getValue().type();
			bytecodes.add(new Bytecode.Dup(WHILEYTUPLE));
			bytecodes.add(new Bytecode.LoadConst(e.getKey()));
			translate(e.getValue(), slots, environment,bytecodes);
			addWriteConversion(et,bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE,"put",ftype,Bytecode.VIRTUAL));			
			bytecodes.add(new Bytecode.Pop(WHILEYTUPLE));
		}
	}
	protected void translate(TupleGenerator expr,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);
		construct(WHILEYTUPLE, slots, environment,bytecodes);		
		for(Map.Entry<String,Expr> e : expr.values().entrySet()) {
			Type et = e.getValue().type(environment);
			bytecodes.add(new Bytecode.Dup(WHILEYTUPLE));
			bytecodes.add(new Bytecode.LoadConst(e.getKey()));
			translate(e.getValue(), slots, environment,bytecodes);
			addWriteConversion(et,bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE,"put",ftype,Bytecode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(WHILEYTUPLE));
		}
	}
	protected void translate(TupleAccess e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.source(), slots, environment,bytecodes);
		// FIXME: new any type is a hack, but it works
		convert(Types.T_ANY,e.source(), slots, environment, bytecodes);
		bytecodes.add(new Bytecode.LoadConst(e.name()));
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE,"get",ftype,Bytecode.VIRTUAL));		
		Type et = e.type(environment);
		addReadConversion(et,bytecodes);				
	}
	protected void translate(TupleEquals e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_BOOL,WHILEYTUPLE);
		bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE, "equals", ftype,
				Bytecode.VIRTUAL));
	}
	protected void translate(TupleNotEquals e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		translate(e.lhs(), slots, environment,bytecodes);
		translate(e.rhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_BOOL,WHILEYTUPLE);
		bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE, "notEquals", ftype,
				Bytecode.VIRTUAL));
	}
	protected void translate(Invoke e, HashMap<String, Integer> slots, HashMap<String, Type> environment,
			ArrayList<Bytecode> bytecodes, boolean noReturn) throws ResolveError {

		// FIXME: this is clearly a hack for now!!!		
			
		ModuleID mid = e.module();		
		JvmType.Clazz owner = new JvmType.Clazz(mid.pkg().toString(),mid.module());
		FunType ft = e.funType();		
		Type receiver = null;
		if(e.target() != null) {
			translate(e.target(), slots, environment, bytecodes);
			receiver = e.target().type(environment);			
		}
		String name = nameMangle(e.name(),receiver,ft);
		JvmType.Function jft = convertType(receiver,ft);
		List<Type> params = ft.parameters();
		List<Expr> args = e.arguments();
		
		for (int i=0;i!=params.size();++i) {
			Expr p = args.get(i);
			Type pt = params.get(i);
			translate(p, slots, environment, bytecodes);
			convert(pt,p, slots, environment, bytecodes);					
			// Now, for list, set and tuple types we need to clone the object in
			// question. In fact, this could be optimised in some situations
			// where we know the old variable is not live.
			Type t = p.type(environment);
			if(t instanceof ListType) {
				JvmType.Function ftype = new JvmType.Function(WHILEYLIST);
				bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "clone", ftype,
						Bytecode.VIRTUAL));				
			} else if(t instanceof SetType) {
				JvmType.Function ftype = new JvmType.Function(WHILEYSET);
				bytecodes.add(new Bytecode.Invoke(WHILEYSET, "clone", ftype,
						Bytecode.VIRTUAL));				
			} else if(t instanceof TupleType) {
				JvmType.Function ftype = new JvmType.Function(WHILEYTUPLE);
				bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE, "clone", ftype,
						Bytecode.VIRTUAL));				
			}
		}

		bytecodes.add(new Bytecode.Invoke(owner,name,jft,Bytecode.STATIC));
		
		if (noReturn && !(ft.returnType() == Types.T_VOID)) {
			// FIXME: broken for bool types
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		}
	}
	
	protected void translate(Variable v,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {		
		Type t = environment.get(v.name());
		int slot = slots.get(v.name());	
		if(t == Types.T_BOOL) {
			bytecodes.add(new Bytecode.Load(slot,T_BOOL));
		} else {
			bytecodes.add(new Bytecode.Load(slot,JAVA_LANG_OBJECT));
		}
	}
	
	protected void translate(Spawn v,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {				
		bytecodes.add(new Bytecode.New(WHILEYPROCESS));
		bytecodes.add(new Bytecode.Dup(WHILEYPROCESS));
		translate(v.mhs(), slots, environment,bytecodes);				
		JvmType.Function ftype = new JvmType.Function(T_VOID,JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYPROCESS, "<init>", ftype,
				Bytecode.SPECIAL));
	}
	protected void translate(TypeGate e,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {
		//translate(e.lhs(), slots, environment,bytecodes);			
		// FIXME: total hack for now
		bytecodes.add(new Bytecode.LoadConst(new Integer(0)));
		String exitLabel = freshLabel();
		String trueLabel = freshLabel();
		bytecodes.add(new Bytecode.If(Bytecode.If.EQ,trueLabel));
		bytecodes.add(new Bytecode.LoadConst(0));
		bytecodes.add(new Bytecode.Goto(exitLabel));
		bytecodes.add(new Bytecode.Label(trueLabel));
		translate(e.rhs(), slots, environment,bytecodes);
		bytecodes.add(new Bytecode.Label(exitLabel));		
	}
	protected void translate(ProcessAccess a,
			HashMap<String, Integer> slots, HashMap<String, Type> environment, ArrayList<Bytecode> bytecodes) {				
		translate(a.mhs(), slots, environment,bytecodes);
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT);		
		bytecodes.add(new Bytecode.Invoke(WHILEYPROCESS, "state", ftype,
				Bytecode.VIRTUAL));
		// finally, we need to cast the object we got back appropriately.		
		ProcessType pt = (ProcessType) a.mhs().type(environment);
		addReadConversion(pt.element(), bytecodes);	
	}
		
	public void addReadConversion(Type et, ArrayList<Bytecode> bytecodes) {
		if(et == Types.T_BOOL) {
			bytecodes.add(new Bytecode.CheckCast(JAVA_LANG_BOOLEAN));
			JvmType.Function ftype = new JvmType.Function(T_BOOL);
			bytecodes.add(new Bytecode.Invoke(JAVA_LANG_BOOLEAN,
					"booleanValue", ftype, Bytecode.VIRTUAL));
		} else {
			bytecodes.add(new Bytecode.CheckCast(convertType(et)));
		}
	}
	
	public void addWriteConversion(Type et, ArrayList<Bytecode> bytecodes) {
		if(et == Types.T_BOOL) {
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_BOOLEAN,T_BOOL);
			bytecodes.add(new Bytecode.Invoke(JAVA_LANG_BOOLEAN,
					"valueOf", ftype, Bytecode.STATIC));
		} 
	}
	
	public void construct(JvmType.Clazz owner,
			HashMap<String, Integer> slots, HashMap<String, Type> environment,
			ArrayList<Bytecode> bytecodes, Expr... params) {
		bytecodes.add(new Bytecode.New(owner));		
		bytecodes.add(new Bytecode.Dup(owner));
		ArrayList<JvmType> paramTypes = new ArrayList<JvmType>();
		for(Expr e : params) {			
			translate(e,slots,environment,bytecodes);
			paramTypes.add(convertType(e.type(environment)));
		}
		JvmType.Function ftype = new JvmType.Function(T_VOID,paramTypes);
		
		// call the appropriate constructor
		bytecodes.add(new Bytecode.Invoke(owner, "<init>", ftype,
				Bytecode.SPECIAL));

	}	

	protected void convert(Type toType, Expr from,
			HashMap<String, Integer> slots, HashMap<String, Type> environment,
			ArrayList<Bytecode> bytecodes) {				
		Type fromType = from.type(environment);			
		
		convert(toType, fromType, slots, bytecodes);
	}
	
	protected void convert(Type toType, Type fromType,
			HashMap<String, Integer> slots, ArrayList<Bytecode> bytecodes) {		
		
		if(toType.equals(fromType)) {		
			// do nothing!			
		} else if (!(toType == Types.T_BOOL) && fromType == Types.T_BOOL) {
			// this is either going into a union type, or the any type
			convert(toType, (BoolType) fromType, slots, bytecodes);
		} else if(toType == Types.T_REAL && fromType == Types.T_INT) {			
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
	
	public JvmType.Function convertType(Type receiver, FunType ft) {
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
		} else if(t == Types.T_BOOL) {
			return T_BOOL;
		} else if(t == Types.T_INT) {
			return BIG_INTEGER;
		} else if(t == Types.T_REAL) {
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
		} else {
			throw new RuntimeException("unknown type encountered: " + t);
		}		
	}	
	
	public Condition simplify(Condition c) {
		return simplifier.simplify(c);
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
	
	protected String nameMangle(String name, Type receiver, FunType type) {
		if (receiver != null) {
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
		} else if(t == Types.T_BOOL) {
			return "B";
		} else if(t == Types.T_INT) {
			return "I";
		} else if(t == Types.T_REAL) {
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
		} else if(t instanceof FunType) {
			FunType ft = (FunType) t;
			String r = type2str(ft.returnType());
			for(Type pt : ft.parameters()) {
				r += type2str(pt);
			}
			return r;
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
