define int where $ >= 0 as utr12nat
define utr12nat|[int] as intList

define (int op, intList il) where op >= 0 && op <= 5 as tupper

int f(tupper y) ensures $ >= 0:
    return y.op

void System::main([string] args):
    tupper x = (op:1,il:1)
    print str(x)
    f(x)