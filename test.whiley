// The current parser state
define state as { string input, int pos } where pos >= 0 && pos <= |input|

state parseWhiteSpace(state st):
    input = st.input
    pos = st.pos
    while pos >= 0 && pos < |input| && input[pos] != ' ':
        st.pos = st.pos + 1
    return {input: input, pos: pos}
