// Copyright 2017 David J. Pearce
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
package wyc.type.util;

import wyc.lang.WhileyFile;
import wyc.lang.WhileyFile.Declaration;

import static wyc.lang.WhileyFile.*;
import wybs.lang.NameResolver.ResolutionError;
import wycc.util.ArrayUtils;
import wyc.type.TypeInferer;
import wyc.type.TypeSystem;

/**
 * A simple type inference for expressions found in WhileyFiles.
 *
 * @author David J. Pearce
 *
 */
public class StdTypeInfererence implements TypeInferer {
	private final TypeSystem types;

	public StdTypeInfererence(TypeSystem types) {
		this.types = types;
	}

	@Override
	public Type getInferredType(Expr expression) throws ResolutionError {
		return inferExpression(expression);
	}

	protected Type inferExpression(Expr expr) throws ResolutionError {
		switch (expr.getOpcode()) {
		case WhileyFile.EXPR_const:
			return inferConstant((Expr.Constant) expr);
		case WhileyFile.EXPR_cast:
			return inferCast((Expr.Cast) expr);
		case WhileyFile.EXPR_invoke:
			return inferInvoke((Expr.Invoke) expr);
		case WhileyFile.EXPR_indirectinvoke:
			return inferIndirectInvoke((Expr.IndirectInvoke) expr);
		case WhileyFile.EXPR_varcopy:
			return inferVariableAccess((Expr.VariableAccess) expr);
		case WhileyFile.EXPR_staticvar:
			return inferStaticVariableAccess((Expr.StaticVariableAccess) expr);
		case WhileyFile.EXPR_not:
		case WhileyFile.EXPR_and:
		case WhileyFile.EXPR_or:
		case WhileyFile.EXPR_implies:
		case WhileyFile.EXPR_iff:
		case WhileyFile.EXPR_eq:
		case WhileyFile.EXPR_neq:
		case WhileyFile.EXPR_lt:
		case WhileyFile.EXPR_lteq:
		case WhileyFile.EXPR_gt:
		case WhileyFile.EXPR_gteq:
			return inferLogicalOperator((Expr.Operator) expr);
		case WhileyFile.EXPR_forall:
		case WhileyFile.EXPR_exists:
			return inferQuantifier((Expr.Quantifier) expr);
		case WhileyFile.EXPR_neg:
		case WhileyFile.EXPR_add:
		case WhileyFile.EXPR_sub:
		case WhileyFile.EXPR_mul:
		case WhileyFile.EXPR_div:
		case WhileyFile.EXPR_rem:
			return inferArithmeticOperator((Expr.Operator) expr);
		case WhileyFile.EXPR_bitwiseand:
		case WhileyFile.EXPR_bitwiseor:
		case WhileyFile.EXPR_bitwisexor:
		case WhileyFile.EXPR_bitwiseshl:
		case WhileyFile.EXPR_bitwiseshr:
		case WhileyFile.EXPR_bitwisenot:
			return inferBitwiseOperator((Expr.Operator) expr);
		case WhileyFile.EXPR_arrlen:
			return inferArrayLength((Expr.Operator) expr);
		case WhileyFile.EXPR_arrinit:
			return inferArrayInitialiser((Expr.Operator) expr);
		case WhileyFile.EXPR_arrgen:
			return inferArrayGenerator((Expr.Operator) expr);
		case WhileyFile.EXPR_arridx:
			return inferArrayIndex((Expr.Operator) expr);
		case WhileyFile.EXPR_arrupdt:
			return inferArrayUpdate((Expr.Operator) expr);
		case WhileyFile.EXPR_recinit:
			return inferRecordInitialiser((Expr.RecordInitialiser) expr);
		case WhileyFile.EXPR_recfield:
			return inferRecordAccess((Expr.RecordAccess) expr);
		case WhileyFile.EXPR_recupdt:
			return inferRecordUpdate((Expr.RecordUpdate) expr);
		case WhileyFile.EXPR_deref:
			return inferDereference((Expr.Dereference) expr);
		default:
			throw new IllegalArgumentException("invalid expression encountered: " + expr);
		}
	}

	protected Type inferCast(Expr.Cast expr) {
		return (Type) expr.getCastType();
	}

	protected Type inferLogicalOperator(Expr.Operator expr) throws ResolutionError {
		return Type.Bool;
	}

	protected Type inferArithmeticOperator(Expr.Operator expr) throws ResolutionError {
		return Type.Int;
	}

	protected Type inferBitwiseOperator(Expr.Operator expr) throws ResolutionError {
		return Type.Byte;
	}

	protected Type inferVariableAccess(Expr.VariableAccess expr) {
		return expr.getVariableDeclaration().getType();
	}

	protected Type inferStaticVariableAccess(Expr.StaticVariableAccess expr) throws ResolutionError {
		Declaration.StaticVariable decl = types.resolveExactly(expr.getName(), Declaration.StaticVariable.class);
		return decl.getType();
	}

