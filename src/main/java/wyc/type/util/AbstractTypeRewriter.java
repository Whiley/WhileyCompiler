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

import java.util.Arrays;

import wybs.util.AbstractCompilationUnit.Tuple;
import static wyc.lang.WhileyFile.*;
import wyc.type.TypeRewriter;

public class AbstractTypeRewriter implements TypeRewriter {

	@Override
	public Type rewrite(Type type) {
		if (type instanceof Type.Primitive) {
			return rewritePrimitive((Type.Primitive) type);
		} else if (type instanceof Type.Nominal) {
			return rewriteNominal((Type.Nominal) type);
		} else if (type instanceof Type.Array) {
			return rewriteArray((Type.Array) type);
		} else if (type instanceof Type.Reference) {
			return rewriteReference((Type.Reference) type);
		} else if (type instanceof Type.Record) {
			return rewriteRecord((Type.Record) type);
		} else if (type instanceof Type.Function) {
			return rewriteFunction((Type.Function) type);
		} else if (type instanceof Type.Property) {
			return rewriteMacro((Type.Property) type);
		} else if (type instanceof Type.Negation) {
			return rewriteNegation((Type.Negation) type);
		} else if (type instanceof Type.Union) {
			return rewriteUnion((Type.Union) type);
		} else {
			return rewriteIntersection((Type.Intersection) type);
		}
	}

	protected Type rewritePrimitive(Type.Primitive type) {
		return type;
	}

	protected Type rewriteNominal(Type.Nominal type) {
		return type;
	}

	protected Type rewriteArray(Type.Array type) {
		Type element = type.getElement();
		Type nElement = rewrite(element);
		if (element != nElement) {
			return new Type.Array(nElement);
		} else {
			return type;
		}
	}

	protected Type rewriteReference(Type.Reference type) {
		Type element = type.getElement();
		Type nElement = rewrite(element);
		if (element != nElement) {
			return new Type.Reference(nElement, type.getLifetime());
		} else {
			return type;
		}
	}

	protected Type rewriteRecord(Type.Record type) {
		Tuple<Declaration.Variable> fields = type.getFields();
		// The purpose of this implementation is try and reduce memory
		// allocations and, hence, put less pressure on the garbage collector.
		Declaration.Variable[] nFields = null;
		for (int i = 0; i != fields.size(); ++i) {
			Declaration.Variable field = fields.getOperand(i);
			Type fieldType = field.getType();
			Type nFieldType = rewrite(fieldType);
			if (nFields != null) {
				// Have previously seen new field, therefore maintain new array.
				nFields[i] = new Declaration.Variable(new Tuple<>(), field.getName(), nFieldType);
			} else if (fieldType != nFieldType) {
				// Lazily construct array of new items now that we know at least
				// one is different.
				nFields = new Declaration.Variable[fields.size()];
				// Copy all items seen so far into the new array
				for (int j = 0; j < i; ++j) {
					nFields[j] = fields.getOperand(j);
				}
				// Copy this item into the new array.
				nFields[i] = new Declaration.Variable(new Tuple<>(), field.getName(), nFieldType);
			}
		}
		if (nFields != null) {
			return new Type.Record(type.isOpen(), new Tuple<>(nFields));
		} else {
			return type;
		}
	}

	protected Type rewriteFunction(Type.Function type) {
		Tuple<Type> params = type.getParameters();
		Tuple<Type> returns = type.getReturns();
		Tuple<Type> nParams = rewrite(params);
		Tuple<Type> nReturns = rewrite(returns);
		if (params == nParams && returns == nReturns) {
			return type;
		} else {
			return new Type.Function(nParams, nReturns);
		}
	}

	protected Type rewriteMacro(Type.Property type) {
		Tuple<Type> params = type.getParameters();
		Tuple<Type> nParams = rewrite(params);
		if (params == nParams) {
			return type;
		} else {
			return new Type.Property(nParams);
		}
	}

	protected Type rewriteNegation(Type.Negation type) {
		Type element = type.getElement();
		Type nElement = rewrite(element);
		if (element != nElement) {
			return new Type.Negation(nElement);
		} else {
			return type;
		}
	}

	protected Type rewriteUnion(Type.Union utype) {
		Type[] types = utype.getOperands();
		Type[] nTypes = rewrite(types);
		//
		if (types == nTypes) {
			return utype;
		} else {
			return new Type.Union(nTypes);
		}
	}

	protected Type rewriteIntersection(Type.Intersection utype) {
		Type[] types = utype.getOperands();
		Type[] nTypes = rewrite(types);
		if (types == nTypes) {
			return utype;
		} else {
			return new Type.Intersection(nTypes);
		}
	}

	protected Tuple<Type> rewrite(Tuple<Type> tuple) {
		Type[] nTypes = new Type[tuple.size()];
		boolean changed = false;
		//
		for (int i = 0; i != tuple.size(); ++i) {
			Type type = tuple.getOperand(i);
			Type nType = rewrite(type);
			changed |= (type != nType);
			nTypes[i] = nType;
		}
		//
		if (!changed) {
			return tuple;
		} else {
			return new Tuple<>(nTypes);
		}
	}

	protected Type[] rewrite(Type[] types) {
		Type[] nTypes = types;
		//
		for (int i = 0; i != types.length; ++i) {
			Type type = types[i];
			Type nType = rewrite(type);
			if (type != nType) {
				if (types == nTypes) {
					nTypes = Arrays.copyOf(types, types.length);
				}
				nTypes[i] = nType;
			}
		}
		return nTypes;
	}
}
