// Copyright (c) 2012, David J. Pearce (djp@ecs.vuw.ac.nz)
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

package wyil.checks;

import static wybs.lang.SyntaxError.internalFailure;
import static wybs.lang.SyntaxError.syntaxError;
import static wycs.Solver.*;
import static wyil.util.ErrorMessages.errorMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import wyautl.core.Automaton;
import wyautl.util.BigRational;
import wybs.lang.*;
import wyil.lang.*;
import wyil.util.ErrorMessages;

/**
 * Responsible for converting a given Wyil bytecode into an appropriate
 * constraint which encodes its semantics.
 * 
 * @author David J. Pearce
 * 
 */
public class VerificationTransformer {
	private final Builder builder;
	private final WyilFile.Case method;
	private final String filename;
	private final boolean assume;
	private final boolean debug;

	public VerificationTransformer(Builder builder, WyilFile.Case method,
			String filename, boolean assume, boolean debug) {
		this.builder = builder;
		this.filename = filename;
		this.assume = assume;
		this.debug = debug;
		this.method = method;
	}

	public String filename() {
		return filename;
	}


	public void end(VerificationBranch.LoopScope scope, VerificationBranch branch) {
		// not sure what really needs to be done here, in fact.
	}


	public void exit(VerificationBranch.LoopScope scope, VerificationBranch branch) {
		for(int i : scope.constraints) {
			branch.assume(i);
		}
	}

	private static int counter = 0;

	public void end(VerificationBranch.ForScope scope, VerificationBranch branch) {
		// we need to build up a quantified formula here.

		Automaton automaton = branch.automaton();
		int root = And(automaton,scope.constraints);
		int qvar = QVar(automaton,"X" + counter++);
		root = automaton.substitute(root, scope.index, qvar);
		branch.assume(ForAll(automaton,qvar,scope.source,root));
	}

	public void exit(VerificationBranch.ForScope scope,
			VerificationBranch branch) {
		
		Automaton automaton = branch.automaton();
		int root = And(automaton, scope.constraints);
		int qvar = QVar(automaton, "X" + counter++);
		root = automaton.substitute(root, scope.index, qvar);
		branch.assume(Exists(automaton, qvar, scope.source, root));
	}

	public void exit(VerificationBranch.TryScope scope, VerificationBranch branch) {
		
	}
	
	protected void transform(Code.Assert code, VerificationBranch branch) {
		int test;
		
		if (assume) {
			test = buildTest(code.op, code.leftOperand, code.rightOperand,
					code.type, branch);
			branch.assume(test);
		} else {
			// At this point, what we do is invert the condition being asserted
			// and check that it is unsatisfiable.
			Code.Comparator cop = Code.invert(code.op);
			if(cop != null) {
				test = buildTest(cop, code.leftOperand, code.rightOperand,
						code.type, branch);
				
				if (!branch.assertFalse(test, debug)) {		
					syntaxError(code.msg, filename, branch.entry());
				}
			} else {
				// fall back
				test = buildTest(code.op, code.leftOperand, code.rightOperand,
						code.type, branch);
				if (!branch.assertTrue(test, debug)) {		
					syntaxError(code.msg, filename, branch.entry());
				}
			}
		}
	}
	
	protected void transform(Code.Assume code, VerificationBranch branch) {
		// At this point, what we do is invert the condition being asserted and
		// check that it is unsatisfiable.
		int test = buildTest(code.op, code.leftOperand, code.rightOperand,
				code.type, branch);
		branch.assume(test);		
	}

	protected void transform(Code.Assign code, VerificationBranch branch) {
		branch.write(code.target, branch.read(code.operand));
	}

