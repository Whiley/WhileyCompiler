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

package wyil.util.type;

import java.util.HashMap;
import java.util.HashSet;

import wybs.lang.NameID;
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
			term = Union(term,parse(typeVariables));
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
			return Negation(parseNotTerm(typeVariables));
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
			return Array(elem, false);
		}
		case '{':
		{
			match("{");
			Type elem = parse(typeVariables);
			skipWhiteSpace();
			// record
			HashMap<String,Type> fields = new HashMap<String,Type>();
			String id = parseIdentifier();
			fields.put(id, elem);
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
				fields.put(id, elem);
				skipWhiteSpace();
			}
			match("}");
			return Record(isOpen,fields);			
		}
		default:
		{
			String typeVariable = parseIdentifier();
			if(typeVariables.contains(typeVariable)) {
				return Nominal(new NameID(Trie.fromString("$"),
						typeVariable));
			} else {
				typeVariables = new HashSet<String>(typeVariables);
				typeVariables.add(typeVariable);
				match("<");
				Type t = parse(typeVariables);
				match(">");
				NameID label = new NameID(Trie.fromString("$"),
						typeVariable);
				return Recursive(label, t);
			}
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
