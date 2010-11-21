// indicates a syntax error
define SyntaxError as { string err }

// The current parser state
define state as { string input, int pos } where pos >= 0 && pos <= |input|

// Top-level parse method
expr parse(string input):
    init = {input: input, pos: 0}
    (e,st) = parseAddSubExpr(init)
    if st.pos != |input|:
        return {err:"junk at end of input"}
    return e

(expr, state) parseAddSubExpr(state st):    
    // First, pass left-hand side
    (lhs,st) = parseMulDivExpr(st)
    st = parseWhiteSpace(st)
    // Second, see if there is a right-hand side
    if st.pos < |st.input| && st.input[st.pos] == '+':
        // add expression
        st.pos = st.pos + 1
        (rhs,st) = parseAddSubExpr(st)
        return {op: ADD, lhs: lhs, rhs: rhs},st
    else if st.pos < |st.input| && st.input[st.pos] == '-':
        // subtract expression
        st.pos = st.pos + 1
        (rhs,st) = parseAddSubExpr(st)
        return {op: SUB, lhs: lhs, rhs: rhs},st
    
    // No right-hand side
    return (lhs,st)

(expr, state) parseMulDivExpr(state st):    
    // First, pass left-hand side
    (lhs,st) = parseTerm(st)
    st = parseWhiteSpace(st)
    // Second, see if there is a right-hand side
    if st.pos < |st.input| && st.input[st.pos] == '*':
        // add expression
        st.pos = st.pos + 1
        (rhs,st) = parseMulDivExpr(st)        
        return {op: MUL, lhs: lhs, rhs: rhs}, st
    else if st.pos < |st.input| && st.input[st.pos] == '/':
        // subtract expression
        st.pos = st.pos + 1
        (rhs,st) = parseMulDivExpr(st)
        return {op: DIV, lhs: lhs, rhs: rhs}, st
    
    // No right-hand side
    return (lhs,st)

(expr, state) parseTerm(state st):
    st = parseWhiteSpace(st)    
    if st.pos < |st.input|:
        if isLetter(st.input[st.pos]):
            return parseIdentifier(st)
        else if isNumeric(st.input[st.pos]):
            return parseNumber(st)
    return ({err:"expecting number or variable"},st)

(var, state) parseIdentifier(state st):    
    txt = ""
    // inch forward until end of identifier reached
    while st.pos < |st.input| && isLetter(st.input[st.pos]):
        txt = txt + [st.input[st.pos]]
        st.pos = st.pos + 1
    return ({id:txt}, st)

(expr, state) parseNumber(state st):    
    n = 0
    // inch forward until end of identifier reached
    while st.pos < |st.input| && isNumeric(st.input[st.pos]):
        n = n + st.input[st.pos] - '0'
        st.pos = st.pos + 1
    
    return n, st

// Parse all whitespace upto end-of-file
state parseWhiteSpace(state st):
    while st.pos < |st.input| && isWhiteSpace(st.input[st.pos]):
        st.pos = st.pos + 1
    return st

// Determine what is whitespace
bool isWhiteSpace(char c):
    return c == ' ' || c == '\t' || c == '\n'    
