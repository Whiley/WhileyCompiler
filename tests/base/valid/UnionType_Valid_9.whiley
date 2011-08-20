import whiley.lang.*:*

// this is a comment!
define IntList as {int|[int] op}

void System::main([string] args):
    x = {op:1}
    x.op = 1
    y = x // OK
    this.out.println(str(y))
    x = {op:[1,2,3]} // OK
    this.out.println(str(x))
