package wycs.builder;

import java.util.*;
import static wybs.lang.SyntaxError.*;
import wybs.lang.Attribute;
import wycs.core.*;
import wycs.syntax.*;

public class CodeGeneration {
	private final WycsBuilder builder;
	private String filename;
	
	public CodeGeneration(WycsBuilder builder) {
		this.builder = builder;
	}
	
	public WycsFile generate(WyalFile file) {
		this.filename = file.filename();
		ArrayList<WycsFile.Declaration> declarations = new ArrayList();
		for(WyalFile.Declaration d : file.declarations()) {
			declarations.add(generate(d));
		}
		return new WycsFile(file.id(),file.filename(),declarations);
	}
	
	protected WycsFile.Declaration generate(WyalFile.Declaration declaration) {
		if(declaration instanceof WyalFile.Define) {
			return generate((WyalFile.Define)declaration);
		} else if(declaration instanceof WyalFile.Function) {
			return generate((WyalFile.Function)declaration);
		} else if(declaration instanceof WyalFile.Assert) {
			return generate((WyalFile.Assert)declaration);
		} else {
			internalFailure("unknown declaration encounterd",filename,declaration);
			return null;
		}
	}
	
	protected WycsFile.Declaration generate(WyalFile.Define d) {
		return null;
	}
	
	protected WycsFile.Declaration generate(WyalFile.Function d) {
		return null;		
	}
	
	protected WycsFile.Declaration generate(WyalFile.Assert d) {
		Code condition = generate(d.expr);
		return new WycsFile.Assert(d.message, condition, d.attribute(Attribute.Source.class));
	}
	
	protected Code generate(Expr e) {
		return Code.Constant(Value.Bool(true));
	}
}
