import whiley.lang.*

type SyntaxError is {[int] msg}

function f(int x) -> bool:
    SyntaxError|{[int] input} nst
    //
    if x > 0:
        nst = {input: "Hello World"}
    else:
        nst = syntaxError("problem")
    //
    if nst is {[int] msg}:
        return true
    else:
        return false

function syntaxError([int] errorMessage) -> SyntaxError:
    return {msg: errorMessage}

method main(System.Console sys) -> void:
    sys.out.println(f(0))
    sys.out.println(f(1))