	protected void transform(Code.BinArithOp code, VerificationBranch branch) {
		Automaton automaton = branch.automaton();
		int lhs = branch.read(code.leftOperand);
		int rhs = branch.read(code.rightOperand);
		int result;

		switch (code.kind) {
		case ADD:
			result = Sum(automaton, automaton.add(new Automaton.Real(0)),
					automaton.add(new Automaton.Bag(lhs, rhs)));
			break;
		case SUB:
			result = Sum(automaton, automaton.add(new Automaton.Real(0)),
					automaton.add(new Automaton.Bag(lhs, Mul(automaton,
							automaton.add(new Automaton.Real(-1)),
							automaton.add(new Automaton.Bag(rhs))))));
			break;
		case MUL:
			result = Mul(automaton, automaton.add(new Automaton.Real(1)),
					automaton.add(new Automaton.Bag(lhs, rhs)));
			break;
		case DIV:			
			result = Div(automaton, lhs, rhs);
			break;
		case RANGE:
			result = Range(automaton, lhs, rhs);
			break;
		default:
			internalFailure("unknown binary operator", filename, branch.entry());
			return;
		}

		branch.write(code.target, result);
	}

	protected void transform(Code.BinListOp code, VerificationBranch branch) {
		Automaton automaton = branch.automaton();
		int lhs = branch.read(code.leftOperand);
		int rhs = branch.read(code.rightOperand);
		int result;

		switch (code.kind) {
		case APPEND:
			result = Append(automaton, lhs, rhs);
			break;
		case LEFT_APPEND:
			result = Append(automaton, lhs, List(automaton, rhs));
			break;
		case RIGHT_APPEND:
			result = Append(automaton, List(automaton, lhs), rhs);
			break;		
		default:
			internalFailure("unknown binary operator", filename, branch.entry());
			return;

		}

		branch.write(code.target, result);

	}

	protected void transform(Code.BinSetOp code, VerificationBranch branch) {
		Automaton automaton = branch.automaton();
		int lhs = branch.read(code.leftOperand);
		int rhs = branch.read(code.rightOperand);
		int result;

		switch (code.kind) {
		case UNION:
			result = Union(automaton, lhs, rhs);
			break;
		case LEFT_UNION:
			result = Union(automaton, lhs, Set(automaton, rhs));
			break;
		case RIGHT_UNION:
			result = Union(automaton, Set(automaton, lhs), rhs);
			break;
		case INTERSECTION:
			result = Intersect(automaton, lhs, rhs);
			break;
		case LEFT_INTERSECTION:
			result = Intersect(automaton, lhs, Set(automaton, rhs));
			break;
		case RIGHT_INTERSECTION:
			result = Intersect(automaton, Set(automaton, lhs), rhs);
			break;
		case LEFT_DIFFERENCE:
			result = Difference(automaton, lhs, Set(automaton, rhs));
			break;
		case DIFFERENCE:
			result = Difference(automaton, lhs, rhs);
			break;
		default:
			internalFailure("unknown binary operator", filename, branch.entry());
			return;

		}

		branch.write(code.target, result);
	}

	protected void transform(Code.BinStringOp code, VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.Convert code, VerificationBranch branch) {
		int result = branch.read(code.operand);
		// TODO: actually implement some or all coercions?
		branch.write(code.target, result);
	}

	protected void transform(Code.Const code, VerificationBranch branch) {
		int rhs = convert(code.constant, branch);
		branch.write(code.target, rhs);
	}

	protected void transform(Code.Debug code, VerificationBranch branch) {
		// do nout
	}

	protected void transform(Code.Dereference code, VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.FieldLoad code, VerificationBranch branch) {
		int src = branch.read(code.operand);
		int field = branch.automaton().add(new Automaton.Strung(code.field));
		int result = FieldOf(branch.automaton(), src, field);
		branch.write(code.target, result);
	}

	protected void transform(Code.If code, VerificationBranch falseBranch,
			VerificationBranch trueBranch) {		
		// First, cover true branch
		int trueTest = buildTest(code.op, code.leftOperand, code.rightOperand,
				code.type, trueBranch);
		trueBranch.assume(trueTest);

		// Second, cover false branch
		Code.Comparator cop = Code.invert(code.op);
		int falseTest;
		if (cop != null) {
			falseTest = buildTest(Code.invert(code.op), code.leftOperand,
					code.rightOperand, code.type, falseBranch);
		} else {
			falseTest = Not(
					falseBranch.automaton(),
					buildTest(code.op, code.leftOperand, code.rightOperand,
							code.type, falseBranch));
		}
		falseBranch.assume(falseTest);
	}

