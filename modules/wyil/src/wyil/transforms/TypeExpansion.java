package wyil.transforms;


import static wyc.lang.WhileyFile.syntaxError;
import static wyil.util.ErrorMessages.UNKNOWN_VARIABLE;
import static wyil.util.ErrorMessages.errorMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wyc.builder.CodeGenerator.Environment;
import wyc.lang.Expr;
import wyc.lang.WhileyFile.Context;
import wycc.lang.NameID;
import wycc.util.ResolveError;
import wyfs.lang.Path;
import wyil.lang.Code;
import wyil.lang.CodeUtils;
import wyil.lang.Codes;
import wyil.lang.Constant;
import wyil.lang.Type;
import wyil.lang.WyilFile;

public class TypeExpansion {


	/**
	 * The builder is needed to provide access to external resources (i.e.
	 * external WyIL files compiled separately). This is required for expanding
	 * types and their constraints in certain situations, such as runtime type
	 * tests (e.g. <code>x is T</code> where <code>T</code> is defined
	 * externally).
	 */
	private final WhileyBuilder builder;

	/**
	 * The name cache stores the translations of any code associated with a
	 * named type or constant, which was previously computed.
	 */
	private final HashMap<NameID,Code.AttributableBlock> cache = new HashMap<NameID,Code.AttributableBlock>();


	public Code.AttributableBlock generate(NameID nid) throws Exception {
		Code.AttributableBlock blk = cache.get(nid);
		if(blk == EMPTY_BLOCK) {
			return null;
		} else if(blk != null) {
			return blk;
		}

		// check whether the item in question is in one of the source
		// files being compiled.
		Path.ID mid = nid.module();
		WhileyFile wf = builder.getSourceFile(mid);
		if(wf != null) {
			// FIXME: the following line is necessary to terminate infinite
			// recursion. However, we really need to do better in the
			// context of recursive types with constraints.

			WhileyFile.Type td = wf.typeDecl(nid.name());
			if(td != null) {
				cache.put(nid, EMPTY_BLOCK);
				blk = generate(td.pattern.toSyntacticType(),td);
				if(td.invariant != null) {
					if(blk == null) {
						blk = new Code.AttributableBlock();
					}
					// Setup the environment which maps source variables to block
					// registers. This is determined by allocating the root variable to
					// register 0, and then creating any variables declared in the type
					// pattern by from this root.
					Environment environment = new Environment();
					int root = environment.allocate(td.resolvedType.raw());
					addDeclaredVariables(root, td.pattern,
							td.resolvedType.raw(), environment, blk);
					String lab = CodeUtils.freshLabel();
					generateCondition(lab, td.invariant, environment, blk, td);
					blk.add(Codes.Fail("constraint not satisfied"));
					blk.add(Codes.Label(lab));
				}
				cache.put(nid, blk);
				return blk;
			} else {
				Constant v = resolver.resolveAsConstant(nid);
				if (v instanceof Constant.Set) {
					Constant.Set vs = (Constant.Set) v;
					Type.Set type = vs.type();
					blk = new Code.AttributableBlock();
					String lab = CodeUtils.freshLabel();
					blk.add(Codes.Const(Codes.REG_1, v));
					blk.add(Codes.If(vs.type(), Codes.REG_0, Codes.REG_1,
							Codes.Comparator.IN, lab));
					blk.add(Codes.Fail("constraint not satisfied"));
					blk.add(Codes.Label(lab));
					cache.put(nid, blk);
					return blk;
				}
			}
		} else {
			// now check whether it's already compiled and available on the
			// WHILEYPATH.
			WyilFile m = builder.getModule(mid);
			WyilFile.TypeDeclaration td = m.type(nid.name());
			if(td != null && td.invariant() != null) {
				// should I cache this?
				return td.invariant();
			} else {
				return null;
			}
		}

		// FIXME: better error message?
		throw new ResolveError("name not found: " + nid);
	}

