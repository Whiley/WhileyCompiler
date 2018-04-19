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
package wyil.type.util;

import static wyc.lang.WhileyFile.TYPE_array;
import static wyc.lang.WhileyFile.TYPE_bool;
import static wyc.lang.WhileyFile.TYPE_byte;
import static wyc.lang.WhileyFile.TYPE_function;
import static wyc.lang.WhileyFile.TYPE_int;
import static wyc.lang.WhileyFile.TYPE_method;
import static wyc.lang.WhileyFile.TYPE_nominal;
import static wyc.lang.WhileyFile.TYPE_null;
import static wyc.lang.WhileyFile.TYPE_record;
import static wyc.lang.WhileyFile.TYPE_reference;
import static wyc.lang.WhileyFile.TYPE_staticreference;
import static wyc.lang.WhileyFile.TYPE_union;
import static wyc.util.ErrorMessages.errorMessage;

import wyc.lang.WhileyFile.Type;
import wybs.lang.CompilationUnit;
import wybs.lang.NameResolver;
import wybs.lang.SyntacticItem;
import wybs.lang.SyntaxError;
import wybs.lang.NameResolver.ResolutionError;
import wyc.lang.WhileyFile.Decl;
import wyc.lang.WhileyFile.SemanticType;
import wyc.util.ErrorMessages;
import wycc.util.ArrayUtils;
import wyil.type.subtyping.EmptinessTest.LifetimeRelation;
import wyil.type.subtyping.SubtypeOperator;

public abstract class AbstractTypeCombinator {
	protected final NameResolver resolver;
	protected final SubtypeOperator subtyping;

	public AbstractTypeCombinator(NameResolver resolver, SubtypeOperator subtyping) {
		this.resolver = resolver;
		this.subtyping = subtyping;
	}

	protected Type apply(Type lhs, Type rhs, LifetimeRelation lifetimes) {
		// FIXME: this is obviously broken as it can infinite loop on recursive types!
		int lhsKind = normalise(lhs);
		int rhsKind = normalise(rhs);
		if (lhsKind == rhsKind) {
			// Easy case.
			switch (lhsKind) {
			case TYPE_null:
				return apply((Type.Null) lhs, (Type.Null) rhs, lifetimes);
			case TYPE_bool:
				return apply((Type.Bool) lhs, (Type.Bool) rhs, lifetimes);
			case TYPE_byte:
				return apply((Type.Byte) lhs, (Type.Byte) rhs, lifetimes);
			case TYPE_int:
				return apply((Type.Int) lhs, (Type.Int) rhs, lifetimes);
			case TYPE_array:
				return apply((Type.Array) lhs, (Type.Array) rhs, lifetimes);
			case TYPE_reference:
				return apply((Type.Reference) lhs, (Type.Reference) rhs, lifetimes);
			case TYPE_record:
				return apply((Type.Record) lhs, (Type.Record) rhs, lifetimes);
			case TYPE_union:
				return apply((Type.Union) lhs, rhs, lifetimes);
			case TYPE_function:
				return apply((Type.Function) lhs, (Type.Function) rhs, lifetimes);
			case TYPE_method:
				return apply((Type.Method) lhs, (Type.Method) rhs, lifetimes);
			case TYPE_nominal:
				return apply((Type.Nominal) lhs, rhs, lifetimes);
			default:
				throw new IllegalArgumentException("invalid type encountered: " + lhs);
			}
		} else if (lhs instanceof Type.Union) {
			return apply((Type.Union) lhs, rhs, lifetimes);
		} else if (lhs instanceof Type.Nominal) {
			return apply((Type.Nominal) lhs, rhs, lifetimes);
		} else if (rhs instanceof Type.Nominal) {
			return apply(lhs, (Type.Nominal) rhs, lifetimes);
		} else if (rhs instanceof Type.Union) {
			return apply(lhs, (Type.Union) rhs, lifetimes);
		} else {
			// Failed to combine them
			return null;
		}
	}

	protected abstract Type apply(Type.Null lhs, Type.Null rhs, LifetimeRelation lifetimes);

	protected abstract Type apply(Type.Bool lhs, Type.Bool rhs, LifetimeRelation lifetimes);

	protected abstract Type apply(Type.Byte lhs, Type.Byte rhs, LifetimeRelation lifetimes);

	protected abstract Type apply(Type.Int lhs, Type.Int rhs, LifetimeRelation lifetimes);

	protected abstract Type apply(Type.Array lhs, Type.Array rhs, LifetimeRelation lifetimes);

	protected abstract  Type apply(Type.Reference lhs, Type.Reference rhs, LifetimeRelation lifetimes);

	protected abstract Type apply(Type.Record lhs, Type.Record rhs, LifetimeRelation lifetimes);

	protected abstract  Type apply(Type.Function lhs, Type.Function rhs, LifetimeRelation lifetimes);

	protected abstract  Type apply(Type.Method lhs, Type.Method rhs, LifetimeRelation lifetimes);

	protected Type apply(Type.Union lhs, Type rhs, LifetimeRelation lifetimes) {
		Type[] types = new Type[lhs.size()];
		for (int i = 0; i != lhs.size(); ++i) {
			types[i] = apply(lhs.get(i), rhs, lifetimes);
		}
		return union(types);
	}

	protected Type apply(Type lhs, Type.Union rhs, LifetimeRelation lifetimes) {
		Type[] types = new Type[rhs.size()];
		for (int i = 0; i != types.length; ++i) {
			types[i] = apply(lhs, rhs.get(i), lifetimes);
		}
		return union(types);
	}

	protected Type apply(Type.Nominal lhs, Type rhs, LifetimeRelation lifetimes) {
		try {
			Decl.Type decl = resolver.resolveExactly(lhs.getName(), Decl.Type.class);
			return apply(decl.getVariableDeclaration().getType(), rhs, lifetimes);
		} catch (ResolutionError e) {
			return syntaxError(errorMessage(ErrorMessages.RESOLUTION_ERROR, lhs.getName().toString()), lhs);
		}
	}

	protected Type apply(Type lhs, Type.Nominal rhs, LifetimeRelation lifetimes) {
		try {
			Decl.Type decl = resolver.resolveExactly(rhs.getName(), Decl.Type.class);
			return apply(lhs, decl.getVariableDeclaration().getType(), lifetimes);
		} catch (ResolutionError e) {
			return syntaxError(errorMessage(ErrorMessages.RESOLUTION_ERROR, rhs.getName().toString()), lhs);
		}
	}

	// ===================================================================================
	// Helpers
	// ===================================================================================

	private static int normalise(SemanticType type) {
		int opcode = type.getOpcode();
		switch (opcode) {
		case TYPE_reference:
		case TYPE_staticreference:
			return TYPE_reference;
		default:
			return opcode;
		}
	}

	private Type union(Type... types) {
		types = ArrayUtils.removeAll(types, Type.Void);
		switch (types.length) {
		case 0:
			return Type.Void;
		case 1:
			return types[0];
		default:
			return new Type.Union(types);
		}
	}

	private <T> T syntaxError(String msg, SyntacticItem e) {
		// FIXME: this is a kludge
		CompilationUnit cu = (CompilationUnit) e.getHeap();
		throw new SyntaxError(msg, cu.getEntry(), e);
	}
}
