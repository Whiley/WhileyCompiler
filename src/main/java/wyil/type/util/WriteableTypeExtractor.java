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

import wybs.lang.CompilationUnit;
import wybs.lang.NameResolver;
import wybs.lang.SyntacticItem;
import wybs.lang.SyntaxError;
import wybs.lang.NameResolver.ResolutionError;
import wyc.lang.WhileyFile.Decl;
import wyc.lang.WhileyFile.Type;
import wyc.util.ErrorMessages;

import static wyc.lang.WhileyFile.*;
import static wyc.util.ErrorMessages.errorMessage;

import wyil.type.subtyping.EmptinessTest.LifetimeRelation;
import wyil.type.subtyping.SubtypeOperator;

public class WriteableTypeExtractor {
  private final NameResolver resolver;
  private final SubtypeOperator subtypeOperator;
  private final ConcreteTypeExtractor extractor;

  public WriteableTypeExtractor(NameResolver resolver, SubtypeOperator subtypeOperator) {
    this.resolver = resolver;
    this.subtypeOperator = subtypeOperator;
    this.extractor = new ConcreteTypeExtractor(resolver);
  }

  public <T extends Type.Atom> T apply(Type type, LifetimeRelation lifetimes, Class<T> kind) {
    switch(type.getOpcode()) {
      case TYPE_null:
      case TYPE_bool:
      case TYPE_byte:
      case TYPE_int:
      case TYPE_record:
      case TYPE_array:
      case TYPE_reference:
      case TYPE_staticreference:
      case TYPE_function:
      case TYPE_method:
        return applyAtom((Type.Atom) type, lifetimes, kind);
      case TYPE_union:
        return applyUnion((Type.Union) type, lifetimes, kind);
      case TYPE_nominal:
        return applyNominal((Type.Nominal) type, lifetimes, kind);
      default:
        throw new IllegalArgumentException("invalid type for extraction");
    }
  }

  private <T extends Type.Atom> T applyAtom(Type.Atom type, LifetimeRelation lifetimes, Class<T> kind) {
    if (kind.isInstance(type)) {
      return (T) type;
    } else {
      return null;
    }
  }

  private <T extends Type.Atom> T applyNominal(Type.Nominal type, LifetimeRelation lifetimes, Class<T> kind) {
    try {
      Decl.Type decl = resolver.resolveExactly(type.getName(), Decl.Type.class);
      return apply(decl.getVariableDeclaration().getType(),lifetimes, kind);
    } catch (ResolutionError e) {
      return syntaxError(errorMessage(ErrorMessages.RESOLUTION_ERROR, type.getName().toString()), type);
    }
  }

  private <T extends Type.Atom> T applyUnion(Type.Union type, LifetimeRelation lifetimes, Class<T> kind) {
    T result = null;
    for(int i=0;i!=type.size();++i) {
      T tmp = apply(type.get(i),lifetimes, kind);
      result = union(result,tmp,lifetimes, kind);
      if(result == null) {
        // abort early
        return null;
      }
    }
    return result;
  }

  private <T extends Type.Atom> T union(T lhs, T rhs, LifetimeRelation lifetimes, Class<T> kind) {
    if(lhs != null && rhs != null) {
      if(kind == Type.Record.class) {
        return (T) union((Type.Record) lhs, (Type.Record) rhs, lifetimes);
      } else if(kind == Type.Array.class) {
        return (T) union((Type.Array) lhs, (Type.Array) rhs, lifetimes);
      } else if(kind == Type.Reference.class) {
        return (T) union((Type.Reference) lhs, (Type.Reference) rhs, lifetimes);
      }
    } else if(rhs != null) {
      return rhs;
    }
    return null;
  }

  private Type.Record union(Type.Record lhs, Type.Record rhs, LifetimeRelation lifetimes) {
    // NOTE: this may seem like a strange thing to do, but it's about code reuse.
    Type t = extractor.apply(new SemanticType.Intersection(lhs,rhs), lifetimes);
    if(t instanceof Type.Record) {
      return (Type.Record) t;
    } else {
      return null;
    }
  }

  private Type.Array union(Type.Array lhs, Type.Array rhs, LifetimeRelation lifetimes) {
    // NOTE: this may seem like a strange thing to do, but it's about code reuse.
    Type t = extractor.apply(new SemanticType.Intersection(lhs,rhs), lifetimes);
    if(t instanceof Type.Array) {
      return (Type.Array) t;
    } else {
      return null;
    }
  }

  private Type.Reference union(Type.Reference lhs, Type.Reference rhs, LifetimeRelation lifetimes) {
 // NOTE: this may seem like a strange thing to do, but it's about code reuse.
    Type t = extractor.apply(new SemanticType.Intersection(lhs,rhs), lifetimes);
    if(t instanceof Type.Array) {
      return (Type.Reference) t;
    } else {
      return null;
    }
  }


  // ================================================================
  // Helpers
  // ================================================================

  private <T> T syntaxError(String msg, SyntacticItem e) {
    // FIXME: this is a kludge
    CompilationUnit cu = (CompilationUnit) e.getHeap();
    throw new SyntaxError(msg, cu.getEntry(), e);
  }
}
