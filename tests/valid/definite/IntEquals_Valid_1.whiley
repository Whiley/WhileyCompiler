void f(int x, real y):
    out->println(str(x))
    out->println(str(y))
    if x == y:
        out->println("EQUAL")
    else:
        out->println("NOT EQUAL")

void System::main([string] args):
    f(1,4.0)
    f(1,4.2)
    f(0,0)
