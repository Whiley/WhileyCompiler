define expr as {int}|bool

{int} g({int} input):
    return input + {-1}

string f(expr e):
    if e is {int}:
        t = g(e)
        return "GOT: " + str(t)
    else:
        return "GOT SOMETHING ELSE?"

void System::main([string] args):
    e = {1,2,3,4}
    this.out.println(f(e))
 
