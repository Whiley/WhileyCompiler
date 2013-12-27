import println from whiley.lang.System

define pos as real where $ > 0
define neg as int where $ < 0
define expr as pos|neg

string f(expr e):
    if e is pos:
        return "POSITIVE: " + Any.toString(e)
    else:
        return "NEGATIVE: " + Any.toString(e)

void ::main(System.Console sys):
    sys.out.println(f(-1))
    sys.out.println(f(1.0))
    sys.out.println(f(1234.0))
 
