import println from whiley.lang.System

define state as {string input, int pos}

state parseWhiteSpace(state st):
    if(st.pos < |st.input| && st.input[st.pos] == ' '):
        return parseWhiteSpace({input:st.input,pos:st.pos+1})
    else:
        return st

state parseTerm(state st):
    st = parseWhiteSpace(st)
    return st

void ::main(System.Console sys):
    st = {input:"  Hello",pos:0}
    st = parseTerm(st)
    sys.out.println(Any.toString(st))
