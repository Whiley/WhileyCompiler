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
import static wyil.util.ErrorMessages.errorMessage;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.*;

import wybs.lang.*;
import wybs.util.Pair;
import wybs.util.Trie;
import wyil.lang.*;
import wyil.util.ErrorMessages;

import wycs.io.WycsFilePrinter;
import wycs.lang.*;
import wycs.transforms.ConstraintInline;
import wycs.transforms.VerificationCheck;
import wycs.util.Exprs;

/**
 * Responsible for converting a given Wyil bytecode into an appropriate
 * constraint which encodes its semantics.
 * 
 * @author David J. Pearce
 * 
 */
public class VerificationTransformer {
	private final Builder builder;
	// private final WyilFile.Case method;
	private final WycsFile wycsFile;
	private final String filename;
	private final boolean assume;

	public VerificationTransformer(Builder builder, WycsFile wycsFile,
			String filename, boolean assume) {
		this.builder = builder;
		this.filename = filename;
		this.assume = assume;
		this.wycsFile = wycsFile;
	}

	public String filename() {
		return filename;
	}

	public void end(VerificationBranch.LoopScope scope,
			VerificationBranch branch) {
		// not sure what really needs to be done here, in fact.
	}

	public void exit(VerificationBranch.LoopScope scope,
			VerificationBranch branch) {
		branch.addAll(scope.constraints);
	}

	public void end(VerificationBranch.ForScope scope, VerificationBranch branch) {
		// we need to build up a quantified formula here.

		ArrayList<Expr> constraints = new ArrayList<Expr>();
		constraints.addAll(scope.constraints);

		Expr root = Expr.Nary(Expr.Nary.Op.AND, constraints, branch.entry()
				.attributes());
		
		SyntacticType type = convert(scope.loop.type.element(),branch.entry());
		TypePattern tp = new TypePattern.Leaf(type, scope.index.name);

		if (scope.loop.type instanceof Type.EffectiveList) {
			// FIXME: hack to work around limitations of whiley for
			// loops.
			TypePattern.Leaf idx = new TypePattern.Leaf(
					new SyntacticType.Primitive(SemanticType.Int), null);
			tp = new TypePattern.Tuple(new TypePattern[] { idx, tp }, null);
		}

		Pair<TypePattern, Expr>[] vars = new Pair[] { new Pair<TypePattern, Expr>(
				tp, scope.source) };
		
		
		branch.add(Expr.ForAll(vars, root, branch.entry().attributes()));
	}

	public void exit(VerificationBranch.ForScope scope,
			VerificationBranch branch) {
		ArrayList<Expr> constraints = new ArrayList<Expr>();
		constraints.addAll(scope.constraints);

		Expr root = Expr.Nary(Expr.Nary.Op.AND, constraints, branch.entry()
				.attributes());
		SyntacticType type = convert(scope.loop.type.element(),branch.entry());
		TypePattern tp = new TypePattern.Leaf(type, scope.index.name);

		if (scope.loop.type instanceof Type.EffectiveList) {
			// FIXME: hack to work around limitations of whiley for
			// loops.
			TypePattern.Leaf idx = new TypePattern.Leaf(
					new SyntacticType.Primitive(SemanticType.Int), null);
			tp = new TypePattern.Tuple(new TypePattern[] { idx, tp }, null);
		}

		Pair<TypePattern, Expr>[] vars = new Pair[] { new Pair<TypePattern, Expr>(
				tp, scope.source) };
		
		branch.add(Expr.Exists(vars, root, branch.entry().attributes()));
	}

	public void exit(VerificationBranch.TryScope scope,
			VerificationBranch branch) {

	}

	protected void transform(Code.Assert code, VerificationBranch branch) {
		Expr test = buildTest(code.op, code.leftOperand, code.rightOperand,
				code.type, branch);

		if (!assume) {
			// We need the entry branch to determine the parameter types.
			Expr assumptions = branch.constraints();
			Expr implication = Expr.Binary(Expr.Binary.Op.IMPLIES, assumptions,
					test);
			Expr assertion = buildAssertion(0, implication, branch);
			wycsFile.add(wycsFile.new Assert(code.msg, assertion, branch
					.entry().attributes()));
		}

		branch.add(test);
	}