	public Code.AttributableBlock generate(SyntacticType t, Context context) throws Exception {
		Nominal nt = resolver.resolveAsType(t, context);
		Type raw = nt.raw();
		if (t instanceof SyntacticType.List) {
			SyntacticType.List lt = (SyntacticType.List) t;
			Code.AttributableBlock blk = generate(lt.element, context);
			if (blk != null) {
				Code.AttributableBlock nblk = new Code.AttributableBlock();
				String label = CodeUtils.freshLabel();
				nblk.add(Codes.ForAll((Type.EffectiveCollection) raw,
						Codes.REG_0, Codes.REG_1, Collections.EMPTY_LIST, label),
						attributes(t));
				nblk.addAll(shiftBlock(1, blk));
				// Must add NOP before loop end to ensure labels at the boundary
				// get written into Wyil files properly. See Issue #253.
				nblk.add(Codes.Nop);
				nblk.add(Codes.LoopEnd(label));
				blk = nblk;
			}
			return blk;
		} else if (t instanceof SyntacticType.Set) {
			SyntacticType.Set st = (SyntacticType.Set) t;
			Code.AttributableBlock blk = generate(st.element, context);
			if (blk != null) {
				Code.AttributableBlock nblk = new Code.AttributableBlock();
				String label = CodeUtils.freshLabel();
				nblk.add(Codes.ForAll((Type.EffectiveCollection) raw,
						Codes.REG_0, Codes.REG_1, Collections.EMPTY_LIST, label),
						attributes(t));
				nblk.addAll(shiftBlock(1, blk));
				// Must add NOP before loop end to ensure labels at the boundary
				// get written into Wyil files properly. See Issue #253.
				nblk.add(Codes.Nop);
				nblk.add(Codes.LoopEnd(label));
				blk = nblk;
			}
			return blk;
		} else if (t instanceof SyntacticType.Map) {
			SyntacticType.Map st = (SyntacticType.Map) t;
			Code.AttributableBlock blk = null;
			// FIXME: put in constraints. REQUIRES ITERATION OVER DICTIONARIES
			Code.AttributableBlock key = generate(st.key, context);
			Code.AttributableBlock value = generate(st.value, context);
			return blk;
		} else if (t instanceof SyntacticType.Tuple) {
			// At the moment, a tuple is compiled down to a WyIL record.
			SyntacticType.Tuple tt = (SyntacticType.Tuple) t;
			Type.EffectiveTuple ett = (Type.EffectiveTuple) raw;
			List<Type> ettElements = ett.elements();
			Code.AttributableBlock blk = null;

			int i = 0;
			for (SyntacticType e : tt.types) {
				Code.AttributableBlock p = generate(e, context);
				if (p != null) {
					if (blk == null) {
						blk = new Code.AttributableBlock();
					}
					blk.add(Codes.TupleLoad(ett, Codes.REG_1, Codes.REG_0, i),
							attributes(t));
					blk.addAll(shiftBlock(1, p));
				}
				i = i + 1;
			}

			return blk;
		} else if (t instanceof SyntacticType.Record) {
			SyntacticType.Record tt = (SyntacticType.Record) t;
			Type.EffectiveRecord ert = (Type.EffectiveRecord) raw;
			Map<String,Type> fields = ert.fields();
			Code.AttributableBlock blk = null;
			for (Map.Entry<String, SyntacticType> e : tt.types.entrySet()) {
				Code.AttributableBlock p = generate(e.getValue(), context);
				if (p != null) {
					if (blk == null) {
						blk = new Code.AttributableBlock();
					}
					blk.add(Codes.FieldLoad(ert, Codes.REG_1, Codes.REG_0,
							e.getKey()), attributes(t));
					blk.addAll(shiftBlock(1, p));
				}
			}
			return blk;
		} else if (t instanceof SyntacticType.Union) {
			SyntacticType.Union ut = (SyntacticType.Union) t;

			boolean constraints = false;
			DecisionTree tree = new DecisionTree(raw);

			for (SyntacticType b : ut.bounds) {
				Type type = resolver.resolveAsType(b, context).raw();
				Code.AttributableBlock constraint = generate(b, context);
				constraints |= constraint != null;
				tree.add(type,constraint);
			}

			if(constraints) {
				return tree.flattern();
			} else {
				// no constraints, must not do anything!
				return null;
			}
		} else if (t instanceof SyntacticType.Negation) {
			SyntacticType.Negation st = (SyntacticType.Negation) t;
			Code.AttributableBlock p = generate(st.element, context);
			Code.AttributableBlock blk = null;
			// TODO: need to fix not constraints
			return blk;
		} else if (t instanceof SyntacticType.Intersection) {
			SyntacticType.Intersection ut = (SyntacticType.Intersection) t;
			Code.AttributableBlock blk = null;
			for (int i = 0; i != ut.bounds.size(); ++i) {
				SyntacticType b = ut.bounds.get(i);
				Code.AttributableBlock p = generate(b, context);
				// TODO: add intersection constraints
			}
			return blk;
		} else if (t instanceof SyntacticType.Reference) {
			SyntacticType.Reference ut = (SyntacticType.Reference) t;
			Code.AttributableBlock blk = generate(ut.element, context);
			// TODO: fix process constraints
			return null;
		} else if (t instanceof SyntacticType.Nominal) {
			SyntacticType.Nominal dt = (SyntacticType.Nominal) t;

			try {
				NameID nid = resolver.resolveAsName(dt.names,context);
				Code.AttributableBlock other = generate(nid);
				if(other != null) {
					Code.AttributableBlock blk = new Code.AttributableBlock();
					blk.addAll(other);
					return blk;
				} else {
					return null;
				}
			} catch (ResolveError rex) {
				syntaxError(rex.getMessage(), context, t, rex);
				return null;
			}
		} else {
			// for base cases
			return null;
		}
	}

