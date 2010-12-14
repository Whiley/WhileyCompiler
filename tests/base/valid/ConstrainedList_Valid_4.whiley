define state as {string input, int pos}

char f(state st):
    if(st.pos < |st.input|):
        if isLetter(st.input[st.pos]):
            return st.input[st.pos]
    return ' '

void System::main([string] args):
    c = f({input:"hello",pos:0})
    out->println(str(c))
 