	/**
	 * Recursively descend the scope stack building up appropriate
	 * parameterisation of the core assertion as we go.
	 * 
	 * @param index
	 *            --- current depth into the scope stack.
	 * @param implication
	 *            --- the core assertion being parameterised.
	 * @param branch
	 *            --- current branch containing scope stack.
	 * @return
	 */
	protected Expr buildAssertion(int index, Expr implication,
			VerificationBranch branch) {
		if (index < branch.nScopes()) {
			Expr contents = buildAssertion(index + 1, implication, branch);

			VerificationBranch.Scope scope = branch.scope(index);
			if (scope instanceof VerificationBranch.EntryScope) {
				VerificationBranch.EntryScope es = (VerificationBranch.EntryScope) scope;
				Pair<TypePattern, Expr>[] vars = convertParameters(es.declaration);
				return Expr.ForAll(vars, contents);
			} else if (scope instanceof VerificationBranch.ForScope) {
				VerificationBranch.ForScope ls = (VerificationBranch.ForScope) scope;
				SyntacticType type = convert(ls.loop.type.element(),branch.entry());
				TypePattern tp = new TypePattern.Leaf(type,ls.index.name);
				
				if (ls.loop.type instanceof Type.EffectiveList) {
					// FIXME: hack to work around limitations of whiley for
					// loops.
					TypePattern.Leaf idx = new TypePattern.Leaf(
							new SyntacticType.Primitive(SemanticType.Int), null);
					tp = new TypePattern.Tuple(new TypePattern[] { idx, tp },
							null);
				}

				Pair<TypePattern,Expr>[] vars = new Pair[]{
						new Pair<TypePattern,Expr>(tp,ls.source)
				};
				
				return Expr.ForAll(vars, contents);
			} else {
				return contents;
			}
		} else {
			return implication;
		}
	}

	protected void transform(Code.Assume code, VerificationBranch branch) {
		// At this point, what we do is invert the condition being asserted and
		// check that it is unsatisfiable.
		Expr test = buildTest(code.op, code.leftOperand, code.rightOperand,
				code.type, branch);
		branch.add(test);
	}

	protected void transform(Code.Assign code, VerificationBranch branch) {
		branch.write(code.target, branch.read(code.operand));
	}

	protected void transform(Code.BinArithOp code, VerificationBranch branch) {
		Expr lhs = branch.read(code.leftOperand);
		Expr rhs = branch.read(code.rightOperand);
		Expr.Binary.Op op;

		switch (code.kind) {
		case ADD:
			op = Expr.Binary.Op.ADD;
			break;
		case SUB:
			op = Expr.Binary.Op.SUB;
			break;
		case MUL:
			op = Expr.Binary.Op.MUL;
			break;
		case DIV:
			op = Expr.Binary.Op.DIV;
			break;
		case RANGE:
			branch.write(code.target,
					Exprs.ListRange(lhs, rhs, branch.entry().attributes()));
			return;
		default:
			internalFailure("unknown binary operator", filename, branch.entry());
			return;
		}

		branch.write(code.target,
				Expr.Binary(op, lhs, rhs, branch.entry().attributes()));
	}

	protected void transform(Code.BinListOp code, VerificationBranch branch) {
		Expr lhs = branch.read(code.leftOperand);
		Expr rhs = branch.read(code.rightOperand);

		switch (code.kind) {
		case APPEND:
			// do nothing
			break;
		case LEFT_APPEND:
			rhs = Exprs.List(new Expr[] { rhs }, branch.entry().attributes());
			break;
		case RIGHT_APPEND:
			lhs = Exprs.List(new Expr[] { lhs }, branch.entry().attributes());
			break;
		default:
			internalFailure("unknown binary operator", filename, branch.entry());
			return;
		}

		branch.write(code.target,
				Exprs.ListAppend(lhs, rhs, branch.entry().attributes()));
	}