	/**
	 * <p>
	 * Translate a source-level condition which represents a runtime type test
	 * (e.g. <code>x is int</code>) into WyIL bytecodes, using a given
	 * environment mapping named variables to slots. One subtlety of this arises
	 * when the lhs is a single variable. In this case, the variable will be
	 * retyped and, in order for this to work, we *must* perform the type test
	 * on the actual varaible, rather than a temporary.
	 * </p>
	 *
	 * @param target
	 *            --- Target label to goto if condition is true. When the
	 *            condition is false, control falls simply through to the next
	 *            bytecode in sqeuence.
	 * @param condition
	 *            --- Source-level condition to be translated into a sequence of
	 *            one or more conditional branches.
	 * @param environment
	 *            --- Mapping from variable names to to slot numbers.
	 * @param codes
	 *            --- List of bytecodes onto which translation should be
	 *            appended.
	 * @return
	 */
	private void generateTypeCondition(String target, Expr.BinOp v,
			Environment environment, Code.AttributableBlock codes, Context context) throws Exception {
		int leftOperand;

		if (v.lhs instanceof Expr.LocalVariable) {

			// This is the case where the lhs is a single variable and, hence,
			// will be retyped by this operation. In this case, we must operate
			// on the original variable directly, rather than a temporary
			// variable (since, otherwise, we'll retype the temporary but not
			// the intended variable).
			Expr.LocalVariable lhs = (Expr.LocalVariable) v.lhs;
			if (environment.get(lhs.var) == null) {
				syntaxError(errorMessage(UNKNOWN_VARIABLE), context, v.lhs);
			}
			leftOperand = environment.get(lhs.var);
		} else {
			// This is the general case whether the lhs is an arbitrary variable
			// and, hence, retyping does not apply. Therefore, we can simply
			// evaluate the lhs into a temporary register as per usual.
			leftOperand = generate(v.lhs, environment, codes, context);
		}

		// Note, the type checker guarantees that the rhs is a type val, so the
		// following cast is always safe.
		Expr.TypeVal rhs = (Expr.TypeVal) v.rhs;

		Code.AttributableBlock constraint = generate(rhs.unresolvedType, context);
		if (constraint != null) {
			String exitLabel = CodeUtils.freshLabel();
			Type glb = Type.intersect(v.srcType.raw(),
					Type.Negation(rhs.type.raw()));

			if (glb != Type.T_VOID) {
				// Only put the actual type test in if it is necessary.
				String nextLabel = CodeUtils.freshLabel();

				// FIXME: should be able to just test the glb here and branch to
				// exit label directly. However, this currently doesn't work
				// because of limitations with intersection of open records.

				codes.add(Codes.IfIs(v.srcType.raw(), leftOperand,
						rhs.type.raw(), nextLabel), attributes(v));
				codes.add(Codes.Goto(exitLabel));
				codes.add(Codes.Label(nextLabel));
			}
			constraint = shiftBlockExceptionZero(environment.size() - 1,
					leftOperand, constraint);
			codes.addAll(chainBlock(exitLabel, constraint));
			codes.add(Codes.Goto(target));
			codes.add(Codes.Label(exitLabel));
		} else {
			codes.add(Codes.IfIs(v.srcType.raw(), leftOperand,
					rhs.type.raw(), target), attributes(v));
		}
	}

