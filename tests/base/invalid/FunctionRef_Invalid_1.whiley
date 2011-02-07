int f(int x):
    return x + 1

int g(void(int) func):
    return func(1)
    
void System::main([string] args):
    out->println(str(g(&f)))