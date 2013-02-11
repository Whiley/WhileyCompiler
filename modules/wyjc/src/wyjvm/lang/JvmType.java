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

/**
 * Represents types used in Java programs (e.g. int, String, Object[], etc).
 * <p>
 * JKit provides classes and methods for representing and manipulating Java
 * types (such <code>int</code>, <code>String[]</code> etc). The majority
 * of these can be found here. For example, the <code>Type.Int</code> class is
 * used to represent <code>int</code> types, whilst
 * <code>Type.Reference</code> represents general reference types, such as
 * <code>java.lang.String</code>.
 */
public interface JvmType extends Comparable<JvmType> {
	
	/**
	 * <p>
	 * This method returns the list of generic variables used in this type. So,
	 * for example, suppose we have:
	 * </p>
	 * 
	 * <pre>
	 * T = java.lang.ArrayList&lt;? extends S&gt;
	 * </pre>
	 * 
	 * <p>
	 * Then, <code>T.usedVariables()=[S]</code>.
	 * </p>
	 * 
	 * @return
	 */
	public List<JvmType.Variable> usedVariables();
	
	/**
     * The Primitive type abstracts all the primitive types.
     */
	public interface Primitive extends JvmType {}
	
	/**
     * The Reference type abstracts all the reference types, including class
     * types, array types, variable and wildcard types.
     */
	public interface Reference extends JvmType {}
	
	/**
     * The Null type is a special type given to the null value. We require that
     * Null is a subtype of any Reference.
     */
	public static class Null implements Reference {		
		public String toString() {
			return "null";
		}
		
		public boolean equals(Object o) {
			return o instanceof JvmType.Null;
		}
		
		public int hashCode() {
			return 0;
		}
		
		public int compareTo(JvmType t) {
			if(t instanceof JvmType.Null) {
				return 0;
			} else {
				return -1;
			}
		}
		
		public List<JvmType.Variable> usedVariables() {
			return new ArrayList<JvmType.Variable>();
		}
	}	 
	
	/**
     * The Void type is used to represent "void" types, found in method
     * declarations.
     * 
     * @author David J. Pearce
     * 
     */
	public static class Void implements Primitive {		
		public String toString() {
			return "void";
		}
		
		public boolean equals(Object o) {
			return o instanceof JvmType.Void;
		}
		
		public int hashCode() {
			return 1;
		}
		
		public int compareTo(JvmType t) {
			if (t instanceof JvmType.Null) {
				return 1;
			} else if (t instanceof JvmType.Void) {
				return 0;
			} else {
				return -1;
			}
		}
		
		public List<JvmType.Variable> usedVariables() {
			return new ArrayList<JvmType.Variable>();
		}
	}
	/**
	 * Represents the Java type "boolean"
	 * @author David J. Pearce
	 *
	 */
	public static class Bool implements Primitive {		
		public String toString() {
			return "boolean";
		}
		
		public boolean equals(Object o) {
			return o instanceof JvmType.Bool;
		}
		
		public int hashCode() {
			return 2;
		}
		
		public int compareTo(JvmType t) {
			if (t instanceof JvmType.Null || t instanceof JvmType.Void) {
				return 1;
			} else if (t instanceof JvmType.Bool) {
				return 0;
			} else {
				return -1;
			}
		}
		
		public List<JvmType.Variable> usedVariables() {
			return new ArrayList<JvmType.Variable>();
		}
	}
	

	/**
	 * Represents the Java type "byte"
	 * @author David J. Pearce
	 *
	 */
	public static class Byte implements Primitive {
		public String toString() {
			return "byte";
		}
		
		public boolean equals(Object o) {
			return o instanceof JvmType.Byte;
		}
		
		public int hashCode() {
			return 3;
		}
		
		public int compareTo(JvmType t) {
			if (t instanceof JvmType.Null || t instanceof JvmType.Void
					|| t instanceof JvmType.Bool) {
				return 1;
			} else if (t instanceof JvmType.Byte) {
				return 0;
			} else {
				return -1;
			}
		}
		
		public List<JvmType.Variable> usedVariables() {
			return new ArrayList<JvmType.Variable>();
		}
	}
	
	/**
	 * Represents the Java type "char"
	 * @author David J. Pearce
	 *
	 */
	public static class Char implements Primitive {
		public String toString() {
			return "char";
		}
		
