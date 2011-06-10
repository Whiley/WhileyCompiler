int f(real x, real y):
    return x % y    

void System::main([string] args):
    out<->println(str(f(10.5343,5.2354)))
    out<->println(str(f(10.5343,4.2345)))
    out<->println(str(f(1,4.2345)))
    out<->println(str(f(10.53433,2)))
    out<->println(str(f(-10.5343,5.2354)))
    out<->println(str(f(-10.5343,4.2345)))
    out<->println(str(f(-1,4.2345)))
    out<->println(str(f(-10.53433,2)))
    out<->println(str(f(-10.5343,-5)))
    out<->println(str(f(-10.5343,-4)))
    out<->println(str(f(-1,-4)))
    out<->println(str(f(-10.53433,-2)))
    out<->println(str(f(10.5343,-5)))
    out<->println(str(f(10.5343,-4)))
    out<->println(str(f(1,-4)))
    out<->println(str(f(10.53433,-2)))
