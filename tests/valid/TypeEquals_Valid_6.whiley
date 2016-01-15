

type SyntaxError is {int[] msg}

function f(int x) -> bool:
    SyntaxError|{int[] input} nst
    //
    if x > 0:
        nst = {input: "Hello World"}
    else:
        nst = syntaxError("problem")
    //
    if nst is {int[] msg}:
        return true
    else:
        return false

function syntaxError(int[] errorMessage) -> SyntaxError:
    return {msg: errorMessage}

public export method test() :
    assume f(0) == true
    assume f(1) == false
