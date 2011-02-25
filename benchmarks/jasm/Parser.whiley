define state as {string input, int pos}
define SyntaxError as {string msg}

ClassFile|SyntaxError parseClassFile(string input):
    st = {input: input, pos:0}
    // match ".class"
    kind,st = parseClassOrInterface(st)
    // check for error
    if kind ~= SyntaxError:
        return kind
    // parse public, private, static, etc    
    mods,st = parseClassModifiers(st)
    // get name
    name,st = parseIdentifier(st)
    // now, look for superclass
    super,st = parseSuperClass(st)
    // and now for implemented interfaces
    // Finally, create the class file record
    return { 
        minor_version: 0, 
        major_version: 0,
        modifiers: mods,    
        type: ["blah"],
        superClass: [""] }


(bool|SyntaxError,state) parseClassOrInterface(state st):
    st = parseWhiteSpace(st)
    nst = match(".",st)
    // check for error
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

(class_t|null|SyntaxError,state) parseSuperClass(state st):
    st = parseWhiteSpace(st)
    nst = match(".",st)
    // check for error
    if nst ~= SyntaxError:
        return (null,st)
    ident,nst = parseIdentifier(nst)
    if ident == "super":
        nst = parseWhiteSpace(nst)
        class,nst = parseClassType(nst)
        if nst ~= SyntaxError:
            return "expecting class type"
        else:
            return (class,nst)
    else:
        return (null,st)
    
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
// Types
// ================================================================

(class_t, state) parseClassType(state st):
    id,st = parseIdentifier(st)
    r = [id]
    while st.pos < |st.input| && st.input[st.pos] == '/':
        st.pos = st.pos + 1
        id,st = parseIdentifier(st)    
        r = r + [id]
    return r,st

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
    ss = st.input[st.pos..st.pos+|m|]
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
    return c == ' ' || c == '\t' || c == '\n' || c == '\r'

// Create a syntax error
SyntaxError syntaxError(string errorMessage):
    return {msg: errorMessage}
