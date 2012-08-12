package wyautl.io;

import java.io.*;
import java.util.*;
import wyautl.lang.*;

public class PrettyAutomataReader {	
	private final InputStream input;
	private final String[] schema;
	private final DataReader[] readers;
	private final HashMap<String,Integer> rSchema;
	private int pos;
	
	public PrettyAutomataReader(InputStream reader, String[] schema, DataReader[] readers) {
		this.input = reader;		
		this.schema = schema;
		this.readers = readers;
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
		lookahead = skipWhiteSpace(lookahead);
		int index = states.size();
		states.add(null);
		
		// ======== parse identifier ===========
		StringBuffer sb = new StringBuffer();
		if(lookahead != -1) {
			sb.append((char)lookahead);
		}		
		while ((lookahead = input.read()) != -1
				&& Character.isJavaIdentifierPart((char) lookahead)) {
			sb.append((char) lookahead);
		}	
		String name = sb.toString();
		Integer kind = rSchema.get(name);
		if (kind == null) {
			throw new SyntaxError("unrecognised term encountered (" + name
					+ ")", pos, pos);
		} else if (lookahead == -1 || (lookahead != '(' && lookahead != '{')) {
			throw new SyntaxError("expecting ')'", pos, pos);
		}
		
		boolean sequential = lookahead == '(';
		
		// ======== parse terms ===========
		boolean firstTime = true;
		ArrayList<Integer> children = new ArrayList<Integer>();
		while ((lookahead = input.read()) != -1 && lookahead != ')' && lookahead != '}' && lookahead != ':') {
			if (!firstTime) {
				if(lookahead != ',') {
					throw new SyntaxError("expecting ','",pos,pos);
				}
				lookahead = -1;
			} else {
				firstTime=false;
			}
			children.add(parseTerm(states, lookahead));
		}
		Object data = null;
		if (lookahead == ':') {
			String str = "";
			while ((lookahead = input.read()) != -1 && lookahead != ')'
					&& lookahead != '}') {
				str += (char) lookahead;
			}
			if (readers == null || kind >= readers.length) {
				throw new SyntaxError("data reader for \"" + name
						+ "\" required", pos, pos);
			}
			DataReader reader = readers[kind];
			if (reader == null) {
				throw new SyntaxError("data reader for \"" + name
						+ "\" required", pos, pos);
			}
			data = reader.parseData(kind, str);
		}
		if (lookahead == -1 || (sequential && lookahead != ')')) {
			throw new SyntaxError("expecting ')'", pos, pos);
		} else if(!sequential && lookahead != '}') {
			throw new SyntaxError("expecting '}'", pos, pos);
		}
		// ======== parse supplementary data ===========

		// ======== create automaton state ===========
		int[] nchildren = new int[children.size()];
		for (int i = 0; i != children.size(); ++i) {
			nchildren[i] = children.get(i);
		}		
		states.set(index, new Automaton.State(kind, data, sequential, nchildren));
		return index;

	}
	
	protected int skipWhiteSpace(int lookahead) throws IOException {
		if(lookahead != -1 && !Character.isWhitespace(lookahead)) {
			return lookahead;
		}
		while ((lookahead = input.read()) != -1 && Character.isWhitespace(lookahead)) {
			pos = pos + 1;
		}
		return lookahead;
	}
	
	public static interface DataReader {
		public Object parseData(int kind, String text);
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
