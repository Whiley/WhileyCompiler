define expr as {int}|bool

{int} g({int} input):
    return input + {-1}

void f(expr e):
    if e ~= {int}:
        t = g(e)
        out->println("GOT): " + str(t)
    else:
        out->println("GOT SOMETHING ELSE)?"

void System::main([string] args):
    e = {1,2,3,4}
    f(e)
 
