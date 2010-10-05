void f(int x):
    if(x < 10):
        out->println("LESS THAN")
    else if(x > 10):
        out->println("GREATER THAN")
    else:
        out->println("EQUALS")

void System::main([string] args):
    f(1)
    f(10)
    f(11)
    f(1212)
    f(-1212)