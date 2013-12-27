import println from whiley.lang.System

define msg1 as {int op, [int] data}
define msg2 as {int op, [{int dum}] data}

define msgType as msg1 | msg2

string f(msgType m):
    return Any.toString(m)

void ::main(System.Console sys):
    x = {op:1,data:[1,2,3]}
    sys.out.println(f(x))
    list = x.data
    sys.out.println(Any.toString(list))
    
