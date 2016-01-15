// Define the 8bit ASCII character
public type char is (int x) where 0 <= x && x <= 255
//
type State is ({char[] input, int pos} s) where s.pos >= 0
type Expr is real | {int[] id}
type SyntaxError is {int[] err}
type SExpr is SyntaxError | Expr

public function isDigit(char c) -> bool:
    return '0' <= c && c <= '9'

function parseTerm(State st) -> {SExpr f1, State f2}:
    if st.pos < |st.input|:
        if isDigit(st.input[st.pos]):
            return parseNumber(st)
    return {f1: {err: "unknown expression encountered"}, f2: st}

function parseNumber(State st) -> {Expr f1, State f2}:
    int n = 0
    while st.pos < |st.input| && isDigit(st.input[st.pos]) where st.pos >= 0:
        n = n + (int) (st.input[st.pos] - '0')
        st.pos = st.pos + 1
    return {f1: (real) n, f2: st}

public export method test() :
    {SExpr f1, State f2} s = parseTerm({input: "123", pos: 0})
    assume s.f1 == 6.0
    s = parseTerm({input: "abc", pos: 0})
    //
    assume s.f1 is SyntaxError