		public boolean equals(Object o) {
			return o instanceof JvmType.Char;
		}
		
		public int hashCode() {
			return 4;
		}
		
		public int compareTo(JvmType t) {
			if (t instanceof JvmType.Char) {
				return 0;
			} else if (t instanceof JvmType.Null || t instanceof JvmType.Void
					|| t instanceof JvmType.Bool || t instanceof JvmType.Byte) {
				return 1;
			} else {
				return -1;
			}
		}
		
		public List<JvmType.Variable> usedVariables() {
			return new ArrayList<JvmType.Variable>();
		}
	}
	
	/**
	 * Represents the Java type "short"
	 * @author David J. Pearce
	 *
	 */
	public static class Short implements Primitive {
		public String toString() {
			return "short";
		}
		
		public boolean equals(Object o) {
			return o instanceof JvmType.Short;
		}
		
		public int hashCode() {
			return 5;
		}
		
		public int compareTo(JvmType t) {
			if (t instanceof JvmType.Short) {
				return 0;
			} else if (t instanceof JvmType.Null || t instanceof JvmType.Void
					|| t instanceof JvmType.Bool || t instanceof JvmType.Byte
					|| t instanceof JvmType.Char) {
				return 1;
			} else {
				return -1;
			}
		}
		
		public List<JvmType.Variable> usedVariables() {
			return new ArrayList<JvmType.Variable>();
		}
	}

	/**
	 * Represents the Java type "int"
	 * @author David J. Pearce
	 *
	 */
	public static class Int implements Primitive {
		public String toString() {
			return "int";
		}
		
		public boolean equals(Object o) {
			return o instanceof JvmType.Int;
		}
		
		public int hashCode() {
			return 6;
		}
		
		public int compareTo(JvmType t) {
			if (t instanceof JvmType.Int) {
				return 0;
			} else if (t instanceof JvmType.Null || t instanceof JvmType.Void
					|| t instanceof JvmType.Bool || t instanceof JvmType.Byte
					|| t instanceof JvmType.Char || t instanceof JvmType.Short) {
				return 1;
			} else {
				return -1;
			}
		}
		
		public List<JvmType.Variable> usedVariables() {
			return new ArrayList<JvmType.Variable>();
		}
	}
	
	/**
	 * Represents the Java type "long"
	 * @author David J. Pearce
	 *
	 */
	public static class Long implements Primitive {
		public String toString() {
			return "long";
		}
		
		public boolean equals(Object o) {
			return o instanceof JvmType.Long;
		}
		
		public int hashCode() {
			return 7;
		}
		
		public int compareTo(JvmType t) {
			if (t instanceof JvmType.Long) {
				return 0;
			} else if (t instanceof JvmType.Null || t instanceof JvmType.Void
					|| t instanceof JvmType.Bool || t instanceof JvmType.Byte
					|| t instanceof JvmType.Char || t instanceof JvmType.Int) {
				return 1;
			} else {
				return -1;
			}
		}
		
		public List<JvmType.Variable> usedVariables() {
			return new ArrayList<JvmType.Variable>();
		}
	}
	
	/**
	 * Represents the Java type "float"
	 * @author David J. Pearce
	 *
	 */
	public static class Float implements Primitive {
		public String toString() {
			return "float";
		}
		
		public boolean equals(Object o) {
			return o instanceof JvmType.Float;
		}
		
		public int hashCode() {
			return 8;
		}
		
		public int compareTo(JvmType t) {
			if (t instanceof JvmType.Float) {
				return 0;
			} else if (t instanceof JvmType.Null || t instanceof JvmType.Void
					|| t instanceof JvmType.Bool || t instanceof JvmType.Byte
					|| t instanceof JvmType.Char || t instanceof JvmType.Int
					|| t instanceof JvmType.Long) {
				return 1;
			} else {
				return -1;
			}
		}
		
		public List<JvmType.Variable> usedVariables() {
			return new ArrayList<JvmType.Variable>();
		}
	}
	
	/**
	 * Represents the Java type "double"
	 * @author David J. Pearce
	 *
	 */
	public static class Double implements Primitive {
		public String toString() {
			return "double";
		}
		
		public boolean equals(Object o) {
			return o instanceof JvmType.Double;
		}
		
		public int hashCode() {
			return 9;
		}
		
