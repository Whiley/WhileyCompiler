define R1 as { real x }

real f(int i):
    return (real) i

void System::main([string] args):
    out.println(str(f(1)))
    
