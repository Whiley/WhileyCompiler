package wycs.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import wybs.lang.Attribute;
import wybs.lang.Content;
import wybs.lang.Path;
import wybs.lang.SyntacticElement;

import wycs.io.*;

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

	private final String filename;
	private final ArrayList<Declaration> declarations;

	// =========================================================================
	// Constructors
	// =========================================================================

	public WycsFile(String filename, Collection<Declaration> declarations) {
		this.filename = filename;
		this.declarations = new ArrayList<Declaration>(declarations);
	}
	
	// =========================================================================
	// Accessors
	// =========================================================================
	
	public List<Declaration> declarations() {
		return declarations;
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

	
	public static class Function extends SyntacticElement.Impl implements
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
		
		public String toString() {
			String gens = "";
			if (generics.size() > 0) {
				gens += "<";
				for (int i = 0; i != generics.size(); ++i) {
					if (i != 0) {
						gens = gens + ", ";
					}
					gens = gens + generics.get(i);
				}
				gens += "> ";
			}

			String from = this.from.toString();
			String to = this.to.toString();
			String condition = this.condition != null ? " where "
					+ this.condition : "";

			if (this instanceof Define) {
				return "define " + name + gens + from + condition;
			} else {
				return "function " + name + gens + from + " => " + to
						+ condition;
			}
		}
	}
	
	public static class Define extends Function {
		public Define(String name, Collection<String> generics, SyntacticType parameter, 
				Expr condition, Attribute... attributes) {
			super(name,generics,parameter,new SyntacticType.Primitive(null,SemanticType.Bool),condition,attributes);
		}
	}
	
	public static class Assert extends Stmt {
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
		
		public String toString() {
			if(message == null) {
				return "assert " + expr;	
			} else {
				return "assert " + expr + ", \"" + message + "\"";
			}
			
		}
	}	
}
