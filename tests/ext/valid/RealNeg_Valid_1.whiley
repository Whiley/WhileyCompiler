real f(real x) requires x > 0, ensures $ < 0:
    return -x

void System::main([string] args):
    out.println(str(f(1.2)))
    out.println(str(f(0.00001)))
    out.println(str(f(5632)))
