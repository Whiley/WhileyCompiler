// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF Type.Selector.TOP KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyil.util;

import static wyil.lang.WyilFile.*;

import java.util.*;

import wybs.lang.Build;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyil.lang.WyilFile.Type.*;
import wyil.lang.WyilFile.Type.Byte;
import wyil.lang.WyilFile.Type.Void;

public class WyilUtils {

	/**
	 * Determine whether a given type is <i>pure</i> or not. That is, whether or not
	 * it contains a reference type. For example, <code>int</code> and
	 * <code>{int f}</code> are pure, whilst <code>&int</code> and
	 * <code>{&int f}</code> are not.
	 *
	 * @param type
	 * @return
	 */
	public static boolean isPure(Type type) {
		return new AbstractTypeReduction<Boolean>() {

			@Override
			public Boolean constructTypeArray(Array type, Boolean child) {
				return child;
			}

			@Override
			public Boolean constructTypeBool(Bool type) {
				return true;
			}

			@Override
			public Boolean constructTypeByte(Byte type) {
				return true;
			}

			@Override
			public Boolean constructTypeInt(Int type) {
				return true;
			}

			@Override
			public Boolean constructTypeFunction(Function type, Boolean param, Boolean ret) {
				return param && ret;
			}

			@Override
			public Boolean constructTypeMethod(Method type, Boolean param, Boolean ret) {
				return param && ret;
			}

			@Override
			public Boolean constructTypeNominal(Nominal type, Boolean child) {
				return (child == null) ? true : child;
			}

			@Override
			public Boolean constructTypeNull(Null type) {
				return true;
			}

			@Override
			public Boolean constructTypeProperty(Property type, Boolean param, Boolean ret) {
				return param && ret;
			}

			@Override
			public Boolean constructTypeRecord(Type.Record type, List<Boolean> children) {
				return AND(children);
			}

			@Override
			public Boolean constructTypeReference(Reference type, Boolean child) {
				return false;
			}

			@Override
			public Boolean constructTypeTuple(wyil.lang.WyilFile.Type.Tuple type, List<Boolean> children) {
				return AND(children);
			}

			@Override
			public Boolean constructTypeUnion(Union type, List<Boolean> children) {
				return AND(children);
			}

			@Override
			public Boolean constructTypeUnresolved(Unknown type) {
				return true;
			}

			@Override
			public Boolean constructTypeVoid(Void type) {
				return true;
			}

			@Override
			public Boolean constructTypeVariable(Universal type) {
				return true;
			}

			private boolean AND(List<Boolean> items) {
				boolean r = true;
				for(int i=0;i!=items.size();++i) {
					r &= items.get(i);
				}
				return r;
			}

		}.apply(type);
	}

