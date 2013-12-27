import println from whiley.lang.System

define State as { string input, int pos }
define Expr as real | { string id }
define SyntaxError as { string err }
define SExpr as SyntaxError | Expr

(SExpr, State) parseTerm(State st):
    if st.pos < |st.input|:
        if Char.isDigit(st.input[st.pos]):
            return parseNumber(st)
    return {err: "unknown expression encountered"},st

(Expr, State) parseNumber(State st):    
    n = 0
    // inch forward until end of identifier reached
    while st.pos < |st.input| && Char.isDigit(st.input[st.pos]):
        n = n + st.input[st.pos] - '0'
        st.pos = st.pos + 1    
    return n, st

void ::main(System.Console sys):
    e,s = parseTerm({input: "123", pos: 0})
    sys.out.println(Any.toString(e))
    e,s = parseTerm({input: "abc", pos: 0})
    if e is SyntaxError: 
        sys.out.println(e.err)
    else:
        sys.out.println(Any.toString(e))

