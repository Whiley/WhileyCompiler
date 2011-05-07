string f(int x, real y):
    if x == y:
        return "EQUAL"
    else:
        return "NOT EQUAL"

void System::main([string] args):
    out<->println(f(1,4.0))
    out<->println(f(1,4.2))
    out<->println(f(0,0))
