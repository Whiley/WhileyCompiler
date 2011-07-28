define R1 as { real x }

int f(real i):
    return (int) i

void System::main([string] args):
    out.println(str(f(1.01)))
    
