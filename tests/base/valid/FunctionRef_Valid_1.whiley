int f1(int x):
    return x + 1

int f2(int x):
    return x * 2

int g(int(int) func):
    return func(1234)
    
void System::main([string] args):
    this.out.println(str(g(&f1)))
    this.out.println(str(g(&f2)))