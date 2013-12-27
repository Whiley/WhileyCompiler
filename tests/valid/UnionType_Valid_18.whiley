import println from whiley.lang.System

define utr12nat as int where $ >= 0
define intList as utr12nat|[int]

define tupper as {int op, intList il} where op >= 0 && op <= 5

int f(tupper y) ensures $ >= 0:
    return y.op

void ::main(System.Console sys):
    x = {op:1,il:1}
    sys.out.println(Any.toString(x))
    f(x)
