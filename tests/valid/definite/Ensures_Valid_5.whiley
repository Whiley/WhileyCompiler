int f(int x) requires $ > x:
    x = x + 1
    return x

void System::main([string] args):
    int y = f(1)
    print str(y)
    