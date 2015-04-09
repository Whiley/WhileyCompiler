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
    assume f(0) == true
    assume f(1) == false
