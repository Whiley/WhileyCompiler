int g(int x) ensures $ > 0 && $ <= 256:
    if(x <= 0):
        return 1
    else:
        return x

[int8] f(int x):
    return [g(x)]

void System::main([string] args):
    bytes = f(256)
    debug str(bytes)