		public int compareTo(JvmType t) {
			if (t instanceof JvmType.Double) {
				return 0;
			} else if (t instanceof JvmType.Null || t instanceof JvmType.Void
					|| t instanceof JvmType.Bool || t instanceof JvmType.Byte
					|| t instanceof JvmType.Char || t instanceof JvmType.Int
					|| t instanceof JvmType.Long || t instanceof JvmType.Float) {
				return 1;
			} else {
				return -1;
			}
		}
		
		public List<JvmType.Variable> usedVariables() {
			return new ArrayList<JvmType.Variable>();
		}
	}
	
	/**
     * The Array type captures array types! The elementType gives the types of
     * the elements held in the array. For example, in "int[]", the element type
     * is int.
     * 
     * @author David J. Pearce
     */
	public static class Array implements Reference {		
		private final JvmType element;
		
		public Array(JvmType element) {
			if (element == null) {
				throw new IllegalArgumentException(
						"Supplied element type cannot be null.");
			}
			this.element = element;			
		}
		
		public JvmType element() {
			return element;
		}
		public String toString() {
			return element + "[]";
		}
		
		public boolean equals(Object o) {
			if(o instanceof JvmType.Array) {
				JvmType.Array a = (JvmType.Array) o;
				return element.equals(a.element);
			}
			return false;
		}
		
		public int hashCode() {
			return 1 + element.hashCode();
		}
		
		public int compareTo(JvmType t) {
			if (t instanceof JvmType.Array) {
				return element.compareTo(((JvmType.Array) t).element());
			} else if (t instanceof JvmType.Primitive) {
				return 1;
			} else {
				return -1;
			}
		}
		
		public List<JvmType.Variable> usedVariables() {
			return element.usedVariables();
		}
	}
	
	/**
     * This represents a reference to a class. E.g. java.lang.String
     * 
     * @author David J. Pearce
     * 
     */
	public static class Clazz implements Reference {		
		private final String pkg;
		private final List<Pair<String, List<JvmType.Reference>>> components;
		
		public Clazz(String pkg,
				List<Pair<String, List<JvmType.Reference>>> components) {
			if (components == null) {
				throw new IllegalArgumentException(
						"Supplied class components type cannot be null.");
			}
			this.pkg = pkg;
			this.components = components;
		}

		public Clazz(String pkg, String... components) {
			this.pkg = pkg;
			this.components = new ArrayList<Pair<String, List<JvmType.Reference>>>();
			for (String c : components) {
				this.components.add(new Pair<String, List<JvmType.Reference>>(
						c, new ArrayList<JvmType.Reference>()));
			}
		}
		
		public List<Pair<String, List<JvmType.Reference>>> components() {
			return components;
		}
		
		public Pair<String, List<JvmType.Reference>> lastComponent() {
			return components.get(components.size()-1);
		}
		
		/**
         * Return the package. If no package, then the value is simply "",
         * rather than null.
         * 
         * @return
         */
		public String pkg() {
			return pkg;
		}
		
		public String toString() {
			String r = pkg;			
			boolean firstTime = pkg.length() == 0;
			for (Pair<String, List<JvmType.Reference>> n : components) {
				if (!firstTime) {
					r += ".";
				}
				firstTime = false;
				r += n.first();
				List<JvmType.Reference> typeArgs = n.second();
				if (typeArgs != null && typeArgs.size() > 0) {
					r += "<";
					boolean innerFirstTime = true;
					for (JvmType t : typeArgs) {
						if (!innerFirstTime) {
							r += ", ";
						}
						innerFirstTime = false;
						r += t;
					}
					r += ">";
				}
			}
			return r;
		}
		
		public boolean equals(Object o) {
			if(o instanceof JvmType.Clazz) {
				JvmType.Clazz c = (JvmType.Clazz) o;
				
				return pkg.equals(c.pkg) &&
					components.equals(c.components);
			}
			return false;
		}
		
		public int hashCode() {
			int hc = 0;
			for (Pair<String, List<JvmType.Reference>> n : components) {
				hc ^= n.first().hashCode();
			}
			return hc;
		}
		
