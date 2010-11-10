define SyntaxError as {string msg}

string f(int x):
    if x > 0:        
        nst = {input: "Hello World"}
    else:
        nst = syntaxError("problem")
    // check for error
    if nst ~= SyntaxError:
        return "error"
    else:
        return nst.input

// Create a syntax error
SyntaxError syntaxError(string errorMessage):
    return {msg: errorMessage}

void System::main([string] args):
    out->println(f(0))
    out->println(f(1))
