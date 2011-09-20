import * from whiley.lang.*

define msg1 as {int op, [int] data}
define msg2 as {int op, [{int dum}] data}

define msgType as msg1 | msg2

string f(msgType m):
    return str(m)

void ::main(System sys,[string] args):
    x = {op:1,data:[1,2,3]}
    sys.out.println(f(x))
    list = x.data
    sys.out.println(str(list))
    