	protected void transform(Code.BinSetOp code, VerificationBranch branch) {
		Collection<Attribute> attributes = branch.entry().attributes();
		Expr lhs = branch.read(code.leftOperand);
		Expr rhs = branch.read(code.rightOperand);
		Expr val;

		switch (code.kind) {
		case UNION:
			val = Exprs.SetUnion(lhs, rhs, attributes);
			break;
		case LEFT_UNION:
			rhs = Expr.Nary(Expr.Nary.Op.SET, new Expr[] { rhs }, branch
					.entry().attributes());
			val = Exprs.SetUnion(lhs, rhs, attributes);
			break;
		case RIGHT_UNION:
			lhs = Expr.Nary(Expr.Nary.Op.SET, new Expr[] { lhs }, branch
					.entry().attributes());
			val = Exprs.SetUnion(lhs, rhs, attributes);
			break;
		case INTERSECTION:
			val = Exprs.SetIntersection(lhs, rhs, attributes);
			break;
		case LEFT_INTERSECTION:
			rhs = Expr.Nary(Expr.Nary.Op.SET, new Expr[] { rhs }, branch
					.entry().attributes());
			val = Exprs.SetIntersection(lhs, rhs, attributes);
			break;
		case RIGHT_INTERSECTION:
			lhs = Expr.Nary(Expr.Nary.Op.SET, new Expr[] { lhs }, branch
					.entry().attributes());
			val = Exprs.SetIntersection(lhs, rhs, attributes);
			break;
		case LEFT_DIFFERENCE:
			rhs = Expr.Nary(Expr.Nary.Op.SET, new Expr[] { rhs }, branch
					.entry().attributes());
			val = Exprs.SetDifference(lhs, rhs, attributes);
			break;
		case DIFFERENCE:
			val = Exprs.SetDifference(lhs, rhs, attributes);
			break;
		default:
			internalFailure("unknown binary operator", filename, branch.entry());
			return;
		}

		branch.write(code.target, val);
	}

	protected void transform(Code.BinStringOp code, VerificationBranch branch) {
		Collection<Attribute> attributes = branch.entry().attributes();
		Expr lhs = branch.read(code.leftOperand);
		Expr rhs = branch.read(code.rightOperand);

		switch (code.kind) {
		case APPEND:
			// do nothing
			break;
		case LEFT_APPEND:
			rhs = Exprs.List(new Expr[] { rhs }, attributes);
			break;
		case RIGHT_APPEND:
			lhs = Exprs.List(new Expr[] { lhs }, attributes);
			break;
		default:
			internalFailure("unknown binary operator", filename, branch.entry());
			return;
		}

		branch.write(code.target, Exprs.ListAppend(lhs, rhs, attributes));
	}

	protected void transform(Code.Convert code, VerificationBranch branch) {
		Expr result = branch.read(code.operand);
		// TODO: actually implement some or all coercions?
		branch.write(code.target, result);
	}

	protected void transform(Code.Const code, VerificationBranch branch) {
		Value val = convert(code.constant, branch.entry());
		branch.write(code.target,
				Expr.Constant(val, branch.entry().attributes()));
	}

	protected void transform(Code.Debug code, VerificationBranch branch) {
		// do nout
	}

	protected void transform(Code.Dereference code, VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.FieldLoad code, VerificationBranch branch) {
		Collection<Attribute> attributes = branch.entry().attributes();
		Expr src = branch.read(code.operand);
		branch.write(code.target, Exprs.FieldOf(src, code.field, attributes));
	}

