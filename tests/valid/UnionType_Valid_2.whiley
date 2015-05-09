type State is {[int] input, int pos} where pos >= 0
type Expr is real | {[int] id}
type SyntaxError is {[int] err}
type SExpr is SyntaxError | Expr
// Define the 8bit ASCII character
public type char is (int x) where 0 <= x && x <= 255

public function isDigit(char c) -> bool:
    return '0' <= c && c <= '9'

function parseTerm(State st) -> (SExpr, State):
    if st.pos < |st.input|:
        if isDigit(st.input[st.pos]):
            return parseNumber(st)
    return ({err: "unknown expression encountered"}, st)

function parseNumber(State st) -> (Expr, State):
    int n = 0
    while st.pos < |st.input| && isDigit(st.input[st.pos]) where st.pos >= 0:
        n = n + (int) (st.input[st.pos] - '0')
        st.pos = st.pos + 1
    return ((real) n), st

public export method test() -> void:
    SExpr e, State s = parseTerm({input: "123", pos: 0})
    assume e == 6.0
    e, s = parseTerm({input: "abc", pos: 0})
    //
    assume e is SyntaxError
