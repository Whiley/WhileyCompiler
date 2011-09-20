import * from whiley.lang.*

define msgType1 as {int op, [int] payload} where op == 1 
define msgType2 as {int op, int header, [int] rest} where op == 2
define msgType as msgType1 | msgType2

string f(msgType msg):
    return str(msg.op)

void ::main(System sys,[string] args):
    sys.out.println(f({op:1,payload:[1,2,3]}))

