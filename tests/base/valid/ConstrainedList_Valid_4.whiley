import println from whiley.lang.System

define state as {string input, int pos}

char f(state st):
    if(st.pos < |st.input|):
        if Char.isLetter(st.input[st.pos]):
            return st.input[st.pos]
    return ' '

void ::main(System.Console sys):
    c = f({input:"hello",pos:0})
    sys.out.println(Any.toString(c))
 
