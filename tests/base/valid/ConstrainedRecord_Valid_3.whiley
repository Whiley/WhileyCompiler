import whiley.lang.*:*

define btup as {int op, int index}

[int] f(btup b):        
    return [b.op,b.index]

void ::main(System sys,[string] args):
    sys.out.println(str(f({op:1,index:2})))
