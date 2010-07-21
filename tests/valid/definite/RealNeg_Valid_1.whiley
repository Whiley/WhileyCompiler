real f(real x) where x > 0 && $ < 0:
    return -x

void System::main([string] args):
    print str(f(1.2))
    print str(f(0.00001))
    print str(f(5632))
