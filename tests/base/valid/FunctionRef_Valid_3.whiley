import whiley.lang.*:*

int f(int x):
    return x * 12

real g(real(int) func):
    return func(1)
    
void System::main([string] args):
    this.out.println(str(g(&f)))