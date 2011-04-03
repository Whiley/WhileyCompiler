int f(int x):
    if x > 0:
        skip
    else:
        return -1
    return x

void System::main([string] args):
    out->println(str(f(1)))
    out->println(str(f(-10)))
