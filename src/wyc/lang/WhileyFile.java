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

package wyc.lang;

import java.io.*;
import java.util.*;

import wybs.lang.Content;
import wybs.lang.Path;
import wybs.lang.SyntacticElement;
import wybs.lang.SyntaxError;
import wybs.util.Trie;
import wyc.builder.Nominal;
import wyc.stages.WhileyFilter;
import wyc.stages.WhileyLexer;
import wyc.stages.WhileyParser;
import wyil.lang.*;

/**
 * Provides classes representing the various kinds of declaration found in a
 * Whiley source file. This includes <i>type declarations</i>, <i>method
 * declarations</i>, <i>constant declarations</i>, etc. In essence, a
 * <code>WhileyFile</code> forms the root of the Abstract Syntax Tree.
 * 
 * @author David J. Pearce
 * 
 */
public final class WhileyFile {
	
	// =========================================================================
	// Content Type
	// =========================================================================
	
	public static final Content.Type<WhileyFile> ContentType = new Content.Type<WhileyFile>() {
		public Path.Entry<WhileyFile> accept(Path.Entry<?> e) {			
			if (e.contentType() == this) {
				return (Path.Entry<WhileyFile>) e;
			} 			
			return null;
		}
		
		/**
		 * This method simply parses a whiley file into an abstract syntax tree. It
		 * makes little effort to check whether or not the file is syntactically
		 * correct. In particular, it does not determine the correct type of all
		 * declarations, expressions, etc.
		 * 
		 * @param file
		 * @return
		 * @throws IOException
		 */		
		public WhileyFile read(Path.Entry<WhileyFile> e, InputStream inputstream) throws IOException {		
			Runtime runtime = Runtime.getRuntime();
			long start = System.currentTimeMillis();
			long memory = runtime.freeMemory();

			WhileyLexer wlexer = new WhileyLexer(inputstream);

			List<WhileyLexer.Token> tokens = new WhileyFilter().filter(wlexer
					.scan());

			WhileyParser wfr = new WhileyParser(e.location().toString(),
					tokens);
//			project.logTimedMessage("[" + e.location() + "] Parsing complete",
//					System.currentTimeMillis() - start,
//					memory - runtime.freeMemory());

			WhileyFile wf = wfr.read();
			return wf;				
		}
		
		public void write(OutputStream output, WhileyFile value) {
			// for now
			throw new UnsupportedOperationException();
		}
		
		public String toString() {
			return "Content-Type: whiley";
		}
	};
	
	// =========================================================================
	// State
	// =========================================================================
	
	public final Path.ID module;
	public final String filename;
	public final ArrayList<Declaration> declarations;

	// =========================================================================
	// Constructors
	// =========================================================================
	
	public WhileyFile(Path.ID module, String filename) {
		this.module = module;
		this.filename = filename;
		this.declarations = new ArrayList<Declaration>();
	}

	// =========================================================================
	// Accessors
	// =========================================================================
	
	public boolean hasName(String name) {
		return declaration(name) != null;
	}			

	public Declaration declaration(String name) {
		for(Declaration d : declarations) {
			if(d.name().equals(name)) {
				return d;
			}
		}
		return null;
	}
	
	public <T> List<T> declarations(Class<T> c) {
		ArrayList<T> r = new ArrayList<T>();
		for(Declaration d : declarations) {
			if(c.isInstance(d)) {
				r.add((T)d);
			}			
		}
		return r;
	}
	
	public <T> List<T> declarations(Class<T> c, String name) {
		ArrayList<T> r = new ArrayList<T>();
		for(Declaration d : declarations) {
			if (d.name().equals(name) && c.isInstance(d)) {
				r.add((T) d);
			}		
		}
		return r;
	}	
	
	public TypeDef typeDecl(String name) {
		for (Declaration d : declarations) {
			if (d instanceof TypeDef && d.name().equals(name)) {
				return (TypeDef) d;
			}
		}
		return null;
	}
		
	// =========================================================================
	// Mutators
	// =========================================================================
	
	public void add(Declaration declaration) {
		declarations.add(declaration);
	}
	
	// =========================================================================
	// Types
	// =========================================================================		
	
	public interface Declaration extends SyntacticElement {
		public String name();
	}

	public interface Context extends SyntacticElement {
		public WhileyFile file();
		public List<Import> imports();
	}
	
