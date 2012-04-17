import println from whiley.lang.System

define tenup as int
define msg1 as {tenup op, [int] data}
define msg2 as {int index}

define msgType as msg1 | msg2

string f(msgType m):
    return Any.toString(m)

void ::main(System.Console sys):
    m1 = {op:11,data:[]}
    m2 = {index:1}
    sys.out.println(f(m1))
    sys.out.println(f(m2))
