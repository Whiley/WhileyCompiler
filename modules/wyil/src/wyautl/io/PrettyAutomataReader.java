package wyautl.io;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import wyautl.lang.*;

public class PrettyAutomataReader {
	private final Reader reader;
	private int pos;
	
	public PrettyAutomataReader(Reader reader) {
		this.reader = reader;		
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
		match("(");		
		boolean firstTime = true;
		ArrayList<Integer> children = new ArrayList<Integer>();
		while ((lookahead = reader.read()) != -1 && lookahead != ')') {
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
		states.set(index, new Automaton.State(0, nchildren));
		return index;

	}
	
	protected String parseIdentifier(int lookahead) throws IOException {
		StringBuffer sb = new StringBuffer();
		if(lookahead != -1) {
			sb.append((char)lookahead);
		}
		int c;
		while ((c = reader.read()) != -1 && Character.isJavaIdentifierPart(c)) {
			sb.append((char) c);
		}
		return sb.toString();
	}
	
	protected void match(String x) throws IOException,SyntaxError {
		skipWhiteSpace();
		for(int i=0;i!=x.length();++i) {
			char e = x.charAt(i);
			int c = reader.read();
			if(c == -1 || ((char)c) != e) {
				throw new SyntaxError("expecting " + e,pos,pos);	
			}
			pos = pos + 1;
		}		
	}
	protected void skipWhiteSpace() throws IOException {
		int c;
		while ((c = reader.read()) != -1 && Character.isWhitespace(c)) {
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
