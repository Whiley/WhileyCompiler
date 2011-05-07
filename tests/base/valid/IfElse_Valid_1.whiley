string f(int x):
    if(x < 10):
        return "LESS THAN"
    else if(x > 10):
        return "GREATER THAN"
    else:
        return "EQUALS"

void System::main([string] args):
    out<->println(f(1))
    out<->println(f(10))
    out<->println(f(11))
    out<->println(f(1212))
    out<->println(f(-1212))