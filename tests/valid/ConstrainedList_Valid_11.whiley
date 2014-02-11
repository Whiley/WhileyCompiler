import whiley.lang.System

type state is {string input, int pos} where (pos >= 0) && (pos <= |input|)

function f(state st) => char:
    if st.pos < |st.input|:
        if Char.isLetter(st.input[st.pos]):
            return st.input[st.pos]
    return ' '

method main(System.Console sys) => void:
    char c = f({input: "hello", pos: 0})
    sys.out.println(Any.toString(c))
