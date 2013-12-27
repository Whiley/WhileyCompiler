import println from whiley.lang.System

define msgType1 as {int op, [int] payload} where op == 1 
define msgType2 as {int op, int header, [int] rest} where op == 2
define msgType as msgType1 | msgType2

string f(msgType msg):
    return Any.toString(msg.op)

void ::main(System.Console sys):
    sys.out.println(f({op:1,payload:[1,2,3]}))