	protected void transform(Code.IfIs code, VerificationBranch falseBranch,
			VerificationBranch trueBranch) {
		// TODO
	}

	protected void transform(Code.IndirectInvoke code, VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.Invoke code, VerificationBranch branch)
			throws Exception {
		int[] code_operands = code.operands;
				
		if (code.target != Code.NULL_REG) {
			// Need to assume the post-condition holds.
			Block postcondition = findPostcondition(code.name, code.type,
					branch.entry());
			int[] operands = new int[code_operands.length + 1];
			for (int i = 0; i != code_operands.length; ++i) {
				operands[i + 1] = branch.read(code_operands[i]);
			}
			
			operands[0] = branch.automaton().add(new Automaton.Strung(code.name.toString()));
			int fn = Fn(branch.automaton(),operands);
			branch.write(code.target, fn);
			
			if (postcondition != null) {
				operands = Arrays.copyOf(operands, operands.length);
				operands[0] = branch.read(code.target);
				int constraint = transformExternalBlock(postcondition, 
						operands, branch);
				// assume the post condition holds
				branch.assume(constraint);
			}		
		}
	}

	protected void transform(Code.Invert code, VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.IndexOf code, VerificationBranch branch) {
		Automaton automaton = branch.automaton();
		int src = branch.read(code.leftOperand);
		int idx = branch.read(code.rightOperand);
		int result = IndexOf(automaton, src, idx);		
		int axiom = ElementOf(automaton,result,src);
		branch.assume(axiom);		
		branch.write(code.target, result);
	}

	protected void transform(Code.LengthOf code, VerificationBranch branch) {
		Automaton automaton = branch.automaton();
		int src = branch.read(code.operand);
		int result = LengthOf(automaton, src);
		branch.write(code.target, result);
		// FIXME: this is a hack which doesn't work in all cases?
		int axiom = LessThanEq(automaton,
				Num(automaton, BigRational.valueOf(0)), result);
		branch.assume(axiom);		
	}

	protected void transform(Code.Loop code, VerificationBranch branch) {
		
		if (code instanceof Code.ForAll) {
			Code.ForAll forall = (Code.ForAll) code;
			// int end = findLabel(branch.pc(),forall.target,body);
			int src = branch.read(forall.sourceOperand);
			int idx = branch.read(forall.indexOperand);
			branch.assume(ElementOf(branch.automaton(), idx, src));
		}
		
		// FIXME: assume loop invariant?
	}

	protected void transform(Code.Move code, VerificationBranch branch) {
		branch.write(code.target, branch.read(code.operand));
	}

	protected void transform(Code.NewMap code, VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.NewList code, VerificationBranch branch) {
		int[] code_operands = code.operands;
		int[] vals = new int[code_operands.length];
		for (int i = 0; i != vals.length; ++i) {
			vals[i] = branch.read(code_operands[i]);
		}
		int result = List(branch.automaton(), vals);
		branch.write(code.target, result);
	}

	protected void transform(Code.NewSet code, VerificationBranch branch) {
		int[] code_operands = code.operands;
		int[] vals = new int[code_operands.length];
		for (int i = 0; i != vals.length; ++i) {
			vals[i] = branch.read(code_operands[i]);
		}
		int result = Set(branch.automaton(), vals);
		branch.write(code.target, result);
	}

	protected void transform(Code.NewRecord code, VerificationBranch branch) {
		int[] code_operands = code.operands;
		Type.Record type = code.type;
		ArrayList<String> fields = new ArrayList<String>(type.fields().keySet());
		Collections.sort(fields);
		int[] vals = new int[fields.size()];
		for (int i = 0; i != fields.size(); ++i) {
			int k = branch.automaton().add(new Automaton.Strung(fields.get(i)));
			int v = branch.read(code_operands[i]);
			vals[i] = branch.automaton().add(new Automaton.List(k, v));
		}

		int result = Record(branch.automaton(), vals);
		branch.write(code.target, result);
	}