	protected void transform(Code.If code, VerificationBranch falseBranch,
			VerificationBranch trueBranch) {
		// First, cover true branch
		Expr.Binary trueTest = buildTest(code.op, code.leftOperand,
				code.rightOperand, code.type, trueBranch);
		trueBranch.add(trueTest);
		falseBranch.add(invert(trueTest));
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
		Collection<Attribute> attributes = branch.entry().attributes();
		int[] code_operands = code.operands;

		if (code.target != Code.NULL_REG) {
			// Need to assume the post-condition holds.
			Block postcondition = findPostcondition(code.name, code.type,
					branch.entry());
			Expr[] operands = new Expr[code_operands.length];
			for (int i = 0; i != code_operands.length; ++i) {
				operands[i] = branch.read(code_operands[i]);
			}
			Expr argument = Expr.Nary(Expr.Nary.Op.TUPLE, operands, attributes);
			branch.write(code.target, Expr.FunCall(code.name.toString(),
					new SyntacticType[0], argument, attributes));

			if (postcondition != null) {
				// operands = Arrays.copyOf(operands, operands.length);
				Expr[] arguments = new Expr[operands.length + 1];
				System.arraycopy(operands, 0, arguments, 1, operands.length);
				arguments[0] = branch.read(code.target);
				Expr constraint = transformExternalBlock(postcondition,
						arguments, branch);
				// assume the post condition holds
				branch.add(constraint);
			}
		}
	}

	protected void transform(Code.Invert code, VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.IndexOf code, VerificationBranch branch) {
		Expr src = branch.read(code.leftOperand);
		Expr idx = branch.read(code.rightOperand);
		branch.write(code.target,
				Exprs.IndexOf(src, idx, branch.entry().attributes()));
	}

	protected void transform(Code.LengthOf code, VerificationBranch branch) {
		Expr src = branch.read(code.operand);
		branch.write(code.target, Expr.Unary(Expr.Unary.Op.LENGTHOF, src,
				branch.entry().attributes()));
	}

	protected void transform(Code.Loop code, VerificationBranch branch) {
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
		Expr[] vals = new Expr[code_operands.length];
		for (int i = 0; i != vals.length; ++i) {
			vals[i] = branch.read(code_operands[i]);
		}
		branch.write(code.target, Exprs.List(vals, branch.entry().attributes()));
	}

	protected void transform(Code.NewSet code, VerificationBranch branch) {
		int[] code_operands = code.operands;
		Expr[] vals = new Expr[code_operands.length];
		for (int i = 0; i != vals.length; ++i) {
			vals[i] = branch.read(code_operands[i]);
		}
		branch.write(code.target,
				Expr.Nary(Expr.Nary.Op.SET, vals, branch.entry().attributes()));
	}

	protected void transform(Code.NewRecord code, VerificationBranch branch) {
		int[] code_operands = code.operands;
		Type.Record type = code.type;
		ArrayList<String> fields = new ArrayList<String>(type.fields().keySet());
		Collections.sort(fields);
		Expr[] vals = new Expr[fields.size()];
		for (int i = 0; i != fields.size(); ++i) {
			vals[i] = branch.read(code_operands[i]);
		}

		branch.write(code.target, Exprs.Record(fields
				.toArray(new String[vals.length]), vals, branch.entry()
				.attributes()));
	}

	protected void transform(Code.NewObject code, VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.NewTuple code, VerificationBranch branch) {
		int[] code_operands = code.operands;
		Expr[] vals = new Expr[code_operands.length];
		for (int i = 0; i != vals.length; ++i) {
			vals[i] = branch.read(code_operands[i]);
		}
		branch.write(code.target, Expr.Nary(Expr.Nary.Op.TUPLE, vals, branch
				.entry().attributes()));
	}

	protected void transform(Code.Nop code, VerificationBranch branch) {
		// do nout
	}

	protected void transform(Code.Return code, VerificationBranch branch) {
		// nothing to do
	}

	protected void transform(Code.SubString code, VerificationBranch branch) {
		Expr src = branch.read(code.operands[0]);
		Expr start = branch.read(code.operands[1]);
		Expr end = branch.read(code.operands[2]);
		Expr result = Exprs.SubList(src, start, end, branch.entry()
				.attributes());
		branch.write(code.target, result);
	}

