package wycs.syntax;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import wycc.lang.Attribute;
import wycc.lang.CompilationUnit;
import wycc.lang.SyntacticElement;
import wycs.io.*;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.Trie;

public class WyalFile implements CompilationUnit {

	// =========================================================================
	// Content Type
	// =========================================================================

	public static final Content.Type<WyalFile> ContentType = new Content.Type<WyalFile>() {
		public Path.Entry<WyalFile> accept(Path.Entry<?> e) {
			if (e.contentType() == this) {
				return (Path.Entry<WyalFile>) e;
			}
			return null;
		}

		public WyalFile read(Path.Entry<WyalFile> e, InputStream input) throws IOException {
			WyalFileReader reader = new WyalFileReader(e.location().toString(),input);
			return reader.read();
		}

		public void write(OutputStream output, WyalFile module) throws IOException {
			//WycsFileClassicalPrinter writer = new WycsFileClassicalPrinter(output);
			WyalFilePrinter writer = new WyalFilePrinter(output);
			writer.write(module);
		}

		public String toString() {
			return "Content-Type: wyal";
		}
	};

	// =========================================================================
	// State
	// =========================================================================

	private final Path.ID id;
	private final String filename;
	private final ArrayList<Declaration> declarations;

	// =========================================================================
	// Constructors
	// =========================================================================

	public WyalFile(Path.ID module, String filename) {
		this.id = module;
		this.filename = filename;
		this.declarations = new ArrayList<Declaration>();
	}

	// =========================================================================
	// Accessors
	// =========================================================================

	public Path.ID id() {
		return id;
	}

	public List<Declaration> declarations() {
		return declarations;
	}

	public Declaration declaration(String name) {
		for(Declaration d : declarations) {
			if(d.name().equals(name)) {
				return d;
			}
		}
		return null;
	}

	public <T extends Declaration> T declaration(String name, Class<T> type) {
		for (Declaration d : declarations) {
			if (d.name().equals(name) && type.isInstance(d)) {
				return (T) d;
			}
		}
		return null;
	}

	public String filename() {
		return filename;
	}

	// =========================================================================
	// Mutators
	// =========================================================================

	public void add(Declaration d) {
		declarations.add(d);
	}

	// =========================================================================
	// Types
	// =========================================================================

	public interface Context extends SyntacticElement {
		public WyalFile file();
		public List<Import> imports();
	}

	public interface Declaration extends Context {
		public String name();
	}

	public abstract class AbstractContext extends SyntacticElement.Impl implements Context {

		protected AbstractContext(Attribute... attributes) {
			super(attributes);
		}

		protected AbstractContext(Collection<Attribute> attributes) {
			super(attributes);
		}

		public WyalFile file() {
			return WyalFile.this;
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
			imports.add(new WyalFile.Import(Trie.fromString(id.parent(), "*"), null));

			for(Declaration d : declarations) {
				if(d == this) {
					break;
				} else if(d instanceof Import) {
					imports.add((Import)d);
				}
			}
			imports.add(new WyalFile.Import(Trie.fromString(id), "*"));

			Collections.reverse(imports);

			return imports;
		}
	}

	public class Function extends AbstractContext implements
			Declaration {
		public final String name;
		public final ArrayList<String> generics;
		public final TypePattern from;
		public final TypePattern to;
		public Expr constraint;

		public Function(String name, List<String> generics, TypePattern from,
				TypePattern to, Expr constraint, Attribute... attributes) {
			super(attributes);
			if(!Expr.isValidIdentifier(name)) {
				throw new IllegalArgumentException("illegal identifier: " + name);
			}
			this.name = name;
			this.generics = new ArrayList<String>(generics);
			this.from = from;
			this.to = to;
			this.constraint = constraint;
		}

		@Override
		public String name() {
			return name;
		}
	}

	public class Macro extends AbstractContext implements Declaration {
		public final String name;
		public final ArrayList<String> generics;
		public final TypePattern from;
		public Expr body;

		public Macro(String name, List<String> generics, TypePattern parameter,
				Expr body, Attribute... attributes) {
			super(attributes);
			if(!Expr.isValidIdentifier(name)) {
				throw new IllegalArgumentException("illegal identifier: " + name);
			}
			this.name = name;
			this.generics = new ArrayList<String>(generics);
			this.from = parameter;
			this.body = body;
		}

		@Override
		public String name() {
			return name;
		}
	}

	public class Type extends AbstractContext implements Declaration {
		public final String name;
		public final ArrayList<String> generics;
		public final TypePattern type;
		public Expr invariant;

		public Type(String name, List<String> generics, TypePattern parameter,
				Expr body, Attribute... attributes) {
			super(attributes);
			if(!Expr.isValidIdentifier(name)) {
				throw new IllegalArgumentException("illegal identifier: " + name);
			}
			this.name = name;
			this.generics = new ArrayList<String>(generics);
			this.type = parameter;
			this.invariant = body;
		}
		
		public Type(String name, List<String> generics, TypePattern parameter,
				Expr body, Collection<Attribute> attributes) {
			super(attributes);
			if(!Expr.isValidIdentifier(name)) {
				throw new IllegalArgumentException("illegal identifier: " + name);
			}
			this.name = name;
			this.generics = new ArrayList<String>(generics);
			this.type = parameter;
			this.invariant = body;
		}

		@Override
		public String name() {
			return name;
		}
	}

	public class Assert extends AbstractContext implements Declaration {
		public final String message;
		public Expr expr;

		public Assert(String message, Expr expr, Attribute... attributes) {
			super(attributes);
			this.message = message;
			this.expr = expr;
		}

		public Assert(String message, Expr expr, Collection<Attribute> attributes) {
			super(attributes);
			this.message = message;
			this.expr = expr;
		}

		public String name() {
			return ""; // anonymous
		}
	}

	public class Assume extends AbstractContext implements Declaration {
		public final String name;
		public Expr expr;

		public Assume(String message, Expr expr, Attribute... attributes) {
			super(attributes);
			this.name = message;
			this.expr = expr;
		}

		public Assume(String message, Expr expr, Collection<Attribute> attributes) {
			super(attributes);
			this.name = message;
			this.expr = expr;
		}

		public String name() {
			return ""; // anonymous
		}
	}

	/**
	 * Represents an import declaration in a Wycs source file. For example:
	 *
	 * <pre>
	 * import wycs.lang.Map
	 * </pre>
	 *
	 * Here, the package is <code>wycs.lang</code>, and the module is
	 * <code>Map</code>.
	 *
	 * @author David J. Pearce
	 *
	 */
	public class Import extends AbstractContext implements Declaration {
		public final Trie filter;
		public final String name;

		public Import(Trie filter, String name, Attribute... attributes) {
			super(attributes);
			this.filter = filter;
			this.name = name;
		}

		public String name() {
			return ""; // anonymous
		}
	}

}
