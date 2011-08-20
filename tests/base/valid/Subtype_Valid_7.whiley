import whiley.lang.*:*

define sr7nat as int

void System::main([string] args):
    x = {f:1}
    x.f = x.f + 1
    this.out.println(str(x))
    
