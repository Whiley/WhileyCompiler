int g(int x) ensures $ > 0 && $ <= 256:
    if(x <= 0):
        return 1
    else:
        return x

[byte] f(int x):
    return [g(x)]

void System::main([string] args):
    [byte] bytes = f(256)
    print str(bytes)

