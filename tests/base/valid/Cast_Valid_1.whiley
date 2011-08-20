import whiley.lang.*:*

define R1 as { real x }

real f(int i):
    return (real) i

void System::main([string] args):
    this.out.println(str(f(1)))
    
