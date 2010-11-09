define state as {string input, int pos}
define SyntaxError as {string msg}

string parseClassOrInterface(state st):
    nst = match(".",st)
    // check for error
    if nst ~= SyntaxError:
        return "error"
    // ok ...
    ident,nst = parseIdentifier(nst)
    return ident    

// ================================================================
// Identifiers
// ================================================================

(string, state) parseIdentifier(state st):        
    return ("", st)

// ================================================================
// Misc
// ================================================================

SyntaxError|state match(string m, state st):
    if (st.pos + |m|) > |st.input|:
        return syntaxError("expected " + m)
    ss = st.input[st.pos:st.pos+|m|]
    if ss != m:
        return syntaxError("expected " + m)
    return {pos: st.pos+|m|,input: st.input}

// Parse all whitespace upto end-of-file
state parseWhiteSpace(state st):
    while st.pos < |st.input| && isWhiteSpace(st.input[st.pos]):
        st.pos = st.pos + 1
    return st

// Determine what is whitespace
bool isWhiteSpace(char c):
    return c == ' ' || c == '\t' || c == '\n'    

// Create a syntax error
SyntaxError syntaxError(string errorMessage):
    return {msg: errorMessage}

void System::main([string] args):
    ident = parseClassOrInterface({pos:0, input: ".class"})
    out->println(ident)
    ident = parseClassOrInterface({pos:0, input: "asdasd"})
    out->println(ident)