import whiley.lang.*:*

define sr6nat as int
define sr6tup as {sr6nat f, int g}

void System::main([string] args):
    x = {f:1,g:5}
    x.f = 2
    this.out.println(str(x))
    
