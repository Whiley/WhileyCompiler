package wyone.io;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import wyone.core.*;

public class PrettyAutomataReader {	
	private final InputStream input;
	private final int[] lookaheads;	
	private final Type.Term[] schema;
	private final HashMap<String,Integer> rSchema;
	private int start,end;
	private int pos;
	
	public PrettyAutomataReader(InputStream reader, Type.Term[] schema) {
		this.input = reader;		
		this.schema = schema;
		this.rSchema = new HashMap<String,Integer>();
		for(int i=0;i!=schema.length;++i) {
			rSchema.put(schema[i].name, i);
		}
		this.lookaheads = new int[1];		
	}
	
	public Automaton read() throws IOException,SyntaxError {
		ArrayList<Automaton.State> states = new ArrayList<Automaton.State>();
		parseTerm(states);
		return new Automaton(schema,states);
	}

	protected int parseState(ArrayList<Automaton.State> states) throws IOException, SyntaxError {
		skipWhiteSpace();
		int lookahead = lookahead();
		
		switch(lookahead) {
			case '(':
			case '{':
			case '[':
				return parseCompound(states);
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				return parseInteger(states);
			case '\"':
				return parseString(states);
			default:
				return parseTerm(states);				
		}		
	}
	
	protected int parseTerm(ArrayList<Automaton.State> states)
			throws IOException, SyntaxError {
		
		int index = states.size();
		states.add(null);
		
		// ======== parse identifier ===========
		StringBuffer sb = new StringBuffer();
		int lookahead;
		while ((lookahead = next()) != -1
				&& Character.isJavaIdentifierPart((char) lookahead)) {
			sb.append((char) lookahead);
		}
		push(lookahead);
		
		String name = sb.toString();
		Integer kind = rSchema.get(name);
		if (kind == null) {
			throw new SyntaxError("unrecognised term encountered (" + name
					+ ")", pos, pos);
		} 
		Type.Term type = schema[kind];
		int data = -1;
		if(type.data != null) {
			data = parseState(states);
		}
		
		states.set(index, new Automaton.Term(kind, data));
		return index;

	}
	
	protected int parseInteger(ArrayList<Automaton.State> states)
			throws IOException, SyntaxError {		
		int index = states.size();
		states.add(null);

		StringBuffer sb = new StringBuffer();		
		int lookahead;
		while ((lookahead = next()) != -1
				&& Character.isDigit((char) lookahead)) {
			sb.append((char) lookahead);
		}
		push(lookahead);
		
		// FIXME: should support arbitrary sized ints
		int val = Integer.parseInt(sb.toString());
		states.set(index, new Automaton.Item(Automaton.K_INT,
				BigInteger.valueOf(val)));
		
		return index;
	}
	
	protected int parseString(ArrayList<Automaton.State> states)
			throws IOException, SyntaxError {		
		int index = states.size();
		states.add(null);
		StringBuffer sb = new StringBuffer();
		int lookahead = next(); // skip starting '\"'
		
		while ((lookahead = next()) != -1
				&& ((char)lookahead) != '\"') {
			sb.append((char) lookahead);
		}
		// no need to push here.

		states.set(index, new Automaton.Item(Automaton.K_STRING,
				sb.toString()));
		
		return index;
	}
	
	protected int parseCompound(ArrayList<Automaton.State> states)
			throws IOException, SyntaxError {
		int index = states.size();
		states.add(null);
		int lookahead = next(); // skip opening brace
		
		int kind;
		
		switch(lookahead) {
			case '(':
				kind = Automaton.K_LIST;
				break;
			case '{':
				kind = Automaton.K_SET;
				break;
			case '[':
				kind = Automaton.K_BAG;
				break;
			default:
				throw new IllegalArgumentException("invalid compound start");
		}
				
		boolean firstTime = true;
		ArrayList<Integer> children = new ArrayList<Integer>();
		
		while ((lookahead = next()) != -1 && lookahead != ')'
				&& lookahead != '}' && lookahead != ':') {
			if (!firstTime) {
				if (lookahead != ',') {
					throw new SyntaxError("expecting ','", pos, pos);
				}
				lookahead = next();
			} else {
				firstTime = false;
			}
			children.add(parseState(states));
		}
		
		
		// ======== create automaton state ===========
		
		states.set(index, new Automaton.Compound(kind,children));
		return index;
	}
	
	protected int next() throws IOException {
		int lookahead;
		if(start != end) {
			lookahead = lookaheads[start];
			start = (start + 1) % lookaheads.length;			
		} else {
			lookahead = input.read();
		}
		return lookahead;
	}
	
	protected int lookahead() throws IOException {
		int lookahead;
		if(start != end) {
			lookahead = lookaheads[start];						
		} else {
			lookahead = input.read();
			lookaheads[end] = lookahead;
			end = (end + 1) % lookaheads.length;
		}
		return lookahead;
	}
	
	protected void push(int item) {
		if(item != -1) {
			lookaheads[end] = item;
			end = (end + 1) % lookaheads.length;
		}
	}
	
	protected void skipWhiteSpace() throws IOException {
		int lookahead;
		while ((lookahead = input.read()) != -1
				&& Character.isWhitespace(lookahead)) {
			pos = pos + 1;
		}
		push(lookahead);
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
