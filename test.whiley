int f(int x) ensures $ > 0:
    return x

void System::main([string] args):
    f(-1)
