package wycs.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import wycc.lang.Attribute;
import wycc.lang.CompilationUnit;
import wycc.lang.SyntacticElement;
import wycs.io.WyalFileReader;
import wycs.io.WyalFilePrinter;
import wycs.io.WycsFileReader;
import wycs.io.WycsFileWriter;
import wycs.syntax.Expr;
import wyfs.lang.Content;
import wyfs.lang.Path;

public class WycsFile implements CompilationUnit {

	// =========================================================================
	// Content Type
	// =========================================================================

	public static final Content.Type<WycsFile> ContentType = new Content.Type<WycsFile>() {
		public Path.Entry<WycsFile> accept(Path.Entry<?> e) {
			if (e.contentType() == this) {
				return (Path.Entry<WycsFile>) e;
			}
			return null;
		}

		public WycsFile read(Path.Entry<WycsFile> e, InputStream input)
				throws IOException {
			WycsFileReader reader = new WycsFileReader(e,input);
			return reader.read();
		}

		public void write(OutputStream output, WycsFile module)
				throws IOException {
			WycsFileWriter writer = new WycsFileWriter(
					output);
			writer.write(module);
		}

		public String toString() {
			return "Content-Type: wycs";
		}
	};

	// =========================================================================
	// State
	// =========================================================================

	private final Path.ID module;
	private final String filename;
	private final ArrayList<Declaration> declarations;

	// =========================================================================
	// Constructors
	// =========================================================================

	public WycsFile(Path.ID module, String filename) {
		this.module = module;
		this.filename = filename;
		this.declarations = new ArrayList<Declaration>();
	}

	public WycsFile(Path.ID module, String filename, Collection<Declaration> declarations) {
		this.module = module;
		this.filename = filename;
		this.declarations = new ArrayList<Declaration>(declarations);
	}

	// =========================================================================
	// Accessors
	// =========================================================================

	public Path.ID id() {
		return module;
	}

	public String filename() {
		return filename;
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

	public Declaration declaration(String name, SemanticType type) {
		for (Declaration d : declarations) {
			if (d.name().equals(name)) {
				if (d instanceof Function && ((Function) d).type.equals(type)) {
					return d;
				} else if (d instanceof Macro && ((Macro) d).type.equals(type)) {
					return d;
				}
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

	// =========================================================================
	// Types
	// =========================================================================

	public interface Declaration extends SyntacticElement {
		public String name();
	}

	public static class Function extends SyntacticElement.Impl implements Declaration {
		public final String name;
		public final SemanticType.Function type;
		public Code<?> constraint;

		public Function(String name, SemanticType.Function type,
				Code<?> constraint, Attribute... attributes) {
			super(attributes);
			if (!Expr.isValidIdentifier(name)) {
				throw new IllegalArgumentException("illegal identifier: "
						+ name);
			}
			this.name = name;
			this.type = type;
			this.constraint = constraint;
		}

		@Override
		public String name() {
			return name;
		}
	}

	public static class Macro extends SyntacticElement.Impl implements Declaration {
		public final String name;
		public final SemanticType.Function type;
		public Code<?> condition;

		public Macro(String name, SemanticType.Function type,
				Code<?> condition, Attribute... attributes) {
			super(attributes);
			if (!Expr.isValidIdentifier(name)) {
				throw new IllegalArgumentException("illegal identifier: "
						+ name);
			}
			this.name = name;
			this.type = type;
			this.condition = condition;
		}

		@Override
		public String name() {
			return name;
		}
	}

	public static class Assert extends SyntacticElement.Impl implements Declaration {
		public final String message;
		public Code<?> condition;

		public Assert(String message, Code<?> condition, Attribute... attributes) {
			super(attributes);
			this.message = message;
			this.condition = condition;
		}

		public Assert(String message, Code<?> condition, Collection<Attribute> attributes) {
			super(attributes);
			this.message = message;
			this.condition = condition;
		}

		public String name() {
			return ""; // anonymous
		}
	}
	
	public static class Type extends SyntacticElement.Impl implements Declaration {
		public final String name;
		public final SemanticType type;
		public Code<?> invariant;
		
		public Type(String name, SemanticType type, Code<?> invariant, Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.type = type;
			this.invariant = invariant;		
		}
		
		public Type(String name, SemanticType type, Code<?> invariant, Collection<Attribute> attributes) {
			super(attributes);
			this.name = name;
			this.type = type;
			this.invariant = invariant;		
		}

		@Override
		public String name() {
			return name;
		}
	}
}
