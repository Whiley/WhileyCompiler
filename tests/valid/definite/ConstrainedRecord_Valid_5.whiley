define state as {string input, int pos} where pos >= 0 && pos <= |input|

state parseWhiteSpace(state st):
    if(st.pos < |st.input| && st.input[st.pos] == ' '):
        return parseWhiteSpace({input:st.input,pos:st.pos+1})
    else:
        return st

state parseTerm(state st):
    st = parseWhiteSpace(st)
    return st

void System::main([string] args):
    state st = {input:"  Hello",pos:0}
    st = parseTerm(st)
    print str(st)
