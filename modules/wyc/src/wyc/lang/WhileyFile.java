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

import wyc.io.WhileyFileLexer;
import wyc.io.WhileyFileParser;
import wycc.lang.Attribute;
import wycc.lang.CompilationUnit;
import wycc.lang.SyntacticElement;
import wycc.lang.SyntaxError;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.Trie;
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
public final class WhileyFile implements CompilationUnit {

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
		 * This method simply parses a whiley file into an abstract syntax tree.
		 * It makes little effort to check whether or not the file is
		 * syntactically correct. In particular, it does not determine the
		 * correct type of all declarations, expressions, etc.
		 *
		 * @param file
		 * @return
		 * @throws IOException
		 */
		public WhileyFile read(Path.Entry<WhileyFile> e, InputStream inputstream)
				throws IOException {
			Runtime runtime = Runtime.getRuntime();
			long start = System.currentTimeMillis();
			long memory = runtime.freeMemory();

			WhileyFileLexer wlexer = new WhileyFileLexer(e.location()
					.toString(), inputstream);
			WhileyFileParser wfr = new WhileyFileParser(e.location()
					.toString(), wlexer.scan());
			return wfr.read();
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

	public NamedDeclaration declaration(String name) {
		for (Declaration d : declarations) {
			if (d instanceof NamedDeclaration) {
				NamedDeclaration nd = (NamedDeclaration) d;
				if(nd.name().equals(name)) {
					return (NamedDeclaration) nd;
				}
			}
		}
		return null;
	}

	public <T> List<T> declarations(Class<T> c) {
		ArrayList<T> r = new ArrayList<T>();
		for (Declaration d : declarations) {
			if (c.isInstance(d)) {
				r.add((T) d);
			}
		}
		return r;
	}

	public <T> List<T> declarations(Class<T> c, String name) {
		ArrayList<T> r = new ArrayList<T>();
		for (Declaration d : declarations) {
			if (d instanceof NamedDeclaration
					&& ((NamedDeclaration) d).name().equals(name)
					&& c.isInstance(d)) {
				r.add((T) d);
			}
		}
		return r;
	}

	public Type typeDecl(String name) {
		for (Declaration d : declarations) {
			if (d instanceof Type && ((NamedDeclaration)d).name().equals(name)) {
				return (Type) d;
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

	}

	public abstract class NamedDeclaration extends AbstractContext implements
			Declaration {
		private final ArrayList<Modifier> modifiers;
		private final String name;

		public NamedDeclaration(String name, Collection<Modifier> modifiers,Attribute... attributes) {
			super(attributes);
			this.modifiers = new ArrayList<Modifier>(modifiers);
			this.name = name;
		}

		public String name() {
			return name;
		}

		public List<Modifier> modifiers() {
			return modifiers;
		}

		public boolean hasModifier(Modifier modifier) {
			return modifiers.contains(modifier);
		}
	}

	public interface Context extends SyntacticElement {
		public WhileyFile file();

		public List<Import> imports();
	}

	private abstract class AbstractContext extends SyntacticElement.Impl
			implements Context {

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
		 * Construct an appropriate list of import statements for a declaration
		 * in a given file. Thus, only import statements up to and including the
		 * given declaration will be included in the returned list.
		 *
		 * @param wf
		 *            --- Whiley File in question to obtain list of import
		 *            statements.
		 * @param decl
		 *            --- declaration in Whiley File for which the list is
		 *            desired.
		 * @return
		 */
		public List<Import> imports() {
			// this computation could (should?) be cached.
			ArrayList<Import> imports = new ArrayList<Import>();
			imports.add(new WhileyFile.Import(Trie.fromString(module.parent(),
					"*"), null));

			for (Declaration d : declarations) {
				if (d == this) {
					break;
				} else if (d instanceof Import) {
					imports.add((Import) d);
				}
			}
			imports.add(new WhileyFile.Import(Trie.fromString(module), "*"));

			Collections.reverse(imports);

			return imports;
		}
	}

	/**
	 * Represents an import declaration in a Whiley source file, which has the
	 * form:
	 *
	 * <pre>
	 * ImportDeclaration ::= "import" [Identifier|Star "from"] Identifier ('.' Identifier|'*')*
	 * </pre>
	 *
	 * The following illustrates a simple import statement:
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
	public static class Import extends SyntacticElement.Impl implements
			Declaration {
		public final Trie filter;
		public final String name;

		public Import(Trie filter, String name, Attribute... attributes) {
			super(attributes);
			this.filter = filter;
			this.name = name;
		}
	}

	/**
	 * Represents a constant declaration in a Whiley source file, which has the
	 * form:
	 *
	 * <pre>
	 * ConstantDeclaration ::= "constant" Identifier "is" Expression
	 * </pre>
	 *
	 * A simple example to illustrate is:
	 *
	 * <pre>
	 * constant PI is 3.141592654
	 * </pre>
	 *
	 * Here, we are defining a constant called <code>PI</code> which represents
	 * the decimal value "3.141592654". Constant declarations may also have
	 * modifiers, such as <code>public</code> and <code>private</code>.
	 *
	 * @author David J. Pearce
	 *
	 */
	public class Constant extends NamedDeclaration {
		public Expr constant;
		public wyil.lang.Constant resolvedValue;

		public Constant(List<Modifier> modifiers, Expr constant, String name,
				Attribute... attributes) {
			super(name, modifiers, attributes);
			this.constant = constant;
		}
	}

	/**
	 * Represents a type declaration in a Whiley source file, which has the
	 * form:
	 *
	 * <pre>
	 * "type" Identifier "is" TypePattern ["where" Expression]
	 * </pre>
	 *
	 * Here, the type pattern specifies a type which may additionally be adorned
	 * with variable names. The "where" clause is optional and is often referred
	 * to as the type's "constraint". Variables defined within the type pattern
	 * may be used within this constraint expressions. A simple example to
	 * illustrate is:
	 *
	 * <pre>
	 * type nat is (int x) where x >= 0
	 * </pre>
	 *
	 * Here, we are defining a <i>constrained type</i> called <code>nat</code>
	 * which represents the set of natural numbers (i.e the non-negative
	 * integers). Type declarations may also have modifiers, such as
	 * <code>public</code> and <code>private</code>.
	 *
	 * @author David J. Pearce
	 *
	 */
	public class Type extends NamedDeclaration {
		public final Parameter parameter;
		public Nominal resolvedType;
		public ArrayList<Expr> invariant;

		public Type(List<Modifier> modifiers, Parameter type,
				String name, List<Expr> constraint, Attribute... attributes) {
			super(name, modifiers,attributes);
			this.parameter = type;
			this.invariant = new ArrayList<Expr>(constraint);
		}
	}

	/**
	 * Represents a <i>function declaration</i> or <i>method declaration</i> in
	 * a Whiley source file which have the form:
	 *
	 * <pre>
	 * FunctionDeclaration ::= "function" TypePattern "=>" TypePattern (FunctionMethodClause)* ':' NewLine Block
	 *
	 * MethodDeclaration ::= "method" TypePattern "=>" TypePattern (FunctionMethodClause)* ':' NewLine Block
	 *
	 * FunctionMethodClause ::= "throws" Type | "requires" Expression | "ensures" Expression
	 * </pre>
	 *
	 * Here, the first type pattern (i.e. before "=>") is referred to as the
	 * "parameter", whilst the second is referred to as the "return". There are
	 * three kinds of option clause:
	 *
	 * <ul>
	 * <li><b>Throws clause</b>. This defines the exceptions which may be thrown
	 * by this function. Multiple clauses may be given, and these are taken
	 * together as a union. Furthermore, the convention is to specify the throws
	 * clause before the others.</li>
	 * <li><b>Requires clause</b>. This defines a constraint on the permissible
	 * values of the parameters on entry to the function or method, and is often
	 * referred to as the "precondition". This expression may refer to any
	 * variables declared within the parameter type pattern. Multiple clauses
	 * may be given, and these are taken together as a conjunction. Furthermore,
	 * the convention is to specify the requires clause(s) before any ensure(s)
	 * clauses.</li>
	 * <li><b>Ensures clause</b>. This defines a constraint on the permissible
	 * values of the the function or method's return value, and is often
	 * referred to as the "postcondition". This expression may refer to any
	 * variables declared within either the parameter or return type pattern.
	 * Multiple clauses may be given, and these are taken together as a
	 * conjunction. Furthermore, the convention is to specify the requires
	 * clause(s) after the others.</li>
	 * </ul>
	 *
	 * <p>The following function declaration provides a small example to
	 * illustrate:</p>
	 *
	 * <pre>
	 * function max(int x, int y) -> (int z)
	 * // return must be greater than either parameter
	 * ensures x <= z && y <= z
	 * // return must equal one of the parmaeters
	 * ensures x == z || y == z:
	 *     ...
	 * </pre>
	 *
	 * <p>Here, we see the specification for the well-known <code>max()</code>
	 * function which returns the largest of its parameters. This does not throw
	 * any exceptions, and does not enforce any preconditions on its parameters.</p>
	 *
	 * <p>
	 * Function and method declarations may also have modifiers, such as
	 * <code>public</code> and <code>private</code>.
	 * </p>
	 */
	public abstract class FunctionOrMethod extends NamedDeclaration {
		public final ArrayList<Parameter> parameters;
		public final ArrayList<Parameter> returns;
		public final ArrayList<Stmt> statements;
		public List<Expr> requires;
		public List<Expr> ensures;

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
		 * @param requires
		 *            - The constraints which must hold true on entry
		 * @param ensures
		 *            - The constraints which must hold true on exit
		 * @param statements
		 *            - The Statements making up the function body.
		 */
		public FunctionOrMethod(List<Modifier> modifiers, String name,
				List<Parameter> returns, List<Parameter> parameters,
				List<Expr> requires, List<Expr> ensures,
				List<Stmt> statements,
				Attribute... attributes) {
			super(name, modifiers,attributes);
			this.returns  = new ArrayList<Parameter>(returns);
			this.parameters = new ArrayList<Parameter>(parameters);
			this.requires = new ArrayList<Expr>(requires);
			this.ensures = new ArrayList<Expr>(ensures);
			this.statements = new ArrayList<Stmt>(statements);
		}

		public abstract SyntacticType.FunctionOrMethod unresolvedType();

		public abstract Nominal.FunctionOrMethod resolvedType();
	}

	/**
	 * Represents a function declaration in a Whiley source file. For example:
	 *
	 * <pre>
	 * function f(int x) -> (int y)
	 * // Parameter must be positive
	 * requires x > 0
	 * // Return must be negative
	 * ensures y < 0:
	 *    // body
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
	 * <p>
	 * <b>NOTE</b> see {@link FunctionOrMethod} for more information.
	 * </p>
	 *
	 * @see FunctionOrMethod
	 *
	 * @author David J. Pearce
	 *
	 */
	public final class Function extends FunctionOrMethod {
		public Nominal.Function resolvedType;

		public Function(List<Modifier> modifiers, String name, List<Parameter> returns,
				List<Parameter> parameters, List<Expr> requires,
				List<Expr> ensures,
				List<Stmt> statements, Attribute... attributes) {
			super(modifiers, name, returns, parameters, requires, ensures,
					statements, attributes);
		}

		public SyntacticType.Function unresolvedType() {
			ArrayList<SyntacticType> paramTypes = new ArrayList<SyntacticType>();
			for (Parameter p : parameters) {
				paramTypes.add(p.type);
			}
			ArrayList<SyntacticType> returnTypes = new ArrayList<SyntacticType>();
			for (Parameter r : returns) {
				returnTypes.add(r.type);
			}
			return new SyntacticType.Function(returnTypes, paramTypes, attributes());
		}

		public Nominal.Function resolvedType() {
			return resolvedType;
		}
	}

	/**
	 * Represents a method declaration in a Whiley source file. For example:
	 *
	 * <pre>
	 * method m(int x) -> (int y)
	 * // Parameter must be positive
	 * requires x > 0
	 * // Return must be negative
	 * ensures $ < 0:
	 *    // body
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
	 * Method declarations may also have modifiers, such as <code>public</code>
	 * and <code>private</code>.
	 * </p>
	 *
	 * <p>
	 * <b>NOTE</b> see {@link FunctionOrMethod} for more information.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public final class Method extends FunctionOrMethod {
		public Nominal.Method resolvedType;

		public Method(List<Modifier> modifiers, String name, List<Parameter> returns, List<Parameter> parameters,
				List<Expr> requires, List<Expr> ensures, List<Stmt> statements, Attribute... attributes) {
			super(modifiers, name, returns, parameters, requires, ensures, statements, attributes);
		}

		public SyntacticType.Method unresolvedType() {
			ArrayList<SyntacticType> parameterTypes = new ArrayList<SyntacticType>();
			for (Parameter p : parameters) {
				parameterTypes.add(p.type);
			}
			ArrayList<SyntacticType> returnTypes = new ArrayList<SyntacticType>();
			for (Parameter r : returns) {
				returnTypes.add(r.type);
			}
			return new SyntacticType.Method(returnTypes, parameterTypes, attributes());
		}

		public Nominal.Method resolvedType() {
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
	public final class Parameter extends AbstractContext {
		public final SyntacticType type;		
		public final String name;

		public Parameter(SyntacticType type, String name,
				Attribute... attributes) {
			super(attributes);
			this.type = type;
			this.name = name;
		}

		public Parameter(SyntacticType type, String name,
				Collection<Attribute> attributes) {
			super(attributes);
			this.type = type;
			this.name = name;
		}

		public String name() {
			return name;
		}
	}

	public static void syntaxError(String msg, Context context,
			SyntacticElement elem) {
		SyntaxError.syntaxError(msg, context.file().filename, elem);
	}

	public static void syntaxError(String msg, Context context,
			SyntacticElement elem, Throwable ex) {
		SyntaxError.syntaxError(msg, context.file().filename, elem, ex);
	}

	public static void internalFailure(String msg, Context context,
			SyntacticElement elem) {
		SyntaxError.internalFailure(msg, context.file().filename, elem);
	}

	public static void internalFailure(String msg, Context context,
			SyntacticElement elem, Throwable ex) {
		SyntaxError.internalFailure(msg, context.file().filename, elem, ex);
	}
}
