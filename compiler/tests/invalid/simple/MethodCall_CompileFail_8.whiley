int f():
    x = spawn 1
    return 1

void System::main([string] args):
    x = f()
    print str(x)
