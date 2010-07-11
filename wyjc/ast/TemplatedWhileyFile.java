// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyjc.ast;

import java.util.*;

import wyjc.ast.attrs.Attribute;
import wyjc.ast.attrs.SyntacticElement;
import wyjc.ast.attrs.SyntacticElementImpl;
import wyjc.ast.exprs.*;
import wyjc.ast.stmts.Stmt;
import wyjc.ast.types.*;
import wyjc.ast.types.unresolved.UnresolvedType;

public class TemplatedWhileyFile {
	
	public interface Decl<T extends UnresolvedType> extends SyntacticElement {
		public String name();
	}
	
	public static class ConstDecl<T extends UnresolvedType, E extends Expr>
			extends
				SyntacticElementImpl implements Decl<T> {
		protected List<Modifier> modifiers;
		protected E value;
		protected final String name;

		public ConstDecl(List<Modifier> modifiers, E value, String name, Attribute... attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.value = value;
			this.name = name;
		}

		public ConstDecl(List<Modifier> modifiers, E value, String name, Collection<Attribute> attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.value = value;
			this.name = name;
		}

		public List<Modifier> modifiers() {
			return modifiers;
		}
		
		public boolean isPublic() {
			for(Modifier m : modifiers) {
				if(m instanceof Modifier.Public) {
					return true;
				}
			}
			return false;
		}
		
		public E constant() {
			return value;
		}

		public String name() {
			return name;
		}

		public String toString() {
			return "define " + value + " as " + name;
		}
	}
	
	public static class TypeDecl<T extends UnresolvedType> extends SyntacticElementImpl implements Decl<T> {
		protected List<Modifier> modifiers;
		protected T type;		
		protected String name;

		public TypeDecl(List<Modifier> modifiers, T type, String name, Attribute... attributes) { 
			super(attributes);
			this.modifiers = modifiers;
			this.type = type;			
			this.name = name;
		}

		public TypeDecl(List<Modifier> modifiers, T type, String name, Collection<Attribute> attributes) { 
			super(attributes);		
			this.modifiers = modifiers;
			this.type = type;			
			this.name = name;
		}
			
		public List<Modifier> modifiers() {
			return modifiers;
		}
		
		public boolean isPublic() {
			for(Modifier m : modifiers) {
				if(m instanceof Modifier.Public) {
					return true;
				}
			}
			return false;
		}
		
		public T type() { return type; }
		
		public String name() { return name; }		
		
		public String toString() {
			return "define " + type + " as " + name;
		}
	}

	public static class FunDecl<T extends UnresolvedType, S extends Receiver<T>, P extends Parameter<T>, R extends Return<T>>
	extends
	SyntacticElementImpl implements Decl<T> {
		protected List<Modifier> modifiers; 
		protected String name;
		protected S receiverType;
		protected R returnType;
		protected List<P> parameters;
		protected Condition precondition;
		protected Condition postcondition;
		protected List<Stmt> statements;

		/**
		 * Construct an object representing a Whiley function.
		 * 
		 * @param name -
		 *            The name of the function.
		 * @param returnType -
		 *            The return type of this method
		 * @param paramTypes -
		 *            The list of parameter names and their types for this method
		 * @param pre -
		 *            The pre condition which must hold true on entry (maybe null)
		 * @param post -
		 *            The post condition which must hold true on exit (maybe null)
		 * @param statements -
		 *            The Statements making up the function body.
		 */
		public FunDecl(List<Modifier> modifiers, String name, S receiverType, R returnType,
				List<P> parameters, Condition pre, Condition post,
				List<Stmt> statements, Collection<Attribute> attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.name = name;		
			this.receiverType = receiverType;
			this.returnType = returnType;
			this.parameters = parameters;
			this.precondition = pre;
			this.postcondition = post;
			this.statements = statements;
		}

		/**
		 * Construct an object representing a Whiley function.
		 * 
		 * @param name -
		 *            The name of the function.
		 * @param returnType -
		 *            The return type of this method
		 * @param paramTypes -
		 *            The list of parameter names and their types for this method
		 * @param pre -
		 *            The pre condition which must hold true on entry (maybe null)
		 * @param post -
		 *            The post condition which must hold true on exit (maybe null)
		 * @param statements -
		 *            The Statements making up the function body.
		 */
		public FunDecl(List<Modifier> modifiers, String name, S receiverType, R returnType,
				List<P> parameters, Condition pre, Condition post,
				List<Stmt> statements, Attribute... attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.name = name;		
			this.receiverType = receiverType;
			this.returnType = returnType;
			this.parameters = parameters;
			this.precondition = pre;
			this.postcondition = post;
			this.statements = statements;
		}
		
		public List<Modifier> modifiers() {
			return modifiers;
		}
		
		public boolean isPublic() {
			for(Modifier m : modifiers) {
				if(m instanceof Modifier.Public) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * Return the name of this function.
		 * 
		 * @return
		 */
		public String name() {
			return name;
		}

		public S receiver() {
			return receiverType;
		}
		
		public boolean isFunction() {
			return receiverType == null;
		}

		/**
		 * Return the precondition of this function.
		 * 
		 * @return
		 */
		public Condition preCondition() {
			return precondition;
		}

		/**
		 * Return the postcondition of this function.
		 * 
		 * @return
		 */
		public Condition postCondition() {
			return postcondition;
		}

		/**
		 * Return the return Type of this function.
		 * 
		 * @return
		 */	
		public R returnType() {
			return returnType;
		}

		/**
		 * Return the parameter types for this function.
		 * 
		 * @return
		 */
		public List<P> parameters() {
			return parameters;
		}

		public List<String> parameterNames() {
			ArrayList<String> names = new ArrayList<String>();
			for (P p : parameters) {
				names.add(p.name());
			}
			return names;
		}

		/**
		 * Return the list of statements which make up the body of this function.
		 */
		public List<Stmt> statements() {
			return statements;
		}		
	}
	
	public static class Return<T extends UnresolvedType> extends SyntacticElementImpl {
		private final T type;

		public Return(T type, Attribute... attributes) {
			super(attributes);
			this.type = type;			
		}

		public Return(T type, Collection<Attribute> attributes) {
			super(attributes);
			this.type = type;			
		}

		public T type() {
			return type;
		}

		public String toString() {
			return type.toString();
		}
	}
	
	public static class Receiver<T extends UnresolvedType> extends SyntacticElementImpl {
		private final T type;

		public Receiver(T type, Attribute... attributes) {
			super(attributes);
			this.type = type;			
		}

		public Receiver(T type, Collection<Attribute> attributes) {
			super(attributes);
			this.type = type;			
		}

		public T type() {
			return type;
		}

		public String toString() {
			return type.toString();
		}
	}

	public static class Parameter<T extends UnresolvedType> extends SyntacticElementImpl {
		private final T type;
		private final String name;

		public Parameter(T type, String name, Attribute... attributes) {
			super(attributes);
			this.type = type;
			this.name = name;
		}

		public Parameter(T type, String name, Collection<Attribute> attributes) {
			super(attributes);
			this.type = type;
			this.name = name;
		}

		public T type() {
			return type;
		}

		public String name() {
			return name;
		}

		public String toString() {
			return type + " " + name;
		}
	}
}