		public int compareTo(JvmType t) {
			if (t instanceof JvmType.Clazz) {
				JvmType.Clazz tc = (JvmType.Clazz) t;
				int pct = pkg.compareTo(tc.pkg);
				if(pct != 0) { return pct; }
				if(components.size() < tc.components.size()) {
					return -1;
				} else if(components.size() == tc.components.size()) {
					return 1;
				}
				for(int i=0;i!=components.size();++i) {
					Pair<String,List<JvmType.Reference>> t1 = components.get(i);
					Pair<String,List<JvmType.Reference>> t2 = tc.components.get(i);
					int fct = t1.first().compareTo(t2.first());
					if(fct != 0) { return fct; }
					if(t1.second().size() < t2.second().size()) {
						return -1;
					} else if(t1.second().size() > t2.second().size()) {
						return 1;
					}
					for(int j=0;j!=t1.second().size();++j) {
						JvmType.Reference r1 = t1.second().get(j);
						JvmType.Reference r2 = t2.second().get(j);
						int rct = r1.compareTo(r2);
						if(rct != 0) { return rct; }
					}
				}
				return 0;
			} else if (t instanceof JvmType.Primitive || t instanceof JvmType.Array) {
				return 1;
			} else {
				return -1;
			}
		}
		
		public List<JvmType.Variable> usedVariables() {
			ArrayList<JvmType.Variable> ls = new ArrayList();
			for(Pair<String,List<JvmType.Reference>> p : components) {
				for(JvmType.Reference r : p.second()) {
					ls.addAll(r.usedVariables());
				}
			}
			return ls;
		}
	}
	
	/**
     * This represents the special "?" type. As used, for example, in the
     * following method declaration:
     * 
     *  void printAll(Collection<? extends MyClass> { ... }
     * 
     * @author David J. Pearce
     * 
     */
	public static class Wildcard implements Reference {
		private final JvmType.Reference lowerBound;
		private final JvmType.Reference upperBound;

		public Wildcard(JvmType.Reference lowerBound, JvmType.Reference upperBound) {			
			this.lowerBound = lowerBound;
			this.upperBound = upperBound;
		}

		/**
         * Return the upper bound of this wildcard. This will be null if there
         * is none.
         * 
         * @return
         */
		public JvmType.Reference upperBound() {
			return upperBound;
		}

		/**
         * Return the lower bound of this wildcard. This will be null if there
         * is none.
         * 
         * @return
         */
		public JvmType.Reference lowerBound() {
			return lowerBound;
		}
		
		public boolean equals(Object o) {
			if (o instanceof Wildcard) {
				Wildcard w = (Wildcard) o;
				boolean lb;
				if(lowerBound == null) {
					lb = w.lowerBound == null;
				} else {
					lb = lowerBound.equals(w.lowerBound);
				}
				boolean ub;
				if(upperBound == null) {
					ub = w.upperBound == null;
				} else {
					ub = upperBound.equals(w.upperBound);
				}
				
				return lb && ub;
			}
			return false;
		}
		
		public String toString() {
			String r = "?";
			if(lowerBound != null) {
				r += " extends " + lowerBound;							
			}
			if(upperBound != null) {
				r += " super " + upperBound;							
			}
			return r;
		}
		
		public int hashCode() {
			int hc = 0;
			if(lowerBound != null) {
				hc ^= lowerBound.hashCode();
			}
			if(upperBound != null) {
				hc ^= upperBound.hashCode();
			}
			return hc;
		}
		
		public int compareTo(JvmType t) {
			if (t instanceof JvmType.Wildcard) {
				JvmType.Wildcard tw = (JvmType.Wildcard) t;
				if(lowerBound == null && tw.lowerBound != null) {
					return -1;
				} else if(lowerBound != null && tw.lowerBound == null) {
					return 1;
				}
				if(upperBound == null && tw.upperBound != null) {
					return -1;
				} else if(upperBound != null && tw.upperBound == null) {
					return 1;
				} 
				if(lowerBound != null) {
					int lbct = lowerBound.compareTo(tw.lowerBound);
					if(lbct != 0) { return lbct; }
				}
				if(upperBound != null) {
					int lbct = upperBound.compareTo(tw.upperBound);
					if(lbct != 0) { return lbct; }
				}
				return 0;
			} else if (t instanceof JvmType.Primitive || t instanceof JvmType.Array
					|| t instanceof JvmType.Clazz) {
				return 1;
			} else {
				return -1;
			}
		}
		
		public List<JvmType.Variable> usedVariables() {
			ArrayList<JvmType.Variable> ls = new ArrayList();
			if(lowerBound != null) {
				ls.addAll(lowerBound.usedVariables());
			}
			if(upperBound != null) {
				ls.addAll(upperBound.usedVariables());
			}
			return ls;
		}
	}
	
