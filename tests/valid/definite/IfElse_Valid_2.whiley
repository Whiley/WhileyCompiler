int f(int x):
    if(x < 10):
        return 1
    else if(x > 10):
        return 2
    return 0

void System::main([string] args):
    print str(f(1))
    print str(f(10))
    print str(f(11))
    print str(f(1212))
    print str(f(-1212))
