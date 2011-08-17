real f(real x, real y) requires x >= 0.5 && y >= 0.3, ensures $ > 0.65:
    return 0.5 + x*y

void System::main([string] args):
    debug str(f(0.5,0.3))