	/**
     * Represents a Generic type variable. For example, the T in class ArrayList<T> {
     * ... }
     * 
     * @author David J. Pearce
     * 
     */
	public static class Variable implements Reference {
		private final String variable;
		private final JvmType.Reference lowerBound;

		public Variable(String variable, JvmType.Reference lowerBound) {
			if(lowerBound == null) {
				throw new IllegalArgumentException("Type.Variable lowerBound cannot be null");
			}
			this.variable = variable;
			this.lowerBound = lowerBound;
		}

		public String variable() {
			return variable;
		}

		public JvmType.Reference lowerBound() {
			return lowerBound;
		}		
		
		public boolean equals(Object o) {			
			if (o instanceof Variable) {
				Variable v = (Variable) o;
				return variable.equals(v.variable)						
						&& lowerBound.equals(v.lowerBound);
			}
			return false;
		}
		
		public String toString() {
			if(lowerBound == null) {
				return variable;
			} else {					
				return variable + " extends " + lowerBound;				
			}
		}
		
		public int hashCode() {
			return variable.hashCode();
		}
		
		public int compareTo(JvmType t) {
			if (t instanceof JvmType.Variable) {
				JvmType.Variable tv = (JvmType.Variable) t;
				int vct = variable.compareTo(tv.variable);
				if(vct != 0) { return vct; }
				if(lowerBound == null && tv.lowerBound != null) {
					return -1;
				} else if(lowerBound != null && tv.lowerBound == null) {
					return 1;
				} else if (lowerBound != null) {
					return lowerBound.compareTo(tv.lowerBound);
				}
				
				return 0;
			} else if (t instanceof JvmType.Primitive || t instanceof JvmType.Array
					|| t instanceof JvmType.Clazz || t instanceof JvmType.Wildcard) {
				return 1;
			} else {
				return -1;
			}
		}
		
		public List<JvmType.Variable> usedVariables() {
			ArrayList<JvmType.Variable> ls = new ArrayList();
			ls.add(this);
			if(lowerBound != null) {
				ls.addAll(lowerBound.usedVariables());
			}			
			return ls;
		}
	}
	
	/**
	 * An intersection type represents a (unknown) type which known to be a
	 * subtype of several types. For example, given types T1 and T2, then their
	 * intersection type is T1 & T2. The intersection type represents an object
	 * which is *both* an instance of T1 and an instance of T2. Thus, we always
	 * have that T1 :> T1 & T2 and T2 :> T1 & T2.
	 * 
	 * @author David J. Pearce
	 */
	public static class Intersection implements Reference {
		private final ArrayList<JvmType.Reference> bounds;
		
		public Intersection(List<JvmType.Reference> bounds) {			
			if (bounds == null) {
				throw new IllegalArgumentException(
						"Supplied bounds cannot be null.");
			} else if(bounds.size() <= 1) {
				throw new IllegalArgumentException(
				"Require more than one bound.");
			}
			this.bounds = new ArrayList<JvmType.Reference>(bounds);
		}
		
		public List<JvmType.Reference> bounds() {
			return bounds;
		}
		
		public String toString() {
			String r = "";
			if(bounds.size() > 1) { r += "("; }
			boolean firstTime = true;
			for(JvmType.Reference b : bounds) {
				if(!firstTime) { r += " & "; }
				firstTime = false;
				r += b.toString();
			}
			if(bounds.size() > 1) { r += ")"; }
			return r;
		}
		
		public boolean equals(Object o) {
			if(o instanceof Intersection) {
				Intersection t = (Intersection) o;
				if(t.bounds.size() == bounds.size()) {
					for(int i=0;i!=bounds.size();++i) {
						if(!t.bounds.get(i).equals(bounds.get(i))) {
							return false;
						}
					}
					return true;
				}
			}
			return false;
		}
		
		public int hashCode() {
			int hc = 0;
			for(JvmType.Reference r : bounds) {
				hc ^= r.hashCode();
			}
			return hc;
		}
		
