import println from whiley.lang.System

define SyntaxError as {string msg}

string f(int x):
    if x > 0:        
        nst = {input: "Hello World"}
    else:
        nst = syntaxError("problem")
    // check for error
    if nst is {string msg}:
        return "error"
    else:
        return nst.input

// Create a syntax error
SyntaxError syntaxError(string errorMessage):
    return {msg: errorMessage}

void ::main(System.Console sys):
    sys.out.println(f(0))
    sys.out.println(f(1))
