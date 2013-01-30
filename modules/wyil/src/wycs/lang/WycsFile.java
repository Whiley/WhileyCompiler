package wycs.lang;

import java.util.ArrayList;
import java.util.Collection;

import wyautl.core.*;

public class WycsFile {	
	/**
	 * The set of statements to be processed (in order)
	 */
	private ArrayList<Stmt> statements;
		
	public WycsFile() {
		this.statements = new ArrayList<Stmt>();
	}
	
	public WycsFile(Collection<Stmt> stmts) {
		this.statements = new ArrayList<Stmt>(stmts);
	}
	
	public void add(Stmt statement) {
		statements.add(statement);
	}	
}
