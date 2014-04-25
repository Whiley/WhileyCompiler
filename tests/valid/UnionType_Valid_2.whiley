import whiley.lang.System

type State is {string input, int pos} where pos >= 0

type Expr is real | {string id}

type SyntaxError is {string err}

type SExpr is SyntaxError | Expr

function parseTerm(State st) => (SExpr, State):
    if st.pos < |st.input|:
        if Char.isDigit(st.input[st.pos]):
            return parseNumber(st)
    return ({err: "unknown expression encountered"}, st)

function parseNumber(State st) => (Expr, State):
    int n = 0
    while st.pos < |st.input| && Char.isDigit(st.input[st.pos]) where st.pos >= 0:
        n = (n + st.input[st.pos]) - '0'
        st.pos = st.pos + 1
    return (n, st)

method main(System.Console sys) => void:
    SExpr e, State s = parseTerm({input: "123", pos: 0})
    sys.out.println(Any.toString(e))
    e, s = parseTerm({input: "abc", pos: 0})
    //
    if e is SyntaxError:
        sys.out.println(e.err)
    else:
        sys.out.println(Any.toString(e))
