// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyautl.io;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import wyautl.core.*;
import wyautl.util.BigRational;

public class PrettyAutomataReader {
	private final InputStream input;
	private final int[] lookaheads;
	private final Schema schema;
	private final HashMap<String,Integer> rSchema;
	private int start,end;
	private int pos;

	public PrettyAutomataReader(InputStream reader, Schema schema) {
		this.input = reader;
		this.schema = schema;
		this.rSchema = new HashMap<String,Integer>();
		for(int i=0;i!=schema.size();++i) {
			rSchema.put(schema.get(i).name, i);
		}
		this.lookaheads = new int[2];
	}

	public Automaton read() throws IOException,SyntaxError {
		Automaton automaton = new Automaton();
		int root = parseState(automaton);
		automaton.setRoot(0,root);
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
				return parseNumber(automaton,true);
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
				return parseNumber(automaton,false);
			case '\"':
				return parseString(automaton);
			default:
				return parseTermOrBool(automaton);
		}
	}

	protected int parseTermOrBool(Automaton automaton)
			throws IOException, SyntaxError {

		// ======== parse identifier ===========
		StringBuffer sb = new StringBuffer();
		int lookahead;
		while ((lookahead = lookahead()) != -1
				&& Character.isJavaIdentifierPart((char) lookahead)) {
			sb.append((char) next());
		}
		String name = sb.toString();

		if(name.equals("true")) {
			return automaton.add(Automaton.TRUE);
		} else if(name.equals("false")) {
			return automaton.add(Automaton.FALSE);
		}

		Integer kind = rSchema.get(name);
		if (kind == null) {
			throw new SyntaxError("unrecognised term encountered (" + name
					+ ")", pos, pos);
		}
		Schema.Term type = schema.get(kind);
		int data = -1;

		if(type.child != null) {
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

	protected int parseNumber(Automaton automaton, boolean negative)
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

		if(lookahead == '.') {
			sb.append((char) next());
			while ((lookahead = lookahead()) != -1
					&& Character.isDigit((char) lookahead)) {
				sb.append((char) next());
			}
			BigRational val = new BigRational(sb.toString());

			if(negative) {
				val = val.negate();
			}

			return automaton.add(new Automaton.Real(val));

		} else {
			BigInteger val = new BigInteger(sb.toString());

			if(negative) {
				val = val.negate();
			}

			return automaton.add(new Automaton.Int(val));
		}
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
