package wycs;

import static wycs.Solver.SCHEMA;

import java.io.FileInputStream;
import java.io.PrintStream;

import wyautl.core.Automaton;
import wyautl.io.PrettyAutomataReader;
import wyautl.io.PrettyAutomataWriter;

public class Main {
	public static PrintStream errout;
	
	/**
	 * Initialise the error output stream so as to ensure it will display
	 * unicode characters (when possible). Additionally, extract version
	 * information from the enclosing jar file.
	 */
	static {
		try {
			errout = new PrintStream(System.err, true, "UTF8");
		} catch(Exception e) {
			errout = System.err;
		}
	}
	
	public static void main(String[] args) {	
		boolean verbose = true;
		try {
			FileInputStream fin = new FileInputStream(args[0]);
			PrettyAutomataReader reader = new PrettyAutomataReader(fin,SCHEMA);
			PrettyAutomataWriter writer = new PrettyAutomataWriter(System.err, SCHEMA, "And", "Or");
			Automaton automaton = reader.read();						
			writer.write(automaton);
			System.out.println();
			Solver.infer(automaton);
			System.out.println("\n\n=> (" + Solver.numSteps
					+ " steps, " + Solver.numInferences
					+ " reductions, " + Solver.numInferences
					+ " inferences)\n");			
			writer.write(automaton);
			writer.flush();
			System.out.println();			
		} catch (Throwable e) {
			errout.println("internal failure (" + e.getMessage() + ")");
			if (verbose) {
				e.printStackTrace(errout);
			}
		
		}		
	}	
}
