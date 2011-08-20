import whiley.lang.*:*

define msg as {int op, int s}

string f(msg m):
    return (str(m))

string f([int] ls):
    return (str(ls))

string f([real] ls):
    return (str(ls))

void System::main([string] args):
    this.out.println(f([1,2,3]))
    this.out.println(f([1.2,2.2,3.3]))
