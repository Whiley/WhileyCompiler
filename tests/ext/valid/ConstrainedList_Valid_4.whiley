import whiley.lang.*:*

define state as {string input, int pos} where pos >= 0 && pos <= |input|

char f(state st):
    if(st.pos < |st.input|):
        if isLetter(st.input[st.pos]):
            return st.input[st.pos]
    return ' '

void ::main(System sys,[string] args):
    c = f({input:"hello",pos:0})
    sys.out.println(str(c))
 
