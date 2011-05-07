define iset as {int} | int

string f(iset e):
    if e ~= {int}:
        return "{int}"
    else:
        return "int"

void System::main([string] args):
    out<->println(f({1,2,3}))
    out<->println(f(1))
