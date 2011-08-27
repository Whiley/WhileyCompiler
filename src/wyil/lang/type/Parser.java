package wyil.lang.type;

import java.util.HashMap;

import wyil.lang.Type;
import static wyil.lang.Type.*;

/**
 * The Type Parser is used to convert a given string into a type (or a syntax
 * error). This is generally useful for debugging purposes, but it does have
 * other usees as well.
 * 
 * @author djp
 * 
 */
public class Parser {
	private int index;
	private String str;
	
	public Parser(String str) { 
		this.str = str;
	}
	
	public Type parse() {
		Type term = parseTerm();
		skipWhiteSpace();
		while(index < str.length() && str.charAt(index) == '|') {
			// union type
			match("|");
			term = T_UNION(term,parse());
			skipWhiteSpace();
		}
		return term;
	}
	public Type parseTerm() {
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
			match("bool");
			return T_BOOL;
		case 'c':
			match("char");
			return T_CHAR;
		case 'i':
			match("int");
			return T_INT;
		case 'r':
			match("real");
			return T_REAL;
		case '[':
		{
			match("[");
			Type elem = parse();
			match("]");
			return T_LIST(elem);
		}
		case '{':
		{
			match("{");
			Type elem = parse();
			skipWhiteSpace();
			if(index < str.length() && str.charAt(index) != '}') {
				// record
				HashMap<String,Type> fields = new HashMap<String,Type>();
				String id = parseIdentifier();
				fields.put(id, elem);
				skipWhiteSpace();
				while(index < str.length() && str.charAt(index) == ',') {
					match(",");
					elem = parse();
					id = parseIdentifier();
					fields.put(id, elem);
					skipWhiteSpace();
				}
				match("}");
				return T_RECORD(fields);					
			}
			match("}");
			return T_SET(elem);
		}
		default:
			throw new IllegalArgumentException("invalid type string: "
					+ str);
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
