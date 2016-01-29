

type state is ({int[] input, int pos} s) where (s.pos >= 0) && (s.pos <= |s.input|)

function parseWhiteSpace(state st) -> state:
    if (st.pos < |st.input|) && (st.input[st.pos] == ' '):
        return parseWhiteSpace({input: st.input, pos: st.pos + 1})
    else:
        return st

function parseTerm(state st) -> state:
    st = parseWhiteSpace(st)
    return st

public export method test() :
    state st = {input: "  Hello", pos: 0}
    assume parseTerm(st) == {input:[32, 32, 72, 101, 108, 108, 111],pos:2}
