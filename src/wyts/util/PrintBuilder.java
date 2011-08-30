package wyts.util;

import java.io.PrintStream;

import wyil.lang.NameID;
import wyil.util.Pair;
import wyts.lang.Automata;

/**
 * The print builder is an example implementation of type builder which
 * simply constructs a textual representation of the type in the form of a
 * graph.
 */
public class PrintBuilder implements TypeBuilder {
	private final PrintStream out;
	
	public PrintBuilder(PrintStream out) {
		this.out = out;
	}
	
	public void initialise(int numNodes) { }
	
	public void buildPrimitive(int index, Automata.Leaf type) {
		out.println("#" + index + " = " + type);
	}
	
	public void buildExistential(int index, NameID name) {
		out.println("#" + index + " = ?" + name);
	}
	
	public void buildSet(int index, int element) {
		out.println("#" + index + " = {#" + element + "}");
	}

	public void buildList(int index, int element) {
		out.println("#" + index + " = [#" + element + "]");
	}

	public void buildProcess(int index, int element) {
		out.println("#" + index + " = process #" + element);
	}

	public void buildDictionary(int index, int key, int value) {
		out.println("#" + index + " = {#" + key + "->#" + value + "}");
	}

	public void buildTuple(int index, int... elements) {
		out.print("#" + index + " = (");
		boolean firstTime=true;
		for(int e : elements) {
			if(!firstTime) {
				out.print(", ");
			}
			firstTime=false;
			out.print("#" + e);
		}
		out.println(")");
	}

	public void buildRecord(int index, Pair<String, Integer>... fields) {
		out.print("#" + index + " = {");
		boolean firstTime=true;
		for(Pair<String,Integer> e : fields) {
			if(!firstTime) {
				out.print(", ");
			}
			firstTime=false;
			out.print("#" + e.second() + " " + e.first());
		}
		out.println("}");
	}

	public void buildFunction(int index, int ret, int... parameters) {
		out.print("#" + index + " = ");			
		out.print("#" + ret + "(");
		boolean firstTime=true;
		for(int e : parameters) {
			if(!firstTime) {
				out.print(", ");
			}
			firstTime=false;
			out.print("#" + e);
		}
		out.println(")");
	}
	
	public void buildMethod(int index, int receiver, int ret, int... parameters) {
		out.print("#" + index + " = ");
		if(receiver != -1) {
			out.print("#" + receiver);
		}
		out.print("::#" + ret + "(");
		boolean firstTime=true;
		for(int e : parameters) {
			if(!firstTime) {
				out.print(", ");
			}
			firstTime=false;
			out.print("#" + e);
		}
		out.println(")");
	}
	
	public void buildUnion(int index, int... bounds) {
		out.print("#" + index + " = ");
		boolean firstTime=true;
		for(int e : bounds) {
			if(!firstTime) {
				out.print(" | ");
			}
			firstTime=false;
			out.print("#" + e);
		}
		out.println();
	}
	
	public void buildIntersection(int index, int... bounds) {
		out.print("#" + index + " = ");
		boolean firstTime=true;
		for(int e : bounds) {
			if(!firstTime) {
				out.print(" & ");
			}
			firstTime=false;
			out.print("#" + e);
		}
		out.println();
	}
	
	public void buildDifference(int index, int left, int right) {
		out.println("#" + index + " = #" + left + " - #" + right);		
	}
}