	protected void transform(Code.NewObject code, VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.NewTuple code, VerificationBranch branch) {
		int[] code_operands = code.operands;
		int[] vals = new int[code_operands.length];
		for (int i = 0; i != vals.length; ++i) {
			vals[i] = branch.read(code_operands[i]);
		}
		int result = Tuple(branch.automaton(), vals);
		branch.write(code.target, result);
	}

	protected void transform(Code.Nop code, VerificationBranch branch) {
		// do nout
	}

	protected void transform(Code.Return code, VerificationBranch branch) {
		// nothing to do
	}

	protected void transform(Code.SubString code, VerificationBranch branch) {
		Automaton automaton = branch.automaton();
		int src = branch.read(code.operands[0]);
		int start = branch.read(code.operands[1]);
		int end = branch.read(code.operands[2]);
		int result = SubList(automaton, src, start, end);
		branch.write(code.target, result);
	}

	protected void transform(Code.SubList code, VerificationBranch branch) {
		Automaton automaton = branch.automaton();
		int src = branch.read(code.operands[0]);
		int start = branch.read(code.operands[1]);
		int end = branch.read(code.operands[2]);
		int result = SubList(automaton, src, start, end);
		branch.write(code.target, result);
	}

	protected void transform(Code.Throw code, VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.TupleLoad code, VerificationBranch branch) {
		Automaton automaton = branch.automaton();
		int src = branch.read(code.operand);
		int idx = automaton.add(new Automaton.Int(code.index));
		int result = TupleLoad(automaton, src, idx);
		branch.write(code.target, result);
	}

	protected void transform(Code.TryCatch code, VerificationBranch branch) {
		// FIXME: do something here?
	}
	
	protected void transform(Code.UnArithOp code, VerificationBranch branch) {
		Automaton automaton = branch.automaton();
		if (code.kind == Code.UnArithKind.NEG) {
			int result = Mul(automaton, automaton.add(new Automaton.Real(-1)),
					automaton.add(new Automaton.Bag(branch.read(code.operand))));
			branch.write(code.target, result);
		} else {
			// TODO
		}
	}

	protected void transform(Code.Update code, VerificationBranch branch) {
		int result = branch.read(code.operand);
		int source = branch.read(code.target);
		branch.write(code.target,
				updateHelper(code.iterator(), source, result, branch));
	}

	protected int updateHelper(Iterator<Code.LVal> iter, int source,
			int result, VerificationBranch branch) {
		if (!iter.hasNext()) {
			return result;
		} else {
			Code.LVal lv = iter.next();
			if (lv instanceof Code.RecordLVal) {
				Code.RecordLVal rlv = (Code.RecordLVal) lv;
				int field = branch.automaton().add(
						new Automaton.Strung(rlv.field));
				result = updateHelper(iter,
						FieldOf(branch.automaton(), source, field), result,
						branch);
				return FieldUpdate(branch.automaton(), source, field, result);
			} else if (lv instanceof Code.ListLVal) {
				Code.ListLVal rlv = (Code.ListLVal) lv;
				int index = branch.read(rlv.indexOperand);
				result = updateHelper(iter,
						IndexOf(branch.automaton(), source, index), result,
						branch);
				return ListUpdate(branch.automaton(), source, index, result);
			} else if (lv instanceof Code.MapLVal) {
				return source; // TODO
			} else if (lv instanceof Code.StringLVal) {
				return source; // TODO
			} else {
				return source; // TODO
			}
		}
	}

