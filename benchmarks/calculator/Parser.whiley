// indicates a syntax error
define SyntaxError as { string err }

// The current parser state
define State as { string input, int pos }

define SExpr as SyntaxError | Expr

// Top-level parse method
SExpr parse(string input):
    init = {input: input, pos: 0}
    (e,st) = parseAddSubExpr(init)
    if st.pos != |input|:
        return {err:"junk at end of input"}
    return e

(SExpr, State) parseAddSubExpr(State st):    
    // First, pass left-hand side
    (lhs,st) = parseMulDivExpr(st)
    
    if lhs ~= SyntaxError:
        return lhs,st    
    
    st = parseWhiteSpace(st)
    // Second, see if there is a right-hand side
    if st.pos < |st.input| && st.input[st.pos] == '+':
        // add expression
        st.pos = st.pos + 1
        (rhs,st) = parseAddSubExpr(st)
        
        if rhs ~= SyntaxError:
            return rhs,st    
        
        return {op: ADD, lhs: lhs, rhs: rhs},st
    else if st.pos < |st.input| && st.input[st.pos] == '-':
        // subtract expression
        st.pos = st.pos + 1
        (rhs,st) = parseAddSubExpr(st)
        
        if rhs ~= SyntaxError:
            return rhs,st    
        
        return {op: SUB, lhs: lhs, rhs: rhs},st
    
    // No right-hand side
    return (lhs,st)

(SExpr, State) parseMulDivExpr(State st):    
    // First, pass left-hand side
    (lhs,st) = parseTerm(st)
    
    if lhs ~= SyntaxError:
        return lhs,st    
    
    st = parseWhiteSpace(st)
    // Second, see if there is a right-hand side
    if st.pos < |st.input| && st.input[st.pos] == '*':
        // add expression
        st.pos = st.pos + 1
        (rhs,st) = parseMulDivExpr(st)        
        
        if rhs ~= SyntaxError:
            return rhs,st           
        
        return {op: MUL, lhs: lhs, rhs: rhs}, st
    else if st.pos < |st.input| && st.input[st.pos] == '/':
        // subtract expression
        st.pos = st.pos + 1
        (rhs,st) = parseMulDivExpr(st)
        
        if rhs ~= SyntaxError:
            return rhs,st           
        
        return {op: DIV, lhs: lhs, rhs: rhs}, st
    
    // No right-hand side
    return (lhs,st)

(SExpr, State) parseTerm(State st):
    st = parseWhiteSpace(st)    
    if st.pos < |st.input|:
        if isLetter(st.input[st.pos]):
            return parseIdentifier(st)
        else if isNumeric(st.input[st.pos]):
            return parseNumber(st)
    return ({err:"expecting number or variable"},st)

(Var, State) parseIdentifier(State st):    
    txt = ""
    // inch forward until end of identifier reached
    while st.pos < |st.input| && isLetter(st.input[st.pos]):
        txt = txt + [st.input[st.pos]]
        st.pos = st.pos + 1
    return ({id:txt}, st)

(Expr, State) parseNumber(State st):    
    n = 0
    // inch forward until end of identifier reached
    while st.pos < |st.input| && isNumeric(st.input[st.pos]):
        n = n + st.input[st.pos] - '0'
        st.pos = st.pos + 1    
    return n, st

// Parse all whitespace upto end-of-file
State parseWhiteSpace(State st):
    while st.pos < |st.input| && isWhiteSpace(st.input[st.pos]):
        st.pos = st.pos + 1
    return st

// Determine what is whitespace
bool isWhiteSpace(char c):
    return c == ' ' || c == '\t' || c == '\n'    
