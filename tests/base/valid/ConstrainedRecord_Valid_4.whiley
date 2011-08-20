import whiley.lang.*:*

define point as {int x, int y}

void System::main([string] args):
    p = {x:1,y:1}
    this.out.println(str(p))
