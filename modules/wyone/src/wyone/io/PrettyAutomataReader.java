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
		this.lookaheads = new int[2];		
	}
	
	public Automaton read() throws IOException,SyntaxError {
		Automaton automaton = new Automaton(schema);
		int root = parseState(automaton);
		automaton.mark(root);
		return automaton;
	}

	protected int parseState(Automaton automaton) throws IOException, SyntaxError {
		skipWhiteSpace();
		int lookahead = lookahead();
		
		switch(lookahead) {
			case '(':
				return parseBracketed(automaton);
			case '[':
			case '{':
				return parseCompound(automaton);
			case '-':
				return parseInteger(automaton,true);
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
				return parseInteger(automaton,false);
			case '\"':
				return parseString(automaton);
			default:
				return parseTerm(automaton);				
		}		
	}
	
	protected int parseTerm(Automaton automaton)
			throws IOException, SyntaxError {
			
		// ======== parse identifier ===========
		StringBuffer sb = new StringBuffer();
		int lookahead;
		while ((lookahead = lookahead()) != -1
				&& Character.isJavaIdentifierPart((char) lookahead)) {
			sb.append((char) next());
		}				
		String name = sb.toString();
		
		Integer kind = rSchema.get(name);
		if (kind == null) {
			throw new SyntaxError("unrecognised term encountered (" + name
					+ ")", pos, pos);
		} 
		Type.Term type = schema[kind];
		int data = -1;
		
		if(type.data != null) {
			data = parseState(automaton);
		}
		
		return automaton.add(new Automaton.Term(kind, data));
	}
	
	protected int parseBracketed(Automaton automaton) throws IOException, SyntaxError {
		match('(');
		int r = parseState(automaton);
		match(')');
		return r;
	}
	
	protected int parseInteger(Automaton automaton, boolean negative)
			throws IOException, SyntaxError {		
		if(negative) {
			match('-');
		}
		StringBuffer sb = new StringBuffer();		
		int lookahead;
		while ((lookahead = lookahead()) != -1
				&& Character.isDigit((char) lookahead)) {
			sb.append((char) next());
		}
		
		// FIXME: should support arbitrary sized ints
		int val = Integer.parseInt(sb.toString());
	
		if(negative) {
			val = -val;
		}
		
		return automaton.add(new Automaton.Int(BigInteger.valueOf(val)));
	}
	
	protected int parseString(Automaton automaton)
			throws IOException, SyntaxError {		
		StringBuffer sb = new StringBuffer();
		int lookahead = next(); // skip starting '\"'
		
		while ((lookahead = next()) != -1
				&& ((char)lookahead) != '\"') {
			sb.append((char) lookahead);
		}

		return automaton.add(new Automaton.Strung(sb.toString()));
	}
	
	protected int parseCompound(Automaton automaton)
			throws IOException, SyntaxError {
		int lookahead = next(); // skip opening brace
		
		int kind;
		
		switch(lookahead) {
			case '[':
				kind = Automaton.K_LIST;
				break;
			case '{':
				if(lookahead() == '|') {
					next(); // skip bar
					kind = Automaton.K_BAG;
				} else {
					kind = Automaton.K_SET;
				}
				break;
			default:
				throw new IllegalArgumentException("invalid compound start");
		}
				
		boolean firstTime = true;
		ArrayList<Integer> children = new ArrayList<Integer>();
		
		while ((lookahead = lookahead()) != -1 && lookahead != ']'
				&& lookahead != '|' && lookahead != '}') {
			if (!firstTime) {
				if (lookahead != ',') {
					throw new SyntaxError("expecting ','", pos, pos);
				}
				next();
			} else {
				firstTime = false;
			}
			children.add(parseState(automaton));
			skipWhiteSpace();
		}
						
		switch(kind) {
			case Automaton.K_LIST:
				match(']');	
				break;
			case Automaton.K_BAG:
				match('|');
				match('}');
				break;
			case Automaton.K_SET:
				match('}');				
				break;
			default:				
		}
		
		if(kind == Automaton.K_LIST) { 
			return automaton.add(new Automaton.List(children));
		} else if(kind == Automaton.K_BAG) { 
			return automaton.add(new Automaton.Bag(children));
		} else {
			return automaton.add(new Automaton.Set(children));
		}
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
	
	protected void match(char c) throws IOException, SyntaxError {
		int lookahead = next();
		if (lookahead != c) {
			throw new SyntaxError("expecting '" + c + "' --- found '"
					+ (char) lookahead + "\'", pos, pos);
		}
	}
	
	protected void skipWhiteSpace() throws IOException {
		int lookahead;
		while ((lookahead = lookahead()) != -1
				&& Character.isWhitespace(lookahead)) {
			next(); // dummy
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