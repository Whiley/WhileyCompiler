import * from whiley.lang.*

define btup as {int op, int index}

[int] f(btup b):        
    return [b.op,b.index]

void ::main(System.Console sys,[string] args):
    sys.out.println(Any.toString(f({op:1,index:2})))