	/**
	 * The chainBlock method takes a block and replaces every fail statement
	 * with a goto to a given label. This is useful for handling constraints in
	 * union types, since if the constraint is not met that doesn't mean its
	 * game over.
	 *
	 * @param target
	 * @param blk
	 * @return
	 */
	private static Code.AttributableBlock chainBlock(String target, Code.AttributableBlock blk) {

		// FIXME: The basic problem with this now is that it doesn't work since
		// blocks are nested

		Code.AttributableBlock nblock = new Code.AttributableBlock();
		for (Code c : blk) {
			if (c instanceof Codes.Fail) {
				nblock.add(Codes.Goto(target));
			} else {
				nblock.add(c, e.attributes());
			}
		}
		return CodeUtils.relabel(nblock);
	}


	/**
	 * The shiftBlock method takes a block and shifts every slot a given amount
	 * to the right. The number of inputs remains the same. This method is used
	 *
	 * @param amount
	 * @param blk
	 * @return
	 */
	private static Code.AttributableBlock shiftBlock(int amount, Code.AttributableBlock blk) {
		HashMap<Integer,Integer> binding = new HashMap<Integer,Integer>();
		for(int i=0;i!=blk.numSlots();++i) {
			binding.put(i,i+amount);
		}
		Code.AttributableBlock nblock = new Code.AttributableBlock();
		for(Code.AttributableBlock.Entry e : blk) {
			Code code = e.code.remap(binding);
			nblock.add(code,e.attributes());
		}
		return CodeUtils.relabel(nblock);
	}

	/**
	 * The shiftBlock method takes a block and shifts every slot a given amount
	 * to the right. The number of inputs remains the same. This method is used
	 *
	 * @param amount
	 * @param blk
	 * @return
	 */
	private static Code.AttributableBlock shiftBlockExceptionZero(int amount,
			int zeroDest, Code.AttributableBlock blk) {
		HashMap<Integer, Integer> binding = new HashMap<Integer, Integer>();
		for (int i = 1; i < blk.numSlots(); ++i) {
			binding.put(i, i + amount);
		}
		binding.put(0, zeroDest);

		Code.AttributableBlock nblock = new Code.AttributableBlock();
		for (Code.AttributableBlock.Entry e : blk) {
			Code code = e.code.remap(binding);
			nblock.add(code, e.attributes());
		}
		return CodeUtils.relabel(nblock);
	}

	private static final Code.AttributableBlock EMPTY_BLOCK = new Code.AttributableBlock();

	public static final class Environment {
		private final HashMap<String, Integer> var2idx;
		private final ArrayList<Type> idx2type;

		public Environment() {
			var2idx = new HashMap<String, Integer>();
			idx2type = new ArrayList<Type>();
		}

		public Environment(Environment env) {
			var2idx = new HashMap<String, Integer>(env.var2idx);
			idx2type = new ArrayList<Type>(env.idx2type);
		}

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

		public int size() {
			return idx2type.size();
		}

		public Integer get(String v) {
			return var2idx.get(v);
		}

		public String get(int idx) {
			for (Map.Entry<String, Integer> e : var2idx.entrySet()) {
				int jdx = e.getValue();
				if (jdx == idx) {
					return e.getKey();
				}
			}
			return null;
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