	protected Type inferConstant(Expr.Constant expr) {
		return inferValue(expr.getValue());
	}

	protected Type inferIs(Expr.Is expr) {
		return Type.Bool;
	}

	protected Type inferInvoke(Expr.Invoke expr) {
		Tuple<Type> returns = expr.getSignature().getReturns();
		if (returns.size() != 1) {
			throw new IllegalArgumentException("need support for multiple returns");
		} else {
			return returns.getOperand(0);
		}
	}

	protected Type inferIndirectInvoke(Expr.IndirectInvoke expr) throws ResolutionError {
		Type src = inferExpression(expr.getSource());
		Type.Callable ct = types.extractReadableLambda(src);
		if(ct != null) {
			Tuple<Type> returns = ct.getReturns();
			if(returns.size() == 1) {
				return returns.getOperand(0);
			}
		}
		return null;
	}


	protected Type inferQuantifier(Expr.Quantifier expr) {
		return Type.Bool;
	}

	// ======================================================================
	// Arrays
	// ======================================================================

	protected Type inferArrayLength(Expr.Operator expr) {
		return Type.Int;
	}

	protected Type inferArrayInitialiser(Expr.Operator expr) throws ResolutionError {
		if (expr.size() > 0) {
			Type[] ts = new Type[expr.size()];
			for (int i = 0; i != ts.length; ++i) {
				ts[i] = inferExpression(expr.getOperand(i));
			}
			// Perform a little simplification here by collapsing
			// identical types together.
			ts = ArrayUtils.removeDuplicates(ts);
			Type element = ts.length == 1 ? ts[0] : new Type.Union(ts);
			return new Type.Array(element);
		} else {
			return new Type.Array(Type.Void);
		}
	}

	protected Type inferArrayGenerator(Expr.Operator expr) throws ResolutionError {
		Type element = inferExpression(expr.getOperand(0));
		return new Type.Array(element);
	}

	protected Type inferArrayIndex(Expr.Operator expr) throws ResolutionError {
		Type src = inferExpression(expr.getOperand(0));
		if(src != null) {
			Type.Array effectiveArray = types.extractReadableArray(src);
			if(effectiveArray != null) {
				return effectiveArray.getElement();
			}
		}
		return null;
	}

	protected Type inferArrayUpdate(Expr.Operator expr) throws ResolutionError {
		return inferExpression(expr.getOperand(0));
	}

	protected Type inferRecordAccess(Expr.RecordAccess expr) throws ResolutionError {
		Type src = inferExpression(expr.getSource());
		if (src != null) {
			Type.Record effectiveRecord = types.extractReadableRecord(src);
			if (effectiveRecord != null) {
				Tuple<Declaration.Variable> fields = effectiveRecord.getFields();
				Identifier actualFieldName = expr.getField();
				for (int i = 0; i != fields.size(); ++i) {
					Declaration.Variable vd = fields.getOperand(i);
					Identifier declaredFieldName = vd.getName();
					if (declaredFieldName.equals(actualFieldName)) {
						return vd.getType();
					}
				}
			}
		}
		//
		return null;
	}

	protected Type inferRecordUpdate(Expr.RecordUpdate expr) throws ResolutionError {
		return inferExpression(expr.getSource());
	}

	protected Type inferRecordInitialiser(Expr.RecordInitialiser expr) throws ResolutionError {
		Declaration.Variable[] decls = new Declaration.Variable[expr.size()];
		for (int i = 0; i != decls.length; ++i) {
			Identifier fieldName = expr.getOperand(i).getFirst();
			Type fieldType = inferExpression(expr.getOperand(i).getSecond());
			decls[i] = new Declaration.Variable(new Tuple<>(), fieldName, fieldType);
		}
		// NOTE: a record initialiser never produces an open record
		// type. By definition, an initialiser always produces a closed
		// (i.e. concrete) type.
		return new Type.Record(false, new Tuple<>(decls));
	}

	// ======================================================================
	// References
	// ======================================================================

	protected Type inferDereference(Expr.Dereference expr) throws ResolutionError {
		Type src = inferExpression(expr.getOperand());
		if(src != null) {
			Type.Reference effectiveReference = types.extractReadableReference(src);
			if(effectiveReference != null) {
				return effectiveReference.getElement();
			}
		}
		return null;
	}

	// ======================================================================
	// Values
	// ======================================================================

	protected Type inferValue(Value val) {
		switch(val.getOpcode()) {
		case ITEM_null:
			return Type.Null;
		case ITEM_bool:
			return Type.Bool;
		case ITEM_byte:
			return Type.Byte;
		case ITEM_int:
			return Type.Int;
		case ITEM_utf8:
			return new Type.Array(Type.Int);
		default:
			throw new RuntimeException("invalid value encountered (" + val + ")");
		}
	}
}
