// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
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

package wyjvm.lang;

import java.util.*;

import wybs.util.Pair;

public class JvmTypes {
	
	/**
     * Given a primitive type, determine the equivalent boxed type. For example,
     * the primitive type int yields the type java.lang.Integer. 
     * 
     * @param p
     * @return
     */
	public static JvmType.Clazz boxedType(JvmType.Primitive p) {
		if(p instanceof JvmType.Bool) {
			return new JvmType.Clazz("java.lang","Boolean");
		} else if(p instanceof JvmType.Byte) {
			return new JvmType.Clazz("java.lang","Byte");
		} else if(p instanceof JvmType.Char) {
			return new JvmType.Clazz("java.lang","Character");
		} else if(p instanceof JvmType.Short) {
			return new JvmType.Clazz("java.lang","Short");
		} else if(p instanceof JvmType.Int) {
			return new JvmType.Clazz("java.lang","Integer");
		} else if(p instanceof JvmType.Long) {
			return new JvmType.Clazz("java.lang","Long");
		} else if(p instanceof JvmType.Float) {
			return new JvmType.Clazz("java.lang","Float");
		} else {
			return new JvmType.Clazz("java.lang","Double");
		}
	}
	
	/**
	 * Given a primitive wrapper class (i.e. a boxed type), return the unboxed
	 * equivalent. For example, java.lang.Integer yields Type.Int, whilst
	 * java.lang.Boolean yields Type.Bool.
	 * 
	 * @param p
	 * @return
	 */
	public static JvmType.Primitive unboxedType(JvmType.Clazz p) {
		String type = p.components().get(p.components().size()-1).first();
		
		if(type.equals("Boolean")) {
			return new JvmType.Bool();
		} else if(type.equals("Byte")) {
			return new JvmType.Byte();
		} else if(type.equals("Character")) {
			return new JvmType.Char();
		} else if(type.equals("Short")) {
			return new JvmType.Short();
		} else if(type.equals("Integer")) {
			return new JvmType.Int();
		} else if(type.equals("Long")) {
			return new JvmType.Long();
		} else if(type.equals("Float")) {
			return new JvmType.Float();
		} else if(type.equals("Double")) {
			return new JvmType.Double();
		} else {
			throw new RuntimeException("unknown boxed type \"" + p.toString()
					+ "\" encountered.");			
		}
	}
	
