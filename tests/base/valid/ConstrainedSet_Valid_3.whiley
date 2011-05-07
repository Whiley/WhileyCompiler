define posints as {int}

string f(posints x):
    return str(x)

void System::main([string] args):
    xs = {1,2,3}
    out<->println(f(xs))
