import whiley.lang.System

type state is {ASCII.string input, int pos} where (pos >= 0) && (pos <= |input|)

public function isLetter(ASCII.char c) -> bool:
    return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z')

function f(state st) -> ASCII.char:
    if st.pos < |st.input|:
        if isLetter(st.input[st.pos]):
            return st.input[st.pos]
    return ' '

method main(System.Console sys) -> void:
    ASCII.char c = f({input: "hello", pos: 0})
    sys.out.println(Any.toString(c))
