int f(int x) where x > 0 && $ < 0:
    print str(x)
    return -1

void System::main([string] args):
    f(|args|-1)
