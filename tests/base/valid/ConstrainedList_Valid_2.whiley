int g(int x):
    if(x <= 0 || x >= 125):
        return 1
    else:
        return x

[int] f(int x):
    return [g(x)]

void System::main([string] args):
    bytes = f(0)
    this.out.println(str(bytes))

