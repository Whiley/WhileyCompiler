define R2 as { real x }
define R1 as { int x }

R1 f(R2 i):
    return (R1) i

void System::main([string] args):
    this.out.println(str(f({x:123542.12})))
    