	protected Block findPrecondition(NameID name, Type.FunctionOrMethod fun,
			SyntacticElement elem) throws Exception {
		Path.Entry<WyilFile> e = builder.namespace().get(name.module(),
				WyilFile.ContentType);
		if (e == null) {
			syntaxError(
					errorMessage(ErrorMessages.RESOLUTION_ERROR, name.module()
							.toString()), filename, elem);
		}
		WyilFile m = e.read();
		WyilFile.MethodDeclaration method = m.method(name.name(), fun);

		for (WyilFile.Case c : method.cases()) {
			// FIXME: this is a hack for now
			return c.precondition();
		}
		return null;
	}

	protected Block findPostcondition(NameID name, Type.FunctionOrMethod fun,
			SyntacticElement elem) throws Exception {
		Path.Entry<WyilFile> e = builder.namespace().get(name.module(),
				WyilFile.ContentType);
		if (e == null) {
			syntaxError(
					errorMessage(ErrorMessages.RESOLUTION_ERROR, name.module()
							.toString()), filename, elem);
		}
		WyilFile m = e.read();
		WyilFile.MethodDeclaration method = m.method(name.name(), fun);

		for (WyilFile.Case c : method.cases()) {
			// FIXME: this is a hack for now
			return c.postcondition();
		}
		return null;
	}

	/**
	 * Generate a constraint representing an external block (e.g. a
	 * pre/post-condition or invariant).
	 * 
	 * @param externalBlock
	 *            --- the external block of code being translated.
	 * @param prefix
	 *            --- a prefix to use to ensure that local variables to the
	 *            external block will not clash with variables in the branch.
	 * @param operands
	 *            --- operand register in containing branch which should map to
	 *            the inputs of the block being translated.
	 * @param branch
	 *            --- branch into which the resulting constraint is to be
	 *            placed.
	 * @return
	 */
	protected int transformExternalBlock(Block externalBlock, 
			int[] operands, VerificationBranch branch) {
		Automaton automaton = branch.automaton();
		
		// first, generate a constraint representing the post-condition.
		VerificationBranch master = new VerificationBranch(automaton,
				externalBlock);
		
		// second, set initial environment
		for(int i=0;i!=operands.length;++i) {
			master.write(i, operands[i]);
		}
		
		return master.transform(new VerificationTransformer(builder,
				method, filename, true, debug));
	}
	
