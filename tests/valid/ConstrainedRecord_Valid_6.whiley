import whiley.lang.*

type state is {[int] input, int pos} where (pos >= 0) && (pos <= |input|)

function parseWhiteSpace(state st) -> state:
    if (st.pos < |st.input|) && (st.input[st.pos] == ' '):
        return parseWhiteSpace({input: st.input, pos: st.pos + 1})
    else:
        return st

function parseTerm(state st) -> state:
    st = parseWhiteSpace(st)
    return st

method main(System.Console sys) -> void:
    state st = {input: "  Hello", pos: 0}
    st = parseTerm(st)
    sys.out.println(st)
