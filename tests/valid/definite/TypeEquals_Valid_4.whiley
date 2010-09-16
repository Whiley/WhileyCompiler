define expr as {int}|bool

{int} g({int} input):
    return input + {-1}

void f(expr e):
    if e ~= {int}:
        {int} t = g(e)
        print "GOT: " + str(t)
    else:
        print "GOT SOMETHING ELSE?"

void System::main([string] args):
    e = {1,2,3,4}
    f(e)
 
