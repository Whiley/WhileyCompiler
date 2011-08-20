import whiley.lang.*:*

define R1 as { real x }

int f(real i):
    return (int) i

void System::main([string] args):
    this.out.println(str(f(1.01)))
    
