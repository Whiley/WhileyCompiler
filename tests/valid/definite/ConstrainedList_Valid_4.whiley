define state as (string input, int pos) requires pos >= 0 && pos <= |input|

char f(state st):
    if(st.pos < |st.input|):
        if isLetter(st.input[st.pos]):
            return st.input[st.pos]
    return ' '

void System::main([string] args):
    char c = f((input:"hello",pos:0))
    print str(c)
 
