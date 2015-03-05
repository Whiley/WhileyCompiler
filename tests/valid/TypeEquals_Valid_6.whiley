import whiley.lang.System

type SyntaxError is {ASCII.string msg}

function f(int x) -> ASCII.string:
    SyntaxError|{ASCII.string input} nst
    //
    if x > 0:
        nst = {input: "Hello World"}
    else:
        nst = syntaxError("problem")
    //
    if nst is {ASCII.string msg}:
        return "error"
    else:
        return nst.input

function syntaxError(ASCII.string errorMessage) -> SyntaxError:
    return {msg: errorMessage}

method main(System.Console sys) -> void:
    sys.out.println(f(0))
    sys.out.println(f(1))
