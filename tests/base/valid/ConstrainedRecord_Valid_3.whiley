import whiley.lang.*:*

define btup as {int op, int index}

[int] f(btup b):        
    return [b.op,b.index]

void System::main([string] args):
    this.out.println(str(f({op:1,index:2})))
