define utr12nat as int requires $ >= 0
define intList as utr12nat|[int]

define tupper as (int op, intList il) requires op >= 0 && op <= 5

int f(tupper y) requires $ >= 0:
    return y.op

void System::main([string] args):
    tupper x = (op:1,il:1)
    print str(x)
    f(x)