	protected void transform(Code.SubList code, VerificationBranch branch) {
		Expr src = branch.read(code.operands[0]);
		Expr start = branch.read(code.operands[1]);
		Expr end = branch.read(code.operands[2]);
		Expr result = Exprs.SubList(src, start, end, branch.entry()
				.attributes());
		branch.write(code.target, result);
	}

	protected void transform(Code.Throw code, VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.TupleLoad code, VerificationBranch branch) {
		Expr src = branch.read(code.operand);
		Expr result = Expr.TupleLoad(src, code.index, branch.entry()
				.attributes());
		branch.write(code.target, result);
	}

	protected void transform(Code.TryCatch code, VerificationBranch branch) {
		// FIXME: do something here?
	}

	protected void transform(Code.UnArithOp code, VerificationBranch branch) {
		if (code.kind == Code.UnArithKind.NEG) {
			Expr operand = branch.read(code.operand);
			branch.write(code.target, Expr.Unary(Expr.Unary.Op.NEG, operand,
					branch.entry().attributes()));
		} else {
			// TODO
		}
	}

	protected void transform(Code.Update code, VerificationBranch branch) {
		Expr result = branch.read(code.operand);
		Expr source = branch.read(code.target);
		branch.write(code.target,
				updateHelper(code.iterator(), source, result, branch));
	}

