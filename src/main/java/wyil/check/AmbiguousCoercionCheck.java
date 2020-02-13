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
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyil.check;

import java.util.ArrayList;
import java.util.Arrays;

import wybs.lang.Build;
import wybs.lang.CompilationUnit;
import wybs.lang.SyntacticItem;
import wybs.lang.SyntacticException;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyc.task.CompileTask;
import wyc.util.ErrorMessages;
import wyil.check.FlowTypeUtils.Environment;
import wyil.check.FunctionalCheck.Context;
import wyil.lang.Compiler;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Expr;
import wyil.lang.WyilFile.Type;
import wyil.util.*;

/**
 * <p>
 * Responsible for checking that implicit coercions are coherent following
 * RFC#19. The challenge is that, whilst union types in Whiley are powerful,
 * they also open up real challenges for efficient compilation. Specifically,
 * for determining appropriate runtime type tags. The following illustrates:
 * </p>
 *
 * <pre>
 * type msg is {int kind, int payload}|{int kind, int[] payload}
 *
 * function msg(int kind, int payload) -> (msg r):
 *	 return {kind: kind, payload: payload}
 * </pre>
 * <p>
 * At the point of the return, an implicit coercion is inserted to transform a
 * value of type <code>{int kind, int payload}</code> to a value of type
 * <code>msg</code>. The latter requires a single tag bit to distinguish the two
 * cases. In this case, the code generator can automatically determine that the
 * coercion targets the first case from the available type information.
 * </p>
 * <p>
 * Whilst the above was relatively straightforward, this is not always the case.
 * Consider this variation on the above:
 * </p>
 *
 * <pre>
 * type msg is {int kind, int payload}|{int kind, int|null payload}
 *
 * function msg(int kind, int payload) -> (msg r):
 *	 return {kind: kind, payload: payload}
 * </pre>
 * <p>
 * The intuition here might be that the first case is preferred since it
 * occupies less space, but the second is provided for general usage. In the
 * above, it is unclear what the appropriate tag should be. This is because
 * either case is a valid supertype of <code>{int kind, int payload}</code>.
 * However, we should note that <code>{int kind, int payload}</code> is more
 * precise than (i.e. is a subtype of)
 * <code>{int kind, int|null payload}</code>. The key to resolving this case is
 * that the most precise type is always preferred, provided it is unique.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class AmbiguousCoercionCheck extends AbstractTypedVisitor implements Compiler.Check {
	private boolean status = true;

	public AmbiguousCoercionCheck(Build.Meter meter) {
		// FIXME: figure out which one to use
		super(meter.fork(AmbiguousCoercionCheck.class.getSimpleName()));
	}

	@Override
	public boolean check(WyilFile file) {
		// Only proceed if no errors in earlier stages
		visitModule(file);
		meter.done();
		//
		return status;
	}

	@Override
	public void visitExternalUnit(Decl.Unit unit) {
		// NOTE: we override this to prevent unnecessarily traversing units
	}

	@Override
	public void visitExpression(Expr expr, Type target, Environment environment) {
		if(checkCoercion(expr, target, environment)) {
			// Continue recursively exploring this expression
			super.visitExpression(expr, target, environment);
		}
	}

	private boolean checkCoercion(Expr expr, Type target, Environment environment) {
		BinaryRelation.HashSet<Type> assumptions = new BinaryRelation.HashSet<>();
		Type source = expr.getType();
		if (!checkCoercion(target, source, environment, assumptions, expr)) {
			syntaxError(expr,WyilFile.AMBIGUOUS_COERCION,source,target);
			return false;
		} else {
			return true;
		}
	}

	private boolean checkCoercion(Type target, Type source, Environment environment, BinaryRelation<Type> assumptions,
			SyntacticItem item) {
		// Check we have already encountered this one or not.
		if (target.equals(source)) {
			// Common case: target and source types identical
			return true;
		} else if(assumptions.get(target, source)) {
			// Yes, this is already an assumption. Therefore, terminate early to enforce the
			// coinduction rule and prevent an infinite loop.
			return true;
		} else {
			// No, this is not an assumption. Therefore, register is an assumption.
			assumptions.set(target, source, true);
		}
		// Display on the target type in question
		boolean r;
		if (target instanceof Type.Atom) {
			// Have reached an atom, and we need to decompose this more.
			r = checkCoercion((Type.Atom) target, source, environment, assumptions, item);
		} else if (target instanceof Type.Nominal) {
			// Have reached a nominal so we just expand this as is.
			r = checkCoercion((Type.Nominal) target, source, environment, assumptions, item);
		} else {
			// Have reached a decision point. Therefore, need to try and make the decision.
			r = checkCoercion((Type.Union) target, source, environment, assumptions, item);
		}
		// Unset the assumption to avoid interference.
		assumptions.set(target, source, false);
		// Done
		return r;
	}

	private boolean checkCoercion(Type.Atom target, Type source, Environment environment,
			BinaryRelation<Type> assumptions, SyntacticItem item) {
		if (source instanceof Type.Void || target instanceof Type.Primitive || target instanceof Type.Universal) {
			return true;
		} else if(source instanceof Type.Nominal) {
			Type.Nominal s = (Type.Nominal) source;
			return checkCoercion(target,s.getConcreteType(),environment, assumptions, item);
		} else if(source instanceof Type.Union) {
			Type.Union s = (Type.Union) source;
			for (int i = 0; i != s.size(); ++i) {
				if (!checkCoercion(target, s.get(i), environment, assumptions, item)) {
					return false;
				}
			}
			return true;
		} else if (target instanceof Type.Array && source instanceof Type.Array) {
			return checkCoercion((Type.Array) target, (Type.Array) source, environment, assumptions, item);
		} else if (target instanceof Type.Reference && source instanceof Type.Reference) {
			return checkCoercion((Type.Reference) target, (Type.Reference) source, environment, assumptions, item);
		} else if (target instanceof Type.Record && source instanceof Type.Record) {
			return checkCoercion((Type.Record) target, (Type.Record) source, environment, assumptions, item);
		} else if (target instanceof Type.Tuple && source instanceof Type.Tuple) {
			return checkCoercion((Type.Tuple) target, (Type.Tuple) source, environment, assumptions, item);
		} else if (target instanceof Type.Callable && source instanceof Type.Callable) {
			return checkCoercion((Type.Callable) target, (Type.Callable) source, environment, item);
		} else {
			return internalFailure("problematic coercion (" + source + " to " + target + ")", item);
		}
	}

	private boolean checkCoercion(Type.Array target, Type.Array source, Environment environment,
			BinaryRelation<Type> assumptions, SyntacticItem item) {
		return checkCoercion(target.getElement(), source.getElement(), environment, assumptions, item);
	}

	private boolean checkCoercion(Type.Reference target, Type.Reference source, Environment environment,
			BinaryRelation<Type> assumptions, SyntacticItem item) {
		return checkCoercion(target.getElement(), source.getElement(), environment, assumptions, item);
	}

	private boolean checkCoercion(Type.Record target, Type.Record source, Environment environment,
			BinaryRelation<Type> assumptions, SyntacticItem item) {
		Tuple<Type.Field> fields = target.getFields();
		for (int i = 0; i != fields.size(); ++i) {
			Type.Field field = fields.get(i);
			Type type = source.getField(field.getName());
			if (!checkCoercion(field.getType(), type, environment, assumptions, item)) {
				return false;
			}
		}
		return true;
	}

	private boolean checkCoercion(Type.Tuple target, Type.Tuple source, Environment environment,
			BinaryRelation<Type> assumptions, SyntacticItem item) {
		for (int i = 0; i != target.size(); ++i) {
			Type element = target.get(i);
			Type type = source.get(i);
			if (!checkCoercion(element, type, environment, assumptions, item)) {
				return false;
			}
		}
		return true;
	}

	private boolean checkCoercion(Type.Callable target, Type.Callable source, Environment environment,
			SyntacticItem item) {
		// FIXME: this is considered safe at this point only because the subtype
		// operator currently does not allow any kind of subtyping between parameters.
		return true;
	}

	private boolean checkCoercion(Type.Nominal target, Type source, Environment environment,
			BinaryRelation<Type> assumptions, SyntacticItem item) {
		if(source instanceof Type.Nominal) {
			Type.Nominal sn = (Type.Nominal) source;
			Decl.Type t = target.getLink().getTarget();
			Decl.Type s = sn.getLink().getTarget();
			// Handle simple case
			if(t == s && target.getParameters().equals(sn.getParameters())) {
				return true;
			}
		}
		return checkCoercion(target.getConcreteType(), source, environment, assumptions, item);
	}

	private boolean checkCoercion(Type.Union target, Type source, Environment environment,
			BinaryRelation<Type> assumptions, SyntacticItem item) {
		Type.Union ut = target;
		Type candidate = selectCoercionCandidate(ut.getAll(), source, environment);
		if (candidate != null) {
			// Indicates decision made easily enough. Continue traversal down the type.
			return checkCoercion(candidate, source, environment, assumptions, item);
		}
		switch(source.getOpcode()) {
		case WyilFile.TYPE_void: {
			// NOTE: void indicates an unusual case where an actual value is guaranteed
			// impossible. Thus, any coercion is considered valid since, in practice,
			// determination of the type tag never occurs.
			return true;
		}
		case WyilFile.TYPE_nominal: {
			// Proceed by expanding source
			Type.Nominal s = (Type.Nominal) source;
			return checkCoercion(target,s.getConcreteType(),environment, assumptions, item);
		}
		case WyilFile.TYPE_union: {
			// Proceed by expanding source
			Type.Union su = (Type.Union) source;
			for (int i = 0; i != su.size(); ++i) {
				if (!checkCoercion(target, su.get(i), environment, assumptions, item)) {
					return false;
				}
			}
			return true;
		}
		case WyilFile.TYPE_recursive: {
			// Proceed by expanding source
			Type.Recursive su = (Type.Recursive) source;
			return checkCoercion(target, su.getHead(), environment, assumptions, item);
		}
		default: {
			// Cannot proceed
			return false;
		}
		}
	}

	private Type selectCoercionCandidate(Type[] candidates, Type type, Environment environment) {
		// FIXME: this is temporary
		return super.select(Arrays.asList(candidates), type, environment);
	}

	private void syntaxError(SyntacticItem e, int code, SyntacticItem... context) {
		status = false;
		ErrorMessages.syntaxError(e, code, context);
	}

	private <T> T internalFailure(String msg, SyntacticItem e) {
		CompilationUnit cu = (CompilationUnit) e.getHeap();
		throw new SyntacticException(msg, cu.getEntry(), e);
	}

	protected interface Assumptions {

	}
}
