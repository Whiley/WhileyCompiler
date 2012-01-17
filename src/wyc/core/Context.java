package wyc.core;

import java.util.*;
import wyc.lang.WhileyFile;
import wyil.util.SyntacticElement;
import wyil.util.SyntaxError;

/**
 * A context captures all the necessary information to look up a given name in a
 * given file, and to report an error on some AST element in that file.
 * 
 * @author David J. Pearce
 * 
 */
public class Context {
	public final WhileyFile file;
	public final ArrayList<WhileyFile.Import> imports;

	public Context(WhileyFile file, List<WhileyFile.Import> imports) {
		this.file = file;
		this.imports = new ArrayList<WhileyFile.Import>(imports);
	}
	
	// The following method are just helpers.
	
	public static void syntaxError(String msg, Context context, SyntacticElement elem) {
		SyntaxError.syntaxError(msg,context.file.filename,elem);
	}
	
	public static void syntaxError(String msg, Context context, SyntacticElement elem, Throwable ex) {
		SyntaxError.syntaxError(msg,context.file.filename,elem,ex);
	}
	
	public static void internalFailure(String msg, Context context, SyntacticElement elem) {
		SyntaxError.internalFailure(msg,context.file.filename,elem);
	}
	
	public static void internalFailure(String msg, Context context, SyntacticElement elem, Throwable ex) {
		SyntaxError.internalFailure(msg,context.file.filename,elem,ex);
	}
}
