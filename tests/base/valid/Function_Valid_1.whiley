string f(real x):
    return "GOT REAL"

string f(int x):
    return "GOT INT"

void System::main([string] args):
    out.println(f(1))
    out.println(f(1.23))
