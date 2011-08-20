import whiley.lang.*:*

define Tup1 as (int, int)
define Tup2 as (real, real)

Tup2 f(Tup1 x):
    return x

void System::main([string] args):
    x = f((1,2))
    this.out.println(str(x))
