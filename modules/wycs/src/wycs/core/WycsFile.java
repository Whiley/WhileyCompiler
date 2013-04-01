package wycs.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import wybs.lang.Attribute;
import wybs.lang.Content;
import wybs.lang.Path;
import wybs.lang.SyntacticElement;
import wycs.io.WyalFileReader;
import wycs.io.WyalFileStructuredPrinter;
import wycs.io.WycsFileReader;
import wycs.io.WycsFileWriter;
import wycs.syntax.Expr;

public class WycsFile {
	
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
			// System.out.println("SCANNING: " + e.id());
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
	private final ArrayList<Declaration> declarations;

	// =========================================================================
	// Constructors
	// =========================================================================

	public WycsFile(Path.ID module) {
		this.module = module;
		this.declarations = new ArrayList<Declaration>();
	}
	
	public WycsFile(Path.ID module, Collection<Declaration> declarations) {
		this.module = module;
		this.declarations = new ArrayList<Declaration>(declarations);
	}
	
	// =========================================================================
	// Accessors
	// =========================================================================
	
	public Path.ID id() {
		return module;
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
	
	// =========================================================================
	// Types
	// =========================================================================

	public interface Declaration {
		public String name();
	}
	
	public class Function extends SyntacticElement.Impl implements Declaration {
		public final String name;
		public final String[] generics;
		public final SemanticType from;
		public final SemanticType to;
		public Code<?> constraint;
		
		public Function(String name, String[] generics, SemanticType from,
				SemanticType to, Code<?> constraint, Attribute... attributes) {
			super(attributes);
			if (!Expr.isValidIdentifier(name)) {
				throw new IllegalArgumentException("illegal identifier: "
						+ name);
			}
			this.name = name;
			this.generics = generics;
			this.from = from;
			this.to = to;
			this.constraint = constraint;
		}
		
		@Override
		public String name() {
			return name;
		}
	}
	
	public static class Macro extends SyntacticElement.Impl implements Declaration {
		public final String name;
		public final String[] generics;
		public final SemanticType from;
		public final Code<?> condition;
		
		public Macro(String name, String[] generics,
				SemanticType parameter, Code<?> condition, Attribute... attributes) {
			super(attributes);
			if(!Expr.isValidIdentifier(name)) {
				throw new IllegalArgumentException("illegal identifier: " + name);
			}
			this.name = name;
			this.generics = generics;
			this.from = parameter;
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
}