		public int compareTo(JvmType t) {
			if (t instanceof JvmType.Intersection) {
				JvmType.Intersection tv = (JvmType.Intersection) t;
				if(bounds.size() < tv.bounds.size()) { 
					return -1;
				} else if(bounds.size() > tv.bounds.size()) {
					return 1;
				} 
				for(int i=0;i!=bounds.size();++i) {
					JvmType.Reference r1 = bounds.get(i);
					JvmType.Reference r2 = tv.bounds.get(i);
					int rct = r1.compareTo(r2);
					if(rct != 0) { return rct; }
				}
				return 0;
			} else if (t instanceof JvmType.Reference) {
				return 1;
			} else {
				return -1;
			}
		}
		
		public List<JvmType.Variable> usedVariables() {
			ArrayList<JvmType.Variable> ls = new ArrayList();
			for(JvmType.Reference r : bounds) {
				ls.addAll(r.usedVariables());
			}
			return ls;
		}
	}
	
	/**
	 * Represents the type of a method.  For example, the following method
	 * 
	 * void m(int x) { ... } has type "void(int)" 
	 * @author David J. Pearce
	 *
	 */
	public static class Function implements JvmType {
		private final List<JvmType> parameters;
		private final JvmType returnType;
		private final List<JvmType.Variable> typeArgs;
		
		public Function(JvmType returnType, JvmType... parameters) {
			if (returnType == null) {
				throw new IllegalArgumentException(
						"Supplied return type cannot be null.");
			}
			this.returnType = returnType;
			this.parameters = new ArrayList<JvmType>();
			this.typeArgs = new ArrayList();
			for(JvmType t : parameters) {
				this.parameters.add(t);
			}
		}
		
		public Function(JvmType returnType, List<JvmType> parameters) {
			if (returnType == null) {
				throw new IllegalArgumentException(
						"Supplied return type cannot be null.");
			}
			this.returnType = returnType;
			this.parameters = parameters;
			this.typeArgs = new ArrayList();
		}
		
		public Function(JvmType returnType, List<JvmType> parameters, List<JvmType.Variable> typeArgs) {
			if (returnType == null) {
				throw new IllegalArgumentException(
						"Supplied return type cannot be null.");
			}
			this.returnType = returnType;
			this.parameters = parameters;
			this.typeArgs = typeArgs;
		}
		
		public JvmType returnType() { 
			return returnType;
		}
		
		public List<JvmType> parameterTypes() {
			return parameters;
		}
		
		public List<JvmType.Variable> typeArguments() {
			return typeArgs;
		}
		
		public boolean equals(Object o) {
			if (o instanceof Function) {
				Function f = (Function) o;
				return returnType.equals(f.returnType)
						&& parameters.equals(f.parameters)
						&& typeArgs.equals(f.typeArgs);
			}
			return false;
		}
		
		public String toString() {
			String r="";
			boolean firstTime;
			if(typeArgs.size() > 0) {
				r += "<";
				firstTime=true;
				for(JvmType.Variable v : typeArgs) {
					if(!firstTime) {
						r += ", ";						
					}
					firstTime=false;
					r += v;
				}
				r += "> ";
			}
			r += returnType;
			r += " ("; 
			
			firstTime=true;
			for(JvmType t : parameters) {
				if(!firstTime) {
					r += ", ";						
				}
				firstTime=false;
				r += t;
			}
			r+= ")";
			return r;
		}
		
		public int hashCode() {
			int hc = 0;
			for(JvmType t : parameters) {
				hc ^= t.hashCode();
			}
			return hc;
		}
		
		public int compareTo(JvmType t) {
			if (t instanceof JvmType.Function) {
				JvmType.Function tf = (JvmType.Function) t;
				if(parameters.size() < tf.parameters.size()) {
					return -1; 
				} else if(parameters.size() > tf.parameters.size()) {
					return 1; 
				} 
				for(int i=0;i!=parameters.size();++i) {
					JvmType p1 = parameters.get(i);
					JvmType p2 = tf.parameters.get(i);
					int pct = p1.compareTo(p2);
					if(pct != 0) { return pct; }
				}
				return returnType.compareTo(tf.returnType);
			} else if (t instanceof JvmType.Reference) {
				return 1;
			} else {
				return -1;
			}
		}
		
		public List<JvmType.Variable> usedVariables() {
			ArrayList<JvmType.Variable> ls = new ArrayList();
			ls.addAll(returnType.usedVariables());
			for(JvmType r : parameters) {
				ls.addAll(r.usedVariables());
			}
			ls.addAll(typeArgs);
			return ls;
		}
	}	
}