	/**
	 * Check whether a given assignment is "simple" or not. That is, assigns only a
	 * single location on the left-hand side.
	 *
	 * @param stmt
	 * @return
	 */
	public static boolean isSimple(Stmt.Assign stmt) {
		Tuple<LVal> lhs = stmt.getLeftHandSide();

		for (int i = 0; i != lhs.size(); ++i) {
			if (i > 0) {
				return false;
			} else if (lhs.get(i).getType().shape() > 1) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Check whether a given assignment "has interference" or not. That is, where
	 * variable which are defined are also used in the right-hand side. The
	 * following illustrates interference between the lhs and rhs:
	 *
	 * <pre>
	 * x,y = y,x
	 * </pre>
	 *
	 * Interference means that locations assigned on the left-hand side are
	 * (potentially) present on the right-hand side. To understand why this is a
	 * problem, consider this naive translation (of the above) into Boogie:
	 *
	 * <pre>
	 * x = y;
	 * y = x;
	 * </pre>
	 *
	 * This obviously doesn't work because <code>x</code> is assigned a new value
	 * before its original value is assigned to <code>y</code>. To work around this,
	 * we need to introduce temporary variables.
	 *
	 * @param stmt
	 * @return
	 */
	public static boolean hasInterference(Stmt.Assign stmt, Build.Meter meter) {
		Tuple<LVal> lhs = stmt.getLeftHandSide();
		Tuple<Expr> rhs = stmt.getRightHandSide();
		//
		HashSet<Decl.Variable> uses = new HashSet<>();
		HashSet<Decl.Variable> defs = new HashSet<>();
		// Identify all defs and uses
		for (int i = 0; i != lhs.size(); ++i) {
			LVal lv = lhs.get(i);
			if (!extractDefinedVariable(lhs.get(i), defs, meter)) {
				// Couldn't tell what was being defined.
				return true;
			}
			extractUsedVariables(lhs.get(i), uses, meter);
			extractUsedVariables(rhs.get(i), uses, meter);
		}
		// Check for interference
		for (Decl.Variable def : defs) {
			if (uses.contains(def)) {
				// Interference detected
				return true;
			}
		}
		//
		return false;
	}

	private static boolean extractDefinedVariable(LVal lval, Set<Decl.Variable> defs, Build.Meter meter) {
		switch (lval.getOpcode()) {
		case EXPR_arrayaccess:
		case EXPR_arrayborrow: {
			Expr.ArrayAccess e = (Expr.ArrayAccess) lval;
			return extractDefinedVariable((LVal) e.getFirstOperand(), defs, meter);
		}
		case EXPR_fielddereference:
		case EXPR_dereference: {
			// NOTE: it's impossible to tell what variable is being defined through a
			// dereference.
			return false;
		}
		case EXPR_tupleinitialiser: {
			Expr.TupleInitialiser e = (Expr.TupleInitialiser) lval;
			Tuple<Expr> operands = e.getOperands();
			for (int i = 0; i != operands.size(); ++i) {
				if (!extractDefinedVariable((LVal) operands.get(i), defs, meter)) {
					return false;
				}
			}
			return true;
		}
		case EXPR_recordaccess:
		case EXPR_recordborrow: {
			Expr.RecordAccess e = (Expr.RecordAccess) lval;
			return extractDefinedVariable((LVal) e.getOperand(), defs, meter);
		}
		case EXPR_variablecopy:
		case EXPR_variablemove: {
			Expr.VariableAccess e = (Expr.VariableAccess) lval;
			defs.add(e.getVariableDeclaration());
			return true;
		}
		default:
			throw new IllegalArgumentException("invalid lval: " + lval);
		}
	}

	private static void extractUsedVariables(LVal lval, Set<Decl.Variable> uses, Build.Meter meter) {
		switch (lval.getOpcode()) {
		case EXPR_arrayaccess:
		case EXPR_arrayborrow: {
			Expr.ArrayAccess e = (Expr.ArrayAccess) lval;
			extractUsedVariables((LVal) e.getFirstOperand(), uses, meter);
			extractUsedVariables(e.getSecondOperand(), uses, meter);
			break;
		}
		case EXPR_dereference: {
			Expr.Dereference e = (Expr.Dereference) lval;
			extractUsedVariables(e.getOperand(), uses, meter);
			break;
		}
		case EXPR_recordaccess:
		case EXPR_recordborrow: {
			Expr.RecordAccess e = (Expr.RecordAccess) lval;
			extractUsedVariables((LVal) e.getOperand(), uses, meter);
			break;
		}
		case EXPR_tupleinitialiser: {
			Expr.TupleInitialiser e = (Expr.TupleInitialiser) lval;
			Tuple<Expr> operands = e.getOperands();
			for (int i = 0; i != operands.size(); ++i) {
				extractUsedVariables((LVal) operands.get(i), uses, meter);
			}
			break;
		}
		case EXPR_variablecopy:
		case EXPR_variablemove: {
			// NOTE: nothing to do here, since this variable is being defined.
			break;
		}
		default:
			throw new IllegalArgumentException("invalid lval: " + lval);
		}
	}

	/**
	 * Extract all used variables from a given statement. This is tricky as we must
	 * account properly for captured variables, etc.
	 *
	 * @param e
	 * @param uses
	 */
	private static void extractUsedVariables(Stmt e, Set<Decl.Variable> uses, Build.Meter meter) {
		// Construct appropriate visitor
		AbstractVisitor visitor = new AbstractVisitor(meter) {
			@Override
			public void visitVariableAccess(Expr.VariableAccess e) {
				uses.add(e.getVariableDeclaration());
			}

			@Override
			public void visitDeclaration(Decl d) {
				// NOTE: this is needed to prevent traversal into type invariants, etc.
				if (d instanceof Decl.Lambda) {
					// NOTE: must account for variable capture here
					Decl.Lambda l = (Decl.Lambda) d;
					HashSet<Decl.Variable> tmp = new HashSet<>();
					// Traverse quantify body
					super.visitLambda(l);
					// Remove all captured variables
					for (Decl.Variable v : l.getParameters()) {
						tmp.remove(v);
					}
					// Done
					uses.addAll(tmp);
				}
			}

			@Override
			public void visitUniversalQuantifier(Expr.UniversalQuantifier q) {
				// NOTE: must account for variable capture here
				HashSet<Decl.Variable> tmp = new HashSet<>();
				// Traverse quantify body
				super.visitUniversalQuantifier(q);
				// Remove all captured variables
				for (Decl.Variable v : q.getParameters()) {
					tmp.remove(v);
				}
				// Done
				uses.addAll(tmp);
			}

			@Override
			public void visitExistentialQuantifier(Expr.ExistentialQuantifier q) {
				// NOTE: must account for variable capture here
				HashSet<Decl.Variable> tmp = new HashSet<>();
				// Traverse quantify body
				super.visitExistentialQuantifier(q);
				// Remove all captured variables
				for (Decl.Variable v : q.getParameters()) {
					tmp.remove(v);
				}
				// Done
				uses.addAll(tmp);
			}

			@Override
			public void visitType(Type t) {
				// NOTE: stop traversal here since we cannot reach any variable declarations
				// from here. This is just an optimisation.
			}
		};
		//
		visitor.visitStatement(e);
	}

	/**
	 * Determine the set of variables used within a given statement, whilst properly accounting for variable capture, etc.
	 *
	 * @param stmt
	 * @param meter
	 * @return
	 */
	public static Set<Decl.Variable> determineUsedVariables(Stmt stmt, Build.Meter meter) {
		HashSet<Decl.Variable> used = new HashSet<>();
		extractUsedVariables(stmt, used, meter);
		return used;
	}

	/**
	 * Determine all (universal) type variables used within a given type.  For example, in the type <code>{ int msg, T data}</code>
	 * we have variable <code>T</code>.  This method is used in situations where we need to handle free variables (e.g.
	 * by adding template arguments).
	 *
	 * @param t
	 * @return
	 */
	public static Tuple<Template.Variable> extractTemplate(Type type, Build.Meter meter) {
		HashSet<Template.Variable> holes = new HashSet<>();
		// Traverse the type looking for universals
		new AbstractVisitor(meter) {
			@Override
			public void visitTypeVariable(Type.Universal t) {
				// FIXME: unsure what the appropriate variance would be.
				holes.add(new Template.Type(t.getOperand(), Template.Variance.INVARIANT));
			}
		}.visitType(type);
		// Extract and sort holes
		ArrayList<Template.Variable> nholes = new ArrayList<>(holes);
		Collections.sort(nholes, new Comparator<Template.Variable>() {
			@Override
			public int compare(Template.Variable t1, Template.Variable t2) {
				return t1.getName().compareTo(t2.getName());
			}
		});
		// Done
		return new Tuple<>(nholes);
	}
}