	private abstract class AbstractContext extends SyntacticElement.Impl implements Context {

		private AbstractContext(Attribute... attributes) {
			super(attributes);
		}
		
		private AbstractContext(Collection<Attribute> attributes) {
			super(attributes);
		}
		
		public WhileyFile file() {
			return WhileyFile.this;
		}
		
		/**
		 * Construct an appropriate list of import statements for a declaration in a
		 * given file. Thus, only import statements up to and including the given
		 * declaration will be included in the returned list.
		 * 
		 * @param wf
		 *            --- Whiley File in question to obtain list of import
		 *            statements.
		 * @param decl
		 *            --- declaration in Whiley File for which the list is desired.
		 * @return
		 */
		public List<Import> imports() {
			// this computation could (should?) be cached.
			ArrayList<Import> imports = new ArrayList<Import>();		
			imports.add(new WhileyFile.Import(Trie.fromString("whiley/lang/*"), null)); 	
			imports.add(new WhileyFile.Import(Trie.fromString(module.parent(), "*"), null)); 
			
			for(Declaration d : declarations) {
				if(d == this) {
					break;
				} else if(d instanceof Import) {
					imports.add((Import)d);
				}
			}			
			imports.add(new WhileyFile.Import(Trie.fromString(module), "*")); 

			Collections.reverse(imports);	
			
			return imports;
		}			
	}
	
