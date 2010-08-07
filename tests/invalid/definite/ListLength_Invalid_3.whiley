int f(int x) requires x+1 > 0 && $ < 0:
    print str(x)
    return -1

void System::main([string] args):
    f(|args|-1)
