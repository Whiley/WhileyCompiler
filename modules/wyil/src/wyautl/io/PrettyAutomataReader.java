package wyautl.io;

import java.io.*;
import java.util.*;
import wyautl.lang.*;

public class PrettyAutomataReader {	
	private final InputStream input;
	private final String[] schema;
	private final HashMap<String,Integer> rSchema;
	private int pos;
	
	public PrettyAutomataReader(InputStream reader, String[] schema) {
		this.input = reader;		
		this.schema = schema;
		this.rSchema = new HashMap<String,Integer>();
		for(int i=0;i!=schema.length;++i) {
			rSchema.put(schema[i], i);
		}
	}
	
	public Automaton read() throws IOException,SyntaxError {
		ArrayList<Automaton.State> states = new ArrayList<Automaton.State>();
		parseTerm(states,-1);
		return new Automaton(states);
	}

	protected int parseTerm(ArrayList<Automaton.State> states, int lookahead)
			throws IOException, SyntaxError {
		skipWhiteSpace();
		int index = states.size();
		states.add(null);
		String name = parseIdentifier(lookahead);
		System.out.println("READ: " + name);
		Integer kind = rSchema.get(name);
		if(kind == null) {
			throw new SyntaxError("unrecognised term encountered (" + name + ")",pos,pos);
		}
		match("(");		
		boolean firstTime = true;
		ArrayList<Integer> children = new ArrayList<Integer>();
		while ((lookahead = input.read()) != -1 && lookahead != ')') {
			if (!firstTime) {
				match(",");
			}
			children.add(parseTerm(states,lookahead));
		}
		if (lookahead == -1) {
			throw new SyntaxError("expecting ')'", pos, pos);
		}
		int[] nchildren = new int[children.size()];
		for (int i = 0; i != children.size(); ++i) {
			nchildren[i] = children.get(i);
		}		
		states.set(index, new Automaton.State(kind, nchildren));
		return index;

	}
	
	protected String parseIdentifier(int lookahead) throws IOException {
		StringBuffer sb = new StringBuffer();
		if(lookahead != -1) {
			sb.append((char)lookahead);
		}		
		while ((lookahead = input.read()) != -1 && Character.isJavaIdentifierPart((char)lookahead)) {			
			sb.append((char) lookahead);
		}
		System.out.println()
		return sb.toString();
	}
	
	protected void match(String x) throws IOException,SyntaxError {
		skipWhiteSpace();
		for(int i=0;i!=x.length();++i) {
			char e = x.charAt(i);
			int c = input.read();
			if(c == -1 || ((char)c) != e) {
				throw new SyntaxError("expecting " + e,pos,pos);	
			}
			pos = pos + 1;
		}		
	}
	protected void skipWhiteSpace() throws IOException {
		int c;
		while ((c = input.read()) != -1 && Character.isWhitespace(c)) {
			pos = pos + 1;
		}
	}
		
	public static final class SyntaxError extends Exception {
		public final int start;
		public final int end;
		public SyntaxError(String msg, int start, int end) {
			super(msg);
			this.start=start;
			this.end=end;
		}
	}

}
