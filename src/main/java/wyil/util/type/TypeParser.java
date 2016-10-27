// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyil.util.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import wybs.lang.NameID;
import wycc.util.Pair;
import wyfs.util.Trie;
import wyil.lang.Type;
import static wyil.lang.Type.*;

/**
 * The Type Parser is used to convert a given string into a type (or a syntax
 * error). This is generally useful for debugging purposes, but it does have
 * other usees as well.
 *
 * @author David J. Pearce
 *
 */
public class TypeParser {
	private int index;
	private String str;

	public TypeParser(String str) {
		this.str = str;
	}

	public Type parse() {
		return parse(new HashSet<String>());
	}

	public Type parse(HashSet<String> typeVariables) {
		Type term = parseNotTerm(typeVariables);
		skipWhiteSpace();
		while (index < str.length()
				&& (str.charAt(index) == '|')) {
			// union type
			match("|");
			term = Type.Union(term,parse(typeVariables));
			skipWhiteSpace();
		}
		return term;
	}
//  FIXME: This is broken and needs to be updated to handle multiple returns.
//	public Type parseFunctionTerm(HashSet<String> typeVariables) {
//		Type t = parseNotTerm(typeVariables);
//		if(index >= str.length()) { return t; }
//		char lookahead = str.charAt(index);
//		if(lookahead == '(') {
//			// this is a tuple, not a bracketed type.
//			List<Type> parameters = parseParameters(typeVariables);
//			skipWhiteSpace();
//			return Function(t,parameters);
//		}
//		return t;
//	}
//
//	private List<Type> parseParameters(HashSet<String> typeVariables) {
//		match("(");
//		ArrayList<Type> elems = new ArrayList();
//		elems.add(parse(typeVariables));
//		char lookahead = str.charAt(index);
//		while(lookahead == ',') {
//			match(",");
//			elems.add(parse(typeVariables));
//			skipWhiteSpace();
//			lookahead = str.charAt(index);
//		}
//		match(")");
//		return elems;
//	}

	public Type parseNotTerm(HashSet<String> typeVariables) {
		skipWhiteSpace();
		char lookahead = str.charAt(index);
		if(lookahead == '!') {
			match("!");
			return Type.Negation(parseNotTerm(typeVariables));
		} else {
			return parseBraceTerm(typeVariables);
		}
	}
	public Type parseBraceTerm(HashSet<String> typeVariables) {
		skipWhiteSpace();
		char lookahead = str.charAt(index);
		if(lookahead == '(') {
			match("(");
			Type t = parse(typeVariables);
			skipWhiteSpace();
			match(")");
			skipWhiteSpace();
			return t;
		} else {
			return parseTerm(typeVariables);
		}
	}
	public Type parseTerm(HashSet<String> typeVariables) {
		skipWhiteSpace();
		char lookahead = str.charAt(index);

		switch (lookahead) {
		case 'a':
			match("any");
			return T_ANY;
		case 'v':
			match("void");
			return T_VOID;
		case 'n':
			match("null");
			return T_NULL;
		case 'b':
			if ((index + 1 < str.length()) && str.charAt(index + 1) == 'o') {
				match("bool");
				return T_BOOL;
			} else {
				match("byte");
				return T_BYTE;
			}
		case 'i':
			match("int");
			return T_INT;
		case '[':
		{
			match("[");
			Type elem = parse(typeVariables);
			match("]");
			return Type.Array(elem);
		}
		case '{':
		{
			match("{");
			Type elem = parse(typeVariables);
			skipWhiteSpace();
			// record
			ArrayList<Pair<Type,String>> fields = new ArrayList<Pair<Type,String>>();
			String id = parseIdentifier();
			fields.add(new Pair<Type,String>(elem, id));
			skipWhiteSpace();
			boolean isOpen = false;
			while(index < str.length() && str.charAt(index) == ',') {
				match(",");
				if(str.charAt(index) == '.') {
					match("...");
					isOpen=true;
					break;
				}
				elem = parse(typeVariables);
				id = parseIdentifier();
				fields.add(new Pair<Type,String>(elem, id));
				skipWhiteSpace();
			}
			match("}");
			return Type.Record(isOpen,fields);
		}
		default: {
			String typeVariable = parseIdentifier();
			return Type.Nominal(new NameID(Trie.fromString("$"), typeVariable));
		}

		}
	}
	private String parseIdentifier() {
		skipWhiteSpace();
		int start = index;
		while (index < str.length()
				&& Character.isJavaIdentifierPart(str.charAt(index))) {
			index++;
		}
		return str.substring(start,index);
	}
	private void skipWhiteSpace() {
		while (index < str.length()
				&& Character.isWhitespace(str.charAt(index))) {
			index++;
		}
	}
	private void match(String match) {
		skipWhiteSpace();
		if ((str.length() - index) < match.length()
				|| !str.startsWith(match, index)) {
			throw new IllegalArgumentException("invalid type string: "
					+ str);
		}
		index += match.length();
	}
}
