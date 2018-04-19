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

import java.util.ArrayList;

import wybs.lang.NameResolver;
import wybs.lang.NameResolver.ResolutionError;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyc.lang.WhileyFile.SemanticType;
import wyc.lang.WhileyFile.Type;
import wyc.lang.WhileyFile.Type.Array;
import wyc.lang.WhileyFile.Type.Function;
import wyc.lang.WhileyFile.Type.Method;
import wyc.lang.WhileyFile.Type.Record;
import wyc.lang.WhileyFile.Type.Reference;
import wyil.type.subtyping.EmptinessTest.LifetimeRelation;
import wyil.type.subtyping.SubtypeOperator;

public class TypeSubtractor extends AbstractTypeCombinator {

	public TypeSubtractor(NameResolver resolver, SubtypeOperator subtyping) {
		super(resolver, subtyping);
	}

	@Override
	protected Type apply(Type lhs, Type rhs, LifetimeRelation lifetimes) {
		Type t = super.apply(lhs,rhs,lifetimes);
		if(t == null) {
			return lhs;
		} else {
			return t;
		}
	}

	@Override
	protected Type apply(Type.Null lhs, Type.Null rhs, LifetimeRelation lifetimes) {
		return Type.Void;
	}

	@Override
	protected Type apply(Type.Bool lhs, Type.Bool rhs, LifetimeRelation lifetimes) {
		return Type.Void;
	}

	@Override
	protected Type apply(Type.Byte lhs, Type.Byte rhs, LifetimeRelation lifetimes) {
		return Type.Void;
	}

	@Override
	protected Type apply(Type.Int lhs, Type.Int rhs, LifetimeRelation lifetimes) {
		return Type.Void;
	}


	@Override
	protected Type apply(Array lhs, Array rhs, LifetimeRelation lifetimes) {
		Type element = apply(lhs.getElement(), rhs.getElement(), lifetimes);
		if (element instanceof Type.Void) {
			return Type.Void;
		} else {
			return new Type.Array(element);
		}
	}

	@Override
	protected Type apply(Reference lhs, Reference rhs, LifetimeRelation lifetimes) {
		//
		try {
			if (subtyping.isSubtype(lhs, rhs, lifetimes)) {
				return lhs;
			} else {
				return Type.Void;
			}
		} catch (ResolutionError e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	protected Type apply(Record lhs, Record rhs, LifetimeRelation lifetimes) {
		ArrayList<Type.Field> fields = new ArrayList<>();
		Tuple<Type.Field> lhsFields = lhs.getFields();
		Tuple<Type.Field> rhsFields = rhs.getFields();
		for (int i = 0; i != lhsFields.size(); ++i) {
			Type.Field lhsField = lhsFields.get(i);
			Identifier lhsFieldName = lhsField.getName();
			boolean matched = false;
			for (int j = 0; j != rhsFields.size(); ++j) {
				Type.Field rhsField = rhsFields.get(j);
				Identifier rhsFieldName = rhsField.getName();
				if (lhsFieldName.equals(rhsFieldName)) {
					Type diff = apply(lhsField.getType(), rhsField.getType(), lifetimes);
					fields.add(new Type.Field(lhsFieldName, diff));
					matched = true;
					break;
				}
			}
			//
			if (!matched && !rhs.isOpen()) {
				// We didn't find a corresponding field, and the rhs is fixed. This means the
				// rhs is not compatible with the lhs and can be ignored.
				return lhs;
			} else if (!matched) {
				// We didn't find a corresponding field, and the rhs is open. This just means
				// the rhs is not taking anything away from the lhs (for this field).
				fields.add(lhsField);
			}
		}
		return new Type.Record(lhs.isOpen(), new Tuple<>(fields));
	}

	@Override
	protected Type apply(Function lhs, Function rhs, LifetimeRelation lifetimes) {
		if (lhs.equals(rhs)) {
			return Type.Void;
		} else {
			return lhs;
		}
	}

	@Override
	protected Type apply(Method lhs, Method rhs, LifetimeRelation lifetimes) {
		if (lhs.equals(rhs)) {
			return Type.Void;
		} else {
			return lhs;
		}
	}

}
