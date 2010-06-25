int f():
    process int x
    x = spawn 1
    return 1

void System::main([string] args):
    int x = f()
    print str(x)
