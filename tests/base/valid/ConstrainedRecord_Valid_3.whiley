import println from whiley.lang.System

define btup as {int op, int index}

[int] f(btup b):        
    return [b.op,b.index]

void ::main(System.Console sys):
    sys.out.println(Any.toString(f({op:1,index:2})))
