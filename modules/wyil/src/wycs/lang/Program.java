package wycs.lang;

import java.util.ArrayList;

import wyautl.core.*;

public class Program {	
	/**
	 * The set of statements to be processed (in order)
	 */
	private ArrayList<Stmt> statements;
		
	public Program() {
		this.statements = new ArrayList<Stmt>();
	}
	
	public void add(Stmt statement) {
		statements.add(statement);
	}	
}
