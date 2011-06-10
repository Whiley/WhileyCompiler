int f(int x, real y):
    return x % y    

void System::main([string] args):
    out<->println(str(f(10,5.23)))
    out<->println(str(f(10,4)))
    out<->println(str(f(1,4)))
    out<->println(str(f(103,2)))
    out<->println(str(f(-10,5.23)))
    out<->println(str(f(-10,4)))
    out<->println(str(f(-1,4)))
    out<->println(str(f(-103,2)))
    out<->println(str(f(-10,-5.23)))
    out<->println(str(f(-10,-4)))
    out<->println(str(f(-1,-4)))
    out<->println(str(f(-103,-2)))
    out<->println(str(f(10,-5.23)))
    out<->println(str(f(10,-4)))
    out<->println(str(f(1,-4)))
    out<->println(str(f(103,-2)))
