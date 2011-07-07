real f(real x):
    return x + 1

real g(real(int) func):
    return func(1)
    
void System::main([string] args):
    out.println(str(g(&f)))