	/**
	 * Represents an import declaration in a Whiley source file. For example:
	 * 
	 * <pre>
	 * import Console from whiley.lang.System
	 * </pre>
	 * 
	 * Here, the package is <code>whiley.lang</code>, the module is
	 * <code>System</code> and the name is <code>Console</code>.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Import extends SyntacticElement.Impl implements Declaration {		
		public final Trie filter;
		public final String name;
		
		public Import(Trie filter, String name, Attribute... attributes) {
			super(attributes);
			this.filter = filter;			
			this.name = name;
		}
	
		/*
		public boolean matchName(String name) {
			
			if(this.name != null) {
				return (this.name.equals(name) || this.name.equals("*"));	
			} else {
				String last = filter.last();
				return last.equals(name) || last.equals("*")  || last.equals("*");
			}					
		}
		*/
//		public boolean matchModule(String module) {
//			return this.module != null && (this.module.equals(module) || this.module.equals("*"));
//		}
//		
		public String name() {
			return "";
		}		
	}

	/**
	 * Represents a constant declaration in a Whiley source file. For example:
	 * 
	 * <pre>
	 * define PI as 3.14159
	 * </pre>
	 * 
	 * Constant declarations may also have modifiers, such as
	 * <code>public</code> and <code>private</code>.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public class Constant extends
				AbstractContext implements Declaration {
		
		public final List<Modifier> modifiers;
		public final String name;
		public Expr constant;
		public Value resolvedValue;		

		public Constant(List<Modifier> modifiers, Expr constant, String name,
				Attribute... attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.constant = constant;
			this.name = name;
		}

		public String name() {
			return name;
		}
		
		public boolean isPublic() {
			for(Modifier m : modifiers) {
				if(m instanceof Modifier.Public) {
					return true;
				}
			}
			return false;
		}
		
		public String toString() {
			return "define " + constant + " as " + name;
		}
	}

	/**
	 * Represents a type declaration in a Whiley source file. For example:
	 * 
	 * <pre>
	 * define nat as int where $ >= 0
	 * </pre>
	 * 
	 * Here, the newly defined type is <code>nat</code> whose values are all
	 * non-negative integers. Type declarations may also have modifiers, such as
	 * <code>public</code> and <code>private</code>.
	 * 
	 * @author David J. Pearce
	 * 
	 */	
	public class TypeDef extends AbstractContext implements Declaration {
		public final List<Modifier> modifiers;
		public final UnresolvedType unresolvedType;
		public Nominal resolvedType;
		public Expr constraint;
		public final String name;		

		public TypeDef(List<Modifier> modifiers, UnresolvedType type, String name,
				Expr constraint, Attribute... attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.unresolvedType = type;
			this.name = name;			
			this.constraint = constraint;			
		}		
		
		public boolean isPublic() {
			for(Modifier m : modifiers) {
				if(m instanceof Modifier.Public) {
					return true;
				}
			}
			return false;
		}
		
		public String name() { return name; }		
		
		public String toString() {
			if(constraint != null) {
				return "define " + unresolvedType + " as " + name + " where " + constraint;
			} else {
				return "define " + unresolvedType + " as " + name;
			}
		}
	}
	
	public abstract class FunctionOrMethodOrMessage extends AbstractContext implements
			Declaration {
		public final ArrayList<Modifier> modifiers;
		public final String name;		
		public final UnresolvedType ret;
		public final UnresolvedType throwType;
		public final ArrayList<Parameter> parameters;
		public final ArrayList<Stmt> statements;			
		public Expr precondition;
		public Expr postcondition;		
		
		/**
		 * Construct an object representing a Whiley function.
		 * 
		 * @param name
		 *            - The name of the function.
		 * @param returnType
		 *            - The return type of this method
		 * @param paramTypes
		 *            - The list of parameter names and their types for this
		 *            method
		 * @param precondition
		 *            - The constraint which must hold true on entry and exit
		 *            (maybe null)
		 * @param statements
		 *            - The Statements making up the function body.
		 */
		public FunctionOrMethodOrMessage(List<Modifier> modifiers, String name,
				UnresolvedType ret, List<Parameter> parameters,
				Expr precondition, Expr postcondition,
				UnresolvedType throwType, List<Stmt> statements,
				Attribute... attributes) {
			super(attributes);
			this.modifiers = new ArrayList<Modifier>(modifiers);
			this.name = name;			
			this.ret = ret;
			this.parameters = new ArrayList<Parameter>(parameters);
			this.precondition = precondition;
			this.postcondition = postcondition;
			this.statements = new ArrayList<Stmt>(statements);
			this.throwType = throwType;
		}

		public boolean isPublic() {
			for (Modifier m : modifiers) {
				if (m instanceof Modifier.Public) {
					return true;
				}
			}
			return false;
		}

		public String name() {
			return name;
		}		
		
		public abstract UnresolvedType.FunctionOrMethodOrMessage unresolvedType();
		
		public abstract Nominal.FunctionOrMethodOrMessage resolvedType();
	}

	public abstract class FunctionOrMethod extends FunctionOrMethodOrMessage {
		public FunctionOrMethod(List<Modifier> modifiers, String name,
				UnresolvedType ret, List<Parameter> parameters,
				Expr precondition, Expr postcondition,
				UnresolvedType throwType, List<Stmt> statements,
				Attribute... attributes) {
			super(modifiers, name, ret, parameters, precondition,
					postcondition, throwType, statements, attributes);
		}		
		
		public abstract UnresolvedType.FunctionOrMethod unresolvedType();
		
		public abstract Nominal.FunctionOrMethod resolvedType();
	}
	
	/**
	 * Represents a function declaration in a Whiley source file. For example:
	 * 
	 * <pre>
	 * int f(int x) requires x > 0, ensures $ < 0:
	 *    return -x
	 * </pre>
	 * 
	 * <p>
	 * Here, a function <code>f</code> is defined which accepts only positive
	 * integers and returns only negative integers. The special variable
	 * <code>$</code> is used to refer to the return value. Functions in Whiley
	 * may not have side-effects (i.e. they are <code>pure functions</code>).
	 * </p>
	 * 
	 * <p>
	 * Function declarations may also have modifiers, such as
	 * <code>public</code> and <code>private</code>.
	 * </p>
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public final class Function extends FunctionOrMethod {
		public Nominal.Function resolvedType;
		
		public Function(List<Modifier> modifiers, String name,
				UnresolvedType ret, List<Parameter> parameters,
				Expr precondition, Expr postcondition,
				UnresolvedType throwType, List<Stmt> statements,
				Attribute... attributes) {
			super(modifiers, name, ret, parameters, precondition,
					postcondition, throwType, statements, attributes);
		}
		
		public UnresolvedType.Function unresolvedType() {
			ArrayList<UnresolvedType> params = new ArrayList<UnresolvedType>();
			for (Parameter p : parameters) {
				params.add(p.type);
			}
			return new UnresolvedType.Function(ret, throwType, params, attributes());
		}
		
		public Nominal.Function resolvedType() {
			return resolvedType;
		}
	}
	
	/**
	 * Represents a method declaration in a Whiley source file. For example:
	 * 
	 * <pre>
	 * int ::m(int x) requires x > 0, ensures $ < 0:
	 *    return -x
	 * </pre>
	 * 
	 * <p>
	 * Here, a method <code>m</code> is defined which accepts only positive
	 * integers and returns only negative integers. The special variable
	 * <code>$</code> is used to refer to the return value. Unlike functions,
	 * methods in Whiley may have side-effects.
	 * </p>
	 * 
	 * <p>
	 * Method declarations may also have modifiers, such as
	 * <code>public</code> and <code>private</code>.
	 * </p>
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public final class Method extends FunctionOrMethod {
		public Nominal.Method resolvedType;
		
		public Method(List<Modifier> modifiers, String name,
				UnresolvedType ret, List<Parameter> parameters,
				Expr precondition, Expr postcondition,
				UnresolvedType throwType, List<Stmt> statements,
				Attribute... attributes) {
			super(modifiers, name, ret, parameters, precondition,
					postcondition, throwType, statements, attributes);
		}
		
		public UnresolvedType.Method unresolvedType() {
			ArrayList<UnresolvedType> params = new ArrayList<UnresolvedType>();
			for (Parameter p : parameters) {
				params.add(p.type);
			}
			return new UnresolvedType.Method(ret, throwType, params, attributes());
		}
		
		public Nominal.Method resolvedType() {
			return resolvedType;
		}
	}
	
	/**
	 * Represents a message declaration in a Whiley source file. For example:
	 * 
	 * <pre>
	 * int MyData::m(int x):
	 *    return this.x + x
	 * </pre>
	 * 
	 * <p>
	 * Here, a message <code>m</code> is defined for a given object type
	 * <code>MyData</code>, which is referred to as the <i>receiver</i>. The
	 * special variable <code>this</code> is used to access fields within the
	 * object type. Like methods, messages in Whiley as they may have
	 * side-effects. This includes reading/writing I/O and modifying the state
	 * of their receiver. Methods may also be <i>headless</i>, meaning they are
	 * not attached to any specific receiver.
	 * </p>
	 * 
	 * <p>
	 * Method declarations may also have modifiers, such as <code>public</code>
	 * and <code>private</code>.
	 * </p>
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public final class Message extends FunctionOrMethodOrMessage {
		public final UnresolvedType receiver;
		public Nominal.Message resolvedType;
		
		public Message(List<Modifier> modifiers, String name,
				UnresolvedType receiver, UnresolvedType ret,
				List<Parameter> parameters, Expr precondition,
				Expr postcondition, UnresolvedType throwType, 
				List<Stmt> statements,
				Attribute... attributes) {
			super(modifiers,name,ret,parameters,precondition,postcondition,throwType,statements,attributes);
			this.receiver = receiver;
		}
		
		public UnresolvedType.Message unresolvedType() {
			ArrayList<UnresolvedType> params = new ArrayList<UnresolvedType>();
			for (Parameter p : parameters) {
				params.add(p.type);
			}
			return new UnresolvedType.Message(receiver, ret, throwType, params, attributes());
		}
		
		public Nominal.Message resolvedType() {
			return resolvedType;
		}
	}

	/**
	 * Represents a parameter declaration as part of a function or method
	 * declaration. The primary purpose of this is to retain the source-code
	 * location of the parameter in case any syntax error needs to be reported
	 * on it.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public final class Parameter extends AbstractContext implements Declaration {
		public final UnresolvedType type;
		public final String name;

		public Parameter(UnresolvedType type, String name, Attribute... attributes) {
			super(attributes);
			this.type = type;
			this.name = name;
		}
		
		public String name() {
			return name;
		}
	}
	

	public static void syntaxError(String msg, Context context, SyntacticElement elem) {
		SyntaxError.syntaxError(msg,context.file().filename,elem);
	}
	
	public static void syntaxError(String msg, Context context, SyntacticElement elem, Throwable ex) {
		SyntaxError.syntaxError(msg,context.file().filename,elem,ex);
	}
	
	public static void internalFailure(String msg, Context context, SyntacticElement elem) {
		SyntaxError.internalFailure(msg,context.file().filename,elem);
	}
	
	public static void internalFailure(String msg, Context context, SyntacticElement elem, Throwable ex) {
		SyntaxError.internalFailure(msg,context.file().filename,elem,ex);
	}
}
