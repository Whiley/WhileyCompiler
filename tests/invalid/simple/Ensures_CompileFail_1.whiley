int f() requires 2*$==1:
    return 1

void System::main([string] args):
    print str(f())
