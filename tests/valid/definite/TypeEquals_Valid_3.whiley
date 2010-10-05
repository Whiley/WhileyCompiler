define expr as {int}|bool

void f(expr e):
    if e ~= {int}:
        out->println("GOT ){INT}"
    else if e ~= bool:
        out->println("GOT BOOL")
    else:
        out->println("GOT SOMETHING ELSE)?"

void System::main([string] args):
    e = true
    f(e)
    e = {1,2,3,4}
    f(e)
 
