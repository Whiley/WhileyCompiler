int f(int x) requires x>=0 && $>=0 && x>=0:
    return x

void System::main([string] args):
    print str(f(10))
