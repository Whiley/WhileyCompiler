int f(int|bool x):
    if x is int:
        return x
    else:
        return 1 

void System::main([string] args):
    this.out.println(str(f(true)))
    this.out.println(str(f(123)))
