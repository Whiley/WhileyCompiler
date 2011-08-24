import whiley.lang.*:*
    
define Contractive as Contractive|null

Contractive f(Contractive x):
    return x

void System::main([string] args):
    x = f(null)
    this.out.println(str(x))
