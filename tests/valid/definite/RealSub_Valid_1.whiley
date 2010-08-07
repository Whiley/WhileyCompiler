real f(real x) requires x >= 0.2345, ensures $ < -0.2:
    return (0.0 - x)

void System::main([string] args):
    print str(f(1.234))