	protected Expr updateHelper(Iterator<Code.LVal> iter, Expr source,
			Expr result, VerificationBranch branch) {
		if (!iter.hasNext()) {
			return result;
		} else {
			Collection<Attribute> attributes = branch.entry().attributes();
			Code.LVal lv = iter.next();
			if (lv instanceof Code.RecordLVal) {
				Code.RecordLVal rlv = (Code.RecordLVal) lv;
				result = updateHelper(iter,
						Exprs.FieldOf(source, rlv.field, attributes), result,
						branch);
				return Exprs.FieldUpdate(source, rlv.field, result, attributes);
			} else if (lv instanceof Code.ListLVal) {
				Code.ListLVal rlv = (Code.ListLVal) lv;
				Expr index = branch.read(rlv.indexOperand);
				result = updateHelper(iter,
						Exprs.IndexOf(source, index, attributes), result,
						branch);
				return Exprs.ListUpdate(source, index, result, attributes);
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
	protected Expr transformExternalBlock(Block externalBlock, Expr[] operands,
			VerificationBranch branch) {

		// first, generate a constraint representing the post-condition.
		VerificationBranch master = new VerificationBranch(externalBlock);

		// second, set initial environment
		for (int i = 0; i != operands.length; ++i) {
			master.write(i, operands[i]);
		}

		return master.transform(new VerificationTransformer(builder, wycsFile,
				filename, true));
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
	private Expr.Binary buildTest(Code.Comparator cop, int leftOperand,
			int rightOperand, Type type, VerificationBranch branch) {
		Expr lhs = branch.read(leftOperand);
		Expr rhs = branch.read(rightOperand);
		Expr.Binary.Op op;
		switch (cop) {
		case EQ:
			op = Expr.Binary.Op.EQ;
			break;
		case NEQ:
			op = Expr.Binary.Op.NEQ;
			break;
		case GTEQ:
			op = Expr.Binary.Op.GTEQ;
			break;
		case GT:
			op = Expr.Binary.Op.GT;
			break;
		case LTEQ:
			op = Expr.Binary.Op.LTEQ;
			break;
		case LT:
			op = Expr.Binary.Op.LT;
			break;
		case SUBSET:
			op = Expr.Binary.Op.SUBSET;
			break;
		case SUBSETEQ:
			op = Expr.Binary.Op.SUBSETEQ;
			break;
		case ELEMOF:
			op = Expr.Binary.Op.IN;
			break;
		default:
			internalFailure("unknown comparator (" + cop + ")", filename,
					branch.entry());
			return null;
		}

		return Expr.Binary(op, lhs, rhs, branch.entry().attributes());
	}

	/**
	 * Generate the logically inverted expression corresponding to this
	 * comparator.
	 * 
	 * @param cop
	 * @param leftOperand
	 * @param rightOperand
	 * @param type
	 * @param branch
	 * @return
	 */
	private Expr invert(Expr.Binary test) {
		Expr.Binary.Op op;
		switch (test.op) {
		case EQ:
			op = Expr.Binary.Op.NEQ;
			break;
		case NEQ:
			op = Expr.Binary.Op.EQ;
			break;
		case GTEQ:
			op = Expr.Binary.Op.LT;
			break;
		case GT:
			op = Expr.Binary.Op.LTEQ;
			break;
		case LTEQ:
			op = Expr.Binary.Op.GT;
			break;
		case LT:
			op = Expr.Binary.Op.GTEQ;
			break;
		case SUBSET:
			op = Expr.Binary.Op.SUPSETEQ;
			break;
		case SUBSETEQ:
			op = Expr.Binary.Op.SUPSET;
			break;
		case SUPSET:
			op = Expr.Binary.Op.SUBSETEQ;
			break;
		case SUPSETEQ:
			op = Expr.Binary.Op.SUBSET;
			break;
		case IN:
			op = Expr.Binary.Op.IN;
			return Expr.Unary(
					Expr.Unary.Op.NOT,
					Expr.Binary(op, test.leftOperand, test.rightOperand,
							test.attributes()), test.attributes());
		default:
			internalFailure("unknown comparator (" + test.op + ")", filename,
					test);
			return null;
		}

		return Expr.Binary(op, test.leftOperand, test.rightOperand,
				test.attributes());
	}

	public Value convert(Constant c, SyntacticElement elem) {
		if (c instanceof Constant.Null) {
			// TODO: is this the best translation?
			return wycs.lang.Value.Integer(BigInteger.ZERO);
		} else if (c instanceof Constant.Bool) {
			Constant.Bool cb = (Constant.Bool) c;
			return wycs.lang.Value.Bool(cb.value);
		} else if (c instanceof Constant.Byte) {
			Constant.Byte cb = (Constant.Byte) c;
			return wycs.lang.Value.Integer(BigInteger.valueOf(cb.value));
		} else if (c instanceof Constant.Char) {
			Constant.Char cb = (Constant.Char) c;
			return wycs.lang.Value.Integer(BigInteger.valueOf(cb.value));
		} else if (c instanceof Constant.Integer) {
			Constant.Integer cb = (Constant.Integer) c;
			return wycs.lang.Value.Integer(cb.value);
		} else if (c instanceof Constant.Rational) {
			Constant.Rational cb = (Constant.Rational) c;
			return wycs.lang.Value.Rational(cb.value);
		} else if (c instanceof Constant.Strung) {
			Constant.Strung cb = (Constant.Strung) c;
			String str = cb.value;
			ArrayList<Value> pairs = new ArrayList<Value>();
			for (int i = 0; i != str.length(); ++i) {
				ArrayList<Value> pair = new ArrayList<Value>();
				pair.add(Value.Integer(BigInteger.valueOf(i)));
				pair.add(Value.Integer(BigInteger.valueOf(str.charAt(i))));
				pairs.add(Value.Tuple(pair));
			}
			return Value.Set(pairs);
		} else if (c instanceof Constant.List) {
			Constant.List cb = (Constant.List) c;
			List<Constant> cb_values = cb.values;
			ArrayList<Value> pairs = new ArrayList<Value>();
			for (int i = 0; i != cb_values.size(); ++i) {
				ArrayList<Value> pair = new ArrayList<Value>();
				pair.add(Value.Integer(BigInteger.valueOf(i)));
				pair.add(convert(cb_values.get(i), elem));
				pairs.add(Value.Tuple(pair));
			}
			return Value.Set(pairs);
		} else if (c instanceof Constant.Map) {
			Constant.Map cb = (Constant.Map) c;
			ArrayList<Value> pairs = new ArrayList<Value>();
			for (Map.Entry<Constant, Constant> e : cb.values.entrySet()) {
				ArrayList<Value> pair = new ArrayList<Value>();
				pair.add(convert(e.getKey(), elem));
				pair.add(convert(e.getValue(), elem));
				pairs.add(Value.Tuple(pair));
			}
			return Value.Set(pairs);
		} else if (c instanceof Constant.Set) {
			Constant.Set cb = (Constant.Set) c;
			ArrayList<Value> values = new ArrayList<Value>();
			for (Constant v : cb.values) {
				values.add(convert(v, elem));
			}
			return wycs.lang.Value.Set(values);
		} else if (c instanceof Constant.Tuple) {
			Constant.Tuple cb = (Constant.Tuple) c;
			ArrayList<Value> values = new ArrayList<Value>();
			for (Constant v : cb.values) {
				values.add(convert(v, elem));
			}
			return wycs.lang.Value.Tuple(values);
		} else {
			internalFailure("unknown constant encountered (" + c + ")",
					filename, elem);
			return null;
		}
	}

	private Pair<TypePattern, Expr>[] convertParameters(
			WyilFile.MethodDeclaration decl) {
		Type.FunctionOrMethod tfm = decl.type();
		ArrayList<Type> parameters = tfm.params();
		Pair<TypePattern, Expr>[] types = new Pair[parameters.size()];
		for (int i = 0; i != types.length; ++i) {
			SyntacticType t = convert(parameters.get(i), decl);
			TypePattern.Leaf l = new TypePattern.Leaf(t, "r" + i);
			types[i] = new Pair<TypePattern, Expr>(l, null);
		}
		return types;
	}

	private SyntacticType convert(Type t, SyntacticElement elem) {
		// FIXME: this is fundamentally broken in the case of recursive types.
		if (t instanceof Type.Any) {
			return new SyntacticType.Primitive(SemanticType.Any);
		} else if (t instanceof Type.Void) {
			return new SyntacticType.Primitive(SemanticType.Void);
		} else if (t instanceof Type.Bool) {
			return new SyntacticType.Primitive(SemanticType.Bool);
		} else if (t instanceof Type.Char) {
			return new SyntacticType.Primitive(SemanticType.Int);
		} else if (t instanceof Type.Int) {
			return new SyntacticType.Primitive(SemanticType.Int);
		} else if (t instanceof Type.Real) {
			return new SyntacticType.Primitive(SemanticType.Real);
		} else if (t instanceof Type.Set) {
			Type.Set st = (Type.Set) t;
			SyntacticType element = convert(st.element(), elem);
			return new SyntacticType.Set(element);
		} else if (t instanceof Type.List) {
			Type.List lt = (Type.List) t;
			SyntacticType element = convert(lt.element(), elem);
			// ugly.
			return new SyntacticType.Set(new SyntacticType.Tuple(
					new SyntacticType[] {
							new SyntacticType.Primitive(SemanticType.Int),
							element }));
		} else if (t instanceof Type.Tuple) {
			Type.Tuple tt = (Type.Tuple) t;
			SyntacticType[] elements = new SyntacticType[tt.size()];
			for (int i = 0; i != tt.size(); ++i) {
				elements[i] = convert(tt.element(i), elem);
			}
			return new SyntacticType.Tuple(elements);
		} else if (t instanceof Type.Record) {
			Type.Record rt = (Type.Record) t;
			// FIXME: this is *completely* broken
			return new SyntacticType.Set(new SyntacticType.Tuple(
					new SyntacticType[] {
							new SyntacticType.Primitive(SemanticType.String),
							new SyntacticType.Primitive(SemanticType.Any) }));
		} else {
			internalFailure("unknown type encountered (" + t + ")", filename,
					elem);
			return null;
		}
	}
}