	/**
	 * Determine whether or not the given type is a wrapper for a primitive
	 * type.  E.g. java.lang.Integer is a wrapper for int.
	 * 
	 * @param t
	 * @return
	 */
	public static boolean isBoxedType(JvmType t) {
		if(!(t instanceof JvmType.Clazz)) {
			return false;
		}
		JvmType.Clazz ref = (JvmType.Clazz) t;
		if(ref.pkg().equals("java.lang") && ref.components().size() == 1) {
			String s = ref.components().get(0).first();
			if(s.equals("Byte") || s.equals("Character") || s.equals("Short") ||
				s.equals("Integer") || s.equals("Long")
					|| s.equals("Float") || s.equals("Double")
					|| s.equals("Boolean")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Determine whether or not the given type is a wrapper for a primitive
	 * type. E.g. java.lang.Integer is a wrapper for int.
	 * 
	 * @param t
	 *            --- type to test
	 * @param wrapper
	 *            --- specific wrapper class to look for (i.e. Integer, Boolean,
	 *            Character).
	 * @return
	 */
	public static boolean isBoxedTypeOf(JvmType t, String wrapper) {
		if(!(t instanceof JvmType.Clazz)) {
			return false;
		}
		JvmType.Clazz ref = (JvmType.Clazz) t;
		if(ref.pkg().equals("java.lang") && ref.components().size() == 1) {
			String s = ref.components().get(0).first();
			if(s.equals(wrapper)) {
				return true;
			}
		}
		return false;
	}
	/**
     * This method accepts a binding from type variables to concrete types, and
     * then substitutes each such variable occuring in the target type with its
     * corresponding instantation. For example, suppose we have this binding:
     * 
     * <pre>
     *  K -&gt; String
     *  V -&gt; Integer
     * </pre>
     * 
     * Then, substituting against <code>HashMap<K,V></code> yields
     * <code>HashMap<String,Integer></code>.
     * 
     * @param type
     * @param binding
     * @return
     */
	public static JvmType.Reference substitute(JvmType.Reference type, Map<String,JvmType.Reference> binding) {
		if (type instanceof JvmType.Variable) {
			// Ok, we've reached a type variable, so we can now bind this with
			// what we already have.
			JvmType.Variable v = (JvmType.Variable) type;
			JvmType.Reference r = binding.get(v.variable());
			if(r == null) {
				// if the variable is not part of the binding, then we simply do
                // not do anything with it.
				return v;
			} else {
				return r;
			}
		} else if(type instanceof JvmType.Wildcard) {
			JvmType.Wildcard wc = (JvmType.Wildcard) type;
			JvmType.Reference lb = wc.lowerBound();
			JvmType.Reference ub = wc.upperBound();
			if(lb != null) { lb = substitute(lb,binding); }
			if(ub != null) { ub = substitute(ub,binding); }
			return new JvmType.Wildcard(lb,ub);
		} else if(type instanceof JvmType.Array) {
			JvmType.Array at = (JvmType.Array) type;
			if(at.element() instanceof JvmType.Reference) {
				return new JvmType.Array(substitute((JvmType.Reference) at.element(),binding));
			} else {
				return type;
			}
		} else if(type instanceof JvmType.Clazz) {
			JvmType.Clazz ct = (JvmType.Clazz) type;
			ArrayList<Pair<String,List<JvmType.Reference>>> ncomponents = new ArrayList();
			List<Pair<String,List<JvmType.Reference>>> components = ct.components();
			
			for(Pair<String,List<JvmType.Reference>> c : components) {
				ArrayList<JvmType.Reference> nc = new ArrayList<JvmType.Reference>();
				for(JvmType.Reference r : c.second()) {
					nc.add(substitute(r,binding));
				}
				ncomponents.add(new Pair(c.first(),nc));
			}
			
			return new JvmType.Clazz(ct.pkg(),ncomponents);
		}
		
		throw new RuntimeException("Cannot substitute against type " + type);
	}
	
	/**
	 * This method accepts a binding from type variables to concrete types, and
	 * then substitutes each such variable occuring in the target (function)
	 * type with its corresponding instantation. For example, suppose we have
	 * this binding:
	 * 
	 * <pre>
	 *  K -&gt; String
	 *  V -&gt; Integer
	 * </pre>
	 * 
	 * Then, substituting against <code>v f(K)</code> yields
	 * <code>Integer f(String)</code>.
	 * 
	 * @param type
	 * @param binding
	 * @return
	 */
	public static JvmType.Function substitute(JvmType.Function type, Map<String,JvmType.Reference> binding) {
		JvmType returnType = type.returnType();
		
		if(returnType instanceof JvmType.Reference) {
			returnType = substitute((JvmType.Reference) returnType,binding);
		}
		
		ArrayList<JvmType> paramTypes = new ArrayList<JvmType>();
		for(JvmType t : type.parameterTypes()) {
			if(t instanceof JvmType.Reference) {
				t = substitute((JvmType.Reference)t,binding);
			}
			paramTypes.add(t);
		}
		
		ArrayList<JvmType.Variable> varTypes = new ArrayList<JvmType.Variable>();
		for(JvmType.Variable v : type.typeArguments()) {
			if(!binding.containsKey(v.variable())) {
				varTypes.add(v);	
			}			
		}
		
		return new JvmType.Function(returnType,paramTypes,varTypes);
	}
	/**
     * Check wither a given type is a reference to java.lang.Object or not.
     * 
     * @param t
     * @return
     */
	public static boolean isJavaLangObject(JvmType t) {
		if(t instanceof JvmType.Clazz) {
			JvmType.Clazz c = (JvmType.Clazz) t;
			 return c.pkg().equals("java.lang") && c.components().size() == 1
					&& c.components().get(0).first().equals("Object");			
		}
		return false;
	}
	
	/**
     * Check wither a given type is a reference to java.lang.String or not.
     * 
     * @param t
     * @return
     */
	public static boolean isJavaLangString(JvmType t) {
		if(t instanceof JvmType.Clazz) {
			JvmType.Clazz c = (JvmType.Clazz) t;
			 return c.pkg().equals("java.lang") && c.components().size() == 1
					&& c.components().get(0).first().equals("String");			
		}
		return false;
	}
	
	public static boolean isClass(String pkg, String clazz, JvmType t) {
		if(t instanceof JvmType.Clazz) {
			JvmType.Clazz c = (JvmType.Clazz) t;
			 return c.pkg().equals(pkg) && c.components().size() == 1
					&& c.components().get(0).first().equals(clazz);
		}
		return false;
	}
	
	public static boolean isGeneric(JvmType t) {
		if (t instanceof JvmType.Variable) {
			return true;
		} else if(t instanceof JvmType.Function) {
			JvmType.Function ft = (JvmType.Function) t;
			if(ft.typeArguments().size() > 0) {
				return true;
			} else {
				for(JvmType p : ft.parameterTypes()) {
					if(isGeneric(p)) {
						return true;
					}
				}
			}
			return isGeneric(ft.returnType());
		} else if (!(t instanceof JvmType.Clazz)) {
			return false;
		}
		JvmType.Clazz ref = (JvmType.Clazz) t;
		for(Pair<String, List<JvmType.Reference>> p : ref.components()) {
			if(p.second().size() > 0) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isGenericArray(JvmType t) {
		if(t instanceof JvmType.Array) {
			JvmType et = ((JvmType.Array)t).element();
			if(et instanceof JvmType.Variable) {
				return true;
			} else {
				return isGenericArray(et);
			}
		} 
		
		return false;	
	}
	
	/**
	 * Return the depth of array nesting. E.g. "int" has 0 depth, "int[]" has
	 * depth 1, "int[][]" has depth 2, etc.
	 * 
	 * @param t
	 * @return
	 */
	public static int arrayDepth(JvmType t) {
		if(t instanceof JvmType.Array) {
			JvmType.Array _t = (JvmType.Array) t;
			return 1 + arrayDepth(_t.element());
		} else {
			return 0;
		}
	}	
	
	/**
	 * Return type representing the enclosing class for the given type, or null
	 * if the given type is already outermost. For example, given
	 * <code>pkg.Test$inner</code> return <code>pkg.Test</code>.
	 * 
	 * @param t
	 * @return
	 */
	public static JvmType.Clazz parentType(JvmType.Clazz t) {
		List<Pair<String,List<JvmType.Reference>>> components = t.components(); 
		if(components.size() == 0) {
			return null;
		}
		ArrayList<Pair<String,List<JvmType.Reference>>> ncomponents = new ArrayList();
		for(int i=0;i!=components.size()-1;++i) {
			ncomponents.add(components.get(i));
		}
		return new JvmType.Clazz(t.pkg(),ncomponents);
	}	
	
	/**
     * The purpose of this method is to strip the generic information from a
     * type.
     * 
     * @param t
     * @return
     */
	public static JvmType stripGenerics(JvmType t) {
		if(t instanceof JvmType.Clazz) {
			return stripGenerics((JvmType.Clazz)t);
		} else if(t instanceof JvmType.Function) {
			return stripGenerics((JvmType.Function)t);
		} else if(t instanceof JvmType.Variable) {
			return stripGenerics((JvmType.Variable)t);
		} else if(t instanceof JvmType.Wildcard) {
			return stripGenerics((JvmType.Wildcard)t);
		} else if(t instanceof JvmType.Intersection) {
			return stripGenerics((JvmType.Intersection)t);
		} else {		
			return t;
		}
	}
	
	public static JvmType.Clazz stripGenerics(JvmType.Clazz ct) {
		ArrayList<Pair<String,List<JvmType.Reference>>> ncomponents = new ArrayList();
		for(Pair<String,List<JvmType.Reference>> p : ct.components()) {
			ncomponents.add(new Pair(p.first(),new ArrayList()));
		}
		return new JvmType.Clazz(ct.pkg(),ncomponents);
	}
	
	public static JvmType.Function stripGenerics(JvmType.Function ft) {
		ArrayList<JvmType> params = new ArrayList<JvmType>();
		for(JvmType t : ft.parameterTypes()) {
			params.add(stripGenerics(t));
		}
		
		return new JvmType.Function(stripGenerics(ft.returnType()),params);
	}

	public static JvmType stripGenerics(JvmType.Variable vt) {
		if(vt.lowerBound() == null) {
			return JAVA_LANG_OBJECT;
		} else {
			return stripGenerics(vt.lowerBound());
		}
	}

	public static JvmType stripGenerics(JvmType.Wildcard wt) {		
		if(wt.lowerBound() == null) {
			return JAVA_LANG_OBJECT;
		} else {
			return stripGenerics(wt.lowerBound());
		}
	}	
	
	public static JvmType.Intersection stripGenerics(JvmType.Intersection wt) {
		ArrayList<JvmType.Reference> bounds = new ArrayList();
		for(JvmType.Reference t : wt.bounds()) {
			bounds.add((JvmType.Reference) stripGenerics(t));
		}
		return new JvmType.Intersection(bounds);
	}
	
	/**
	 * The following are provided for performance reasons, particularly to help
	 * reduce the memory footprint during compilation.
	 */
	public static final JvmType.Void T_VOID = new JvmType.Void();
	public static final JvmType.Null T_NULL = new JvmType.Null();
	public static  final JvmType.Bool T_BOOL = new JvmType.Bool();
	public static  final JvmType.Byte T_BYTE = new JvmType.Byte();
	public static  final JvmType.Char T_CHAR = new JvmType.Char();
	public static  final JvmType.Short T_SHORT = new JvmType.Short();
	public static  final JvmType.Int T_INT = new JvmType.Int();
	public static  final JvmType.Long T_LONG = new JvmType.Long();
	public static  final JvmType.Float T_FLOAT = new JvmType.Float();
	public static  final JvmType.Double T_DOUBLE = new JvmType.Double();
	
	public static final JvmType.Clazz JAVA_LANG_OBJECT = new JvmType.Clazz("java.lang","Object");
	public static final JvmType.Clazz JAVA_LANG_CLONEABLE = new JvmType.Clazz("java.lang","Cloneable");
	public static final JvmType.Clazz JAVA_LANG_STRING = new JvmType.Clazz("java.lang","String");
	public static final JvmType.Clazz JAVA_LANG_ENUM = new JvmType.Clazz("java.lang","Enum");
	
	public static final JvmType.Clazz JAVA_LANG_BOOLEAN = new JvmType.Clazz("java.lang","Boolean");
	public static final JvmType.Clazz JAVA_LANG_CHARACTER = new JvmType.Clazz("java.lang","Character");
	public static final JvmType.Clazz JAVA_LANG_BYTE = new JvmType.Clazz("java.lang","Byte");
	public static final JvmType.Clazz JAVA_LANG_SHORT = new JvmType.Clazz("java.lang","Short");
	public static final JvmType.Clazz JAVA_LANG_INTEGER = new JvmType.Clazz("java.lang","Integer");
	public static final JvmType.Clazz JAVA_LANG_LONG = new JvmType.Clazz("java.lang","Long");
	public static final JvmType.Clazz JAVA_LANG_FLOAT = new JvmType.Clazz("java.lang","Float");
	public static final JvmType.Clazz JAVA_LANG_DOUBLE = new JvmType.Clazz("java.lang","Double");
	
	// io
	public static final JvmType.Clazz JAVA_IO_SERIALIZABLE = new JvmType.Clazz("java.io","Serializable");
	
	// util
 	public static final JvmType.Clazz JAVA_UTIL_ITERATOR = new JvmType.Clazz("java.util","Iterator");
	
	// exceptions related types
	public static  final JvmType.Clazz JAVA_LANG_THROWABLE = new JvmType.Clazz("java.lang","Throwable");
	public static  final JvmType.Clazz JAVA_LANG_RUNTIMEEXCEPTION = new JvmType.Clazz("java.lang","RuntimeException");
	public static  final JvmType.Clazz JAVA_LANG_VIRTUALMACHINEERROR = new JvmType.Clazz("java.lang","VirtualMachineError");
	public static  final JvmType.Clazz JAVA_LANG_NULLPOINTEREXCEPTION = new JvmType.Clazz("java.lang","NullPointerException");
	public static  final JvmType.Clazz JAVA_LANG_ARITHMETICEXCEPTION = new JvmType.Clazz("java.lang","ArithmeticException");	
}