	/**
	 * Convert between a WYIL value and a WYONE value. Basically, this is really
	 * stupid and it would be good for them to be the same.
	 * 
	 * @param value
	 * @return
	 */
	private int convert(wyil.lang.Constant value, VerificationBranch branch) {
		Automaton automaton = branch.automaton();

		if (value instanceof wyil.lang.Constant.Bool) {
			wyil.lang.Constant.Bool b = (wyil.lang.Constant.Bool) value;
			return b.value ? automaton.add(True) : automaton.add(False);
		} else if (value instanceof wyil.lang.Constant.Byte) {
			wyil.lang.Constant.Byte v = (wyil.lang.Constant.Byte) value;
			return Num(branch.automaton(), BigRational.valueOf(v.value));
		} else if (value instanceof wyil.lang.Constant.Char) {
			wyil.lang.Constant.Char v = (wyil.lang.Constant.Char) value;
			// Simple, but mostly good translation
			return Num(branch.automaton(), v.value);
		} else if (value instanceof wyil.lang.Constant.Map) {
			return automaton.add(False); // TODO
		} else if (value instanceof wyil.lang.Constant.Integer) {
			wyil.lang.Constant.Integer v = (wyil.lang.Constant.Integer) value;
			return Num(branch.automaton(), BigRational.valueOf(v.value));
		} else if (value instanceof wyil.lang.Constant.Rational) {
			wyil.lang.Constant.Rational v = (wyil.lang.Constant.Rational) value;
			wyil.util.BigRational br = v.value;
			return Num(branch.automaton(),
					new BigRational(br.numerator(), br.denominator()));
		} else if (value instanceof wyil.lang.Constant.Null) {
			return automaton.add(Null);
		} else if (value instanceof wyil.lang.Constant.List) {
			Constant.List vl = (Constant.List) value;
			int[] vals = new int[vl.values.size()];
			for (int i = 0; i != vals.length; ++i) {
				vals[i] = convert(vl.values.get(i), branch);
			}
			return List(branch.automaton(), vals);
		} else if (value instanceof wyil.lang.Constant.Set) {
			Constant.Set vs = (Constant.Set) value;
			int[] vals = new int[vs.values.size()];
			int i = 0;
			for (Constant c : vs.values) {
				vals[i++] = convert(c, branch);
			}
			return Set(branch.automaton(), vals);
		} else if (value instanceof wyil.lang.Constant.Record) {
			Constant.Record vt = (Constant.Record) value;
			int[] vals = new int[vt.values.size()];
			int i = 0;
			for (Map.Entry<String, Constant> e : vt.values.entrySet()) {
				int k = branch.automaton()
						.add(new Automaton.Strung(e.getKey()));
				int v = convert(e.getValue(), branch);
				vals[i++] = branch.automaton().add(new Automaton.List(k, v));
			}
			return Record(branch.automaton(), vals);
		} else if (value instanceof wyil.lang.Constant.Strung) {
			Constant.Strung vs = (Constant.Strung) value;
			int[] vals = new int[vs.value.length()];
			for (int i = 0; i != vals.length; ++i) {
				vals[i] = Num(branch.automaton(), vs.value.charAt(i));
			}
			return List(branch.automaton(), vals);
		} else if (value instanceof wyil.lang.Constant.Tuple) {
			Constant.Tuple vt = (Constant.Tuple) value;
			int[] vals = new int[vt.values.size()];
			for (int i = 0; i != vals.length; ++i) {
				vals[i] = convert(vt.values.get(i), branch);
			}
			return Tuple(branch.automaton(), vals);
		} else {
			internalFailure("unknown value encountered (" + value + ")",
					filename, branch.entry());
			return -1;
		}
	}

	/**
	 * Generate a formula representing a condition from an Code.IfCode or
	 * Code.Assert bytecodes.
	 * 
	 * @param op
	 * @param stack
	 * @param elem
	 * @return
	 */
	private int buildTest(Code.Comparator op, int leftOperand,
			int rightOperand, Type type, VerificationBranch branch) {
		Automaton automaton = branch.automaton();
		boolean isInt = type == Type.T_INT;		
		int lhs = branch.read(leftOperand);
		int rhs = branch.read(rightOperand);

		switch (op) {
		case EQ:
			return Equals(branch.automaton(), lhs, rhs);
		case NEQ:
			return Not(automaton, Equals(automaton, lhs, rhs));
		case GTEQ:
			return LessThanEq(automaton, rhs, lhs);
		case GT:
			if(isInt) {
				rhs = addOne(automaton,rhs);
				return LessThanEq(automaton, rhs, lhs);				
			} else {
				return LessThan(automaton, rhs, lhs);
			}
		case LTEQ:
			// TODO: investigate whether better to represent LessThanEq
			// explcitly in constraint solver
			return LessThanEq(automaton, lhs, rhs);
		case LT:
			if(isInt) {
				lhs = addOne(automaton,lhs);
				return LessThanEq(automaton, lhs, rhs);				
			} else {
				return LessThan(automaton, lhs, rhs);
			}
		case SUBSET:
			return SubSet(automaton, lhs, rhs);
		case SUBSETEQ:
			// TODO: investigate whether better to represent SubSetEq explcitly
			// in constraint solver
			return Or(automaton, Equals(automaton, lhs, rhs),
					SubSet(automaton, lhs, rhs));
		case ELEMOF:
			return ElementOf(automaton, lhs, rhs);
		default:
			internalFailure("unknown comparator (" + op + ")", filename,
					branch.entry());
			return -1;
		}
	}
	
	private int addOne(Automaton automaton, int operand) {
		operand = automaton.add(new Automaton.Bag(operand));
		return Sum(automaton, automaton.add(new Automaton.Real(1)),operand);
	}
}
