package wycs.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import wybs.lang.Attribute;
import wybs.lang.CompilationUnit;
import wybs.lang.Content;
import wybs.lang.Path;
import wybs.lang.SyntacticElement;
import wybs.util.Trie;

import wycs.io.*;

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

		public WycsFile read(Path.Entry<WycsFile> e, InputStream input) throws IOException {			
			WycsFileReader reader = new WycsFileReader(e.id().toString(),input);
			return reader.read();
		}

		public void write(OutputStream output, WycsFile module) throws IOException {
			WycsFilePrinter writer = new WycsFilePrinter(output);
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

	public WycsFile(Path.ID module, String filename, Collection<Declaration> declarations) {
		this.module = module;
		this.filename = filename;
		this.declarations = new ArrayList<Declaration>(declarations);
	}
	
	// =========================================================================
	// Accessors
	// =========================================================================
	
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
	// Types
	// =========================================================================		
	
	public interface Declaration extends SyntacticElement {
		public String name();
	}

	public interface Context extends SyntacticElement {
		public WycsFile file();
		public List<Import> imports();
	}
	
	private abstract class AbstractContext extends SyntacticElement.Impl implements Context {

		private AbstractContext(Attribute... attributes) {
			super(attributes);
		}
		
		private AbstractContext(Collection<Attribute> attributes) {
			super(attributes);
		}
		
		public WycsFile file() {
			return WycsFile.this;
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
			imports.add(new WycsFile.Import(Trie.fromString(module.parent(), "*"), null)); 
			
			for(Declaration d : declarations) {
				if(d == this) {
					break;
				} else if(d instanceof Import) {
					imports.add((Import)d);
				}
			}			
			imports.add(new WycsFile.Import(Trie.fromString(module), "*"));
			
			Collections.reverse(imports);	
			
			return imports;
		}			
	}

	
	public class Function extends AbstractContext implements
			Declaration {
		public final String name;
		public final ArrayList<String> generics;
		public final SyntacticType from;
		public final SyntacticType to;
		public Expr condition;

		public Function(String name, Collection<String> generics, SyntacticType from, SyntacticType to,
				Expr condition, Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.generics = new ArrayList<String>(generics);
			this.from = from;
			this.to = to;
			this.condition = condition;
		}
		
		public String name() {
			return name;
		}
	}
	
	public class Define extends Function {
		public Define(String name, Collection<String> generics, SyntacticType parameter, 
				Expr condition, Attribute... attributes) {
			super(name,generics,parameter,new SyntacticType.Primitive(null,SemanticType.Bool),condition,attributes);
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
