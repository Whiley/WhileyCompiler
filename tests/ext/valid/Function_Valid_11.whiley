import whiley.lang.*:*

define msg1 as {int op, int s} where op == 1
define msg2 as {int op, int s} where op == 2

string f(msg1 m):
    return ("MSG1")

string f(msg2 m):
    return ("MSG2")

void System::main([string] args):
    m1 = {op:1,s:123}
    m2 = {op:2,s:123}
    this.out.println(f(m1))
    this.out.println(f(m2))
