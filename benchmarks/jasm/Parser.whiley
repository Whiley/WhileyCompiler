define state as {string input, int pos}
define SyntaxError as {string msg}

ClassFile|SyntaxError parseClassFile(string input):
    print "STAGE 1"
    st = {input: input, pos:0}
    // match ".class"
    print "STAGE 2"
    kind,st = parseClassOrInterface(st)
    // check for error
    if kind ~= SyntaxError:
        return kind
    print "STAGE 3"
    // parse public, private, static, etc    
    mods,st = parseClassModifiers(st)
    // get name
    name,st = parseIdentifier(st)
    print "NAME: " + name
    print "MODIFIERS: " + str(mods)
    // Finally, create the class file record
    return { 
        minor_version: 0, 
        major_version: 0,
        modifiers: mods,    
        type: ["blah"],
        superClass: [""] }


(bool|SyntaxError,state) parseClassOrInterface(state st):
    nst = match(".",st)
    // check for error

    // NOTE: the following line should be testing against a syntax 
    // error.  However, without verification enabled this test cannot be
    // proven.

    if nst ~= {[int] msg}:
        return (nst,st)
    // ok ...
    ident,nst = parseIdentifier(nst)
    if ident == "interface":
        return (false,nst)
    else if ident == "class":
        return (true,nst)
    else:
        return syntaxError("expecting class or interface"),st

// ================================================================
// Modifiers
// ================================================================
//
// Modifers are prepended to classes and methods.  Examples include
// "public","private",etc

define ClassModifiers as {
    "public",
    "final",
    "abstract"
}

({ClassModifier},state) parseClassModifiers(state st):
    st = parseWhiteSpace(st)
    tokens = {}
    token,nst = parseIdentifier(st)
    while token in ClassModifiers:
        tokens = tokens + {convertClassModifier(token)}
        st = nst
        token,nst = parseIdentifier(st)
    return (tokens,st)

// Following should be eliminated by a map
ClassModifier convertClassModifier(string token):
    if token == "public":
        return PUBLIC
    else if token == "abstract":
        return ABSTRACT
    else:
        return FINAL

// ================================================================
// Identifiers
// ================================================================

(string, state) parseIdentifier(state st):    
    st = parseWhiteSpace(st)
    txt = ""
    // inch forward until end of identifier reached
    while st.pos < |st.input| && isLetter(st.input[st.pos]):
        txt = txt + [st.input[st.pos]]
        st.pos = st.pos + 1
    return (txt, st)

// ================================================================
// Misc
// ================================================================

SyntaxError|state match(string m, state st):
    st = parseWhiteSpace(st)
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
