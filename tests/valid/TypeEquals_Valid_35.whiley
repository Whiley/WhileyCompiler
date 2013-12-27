import println from whiley.lang.System

define pos as int
define neg as int
define expr as pos|neg|[int]

string f(expr e):
    if e is pos && e > 0:
        e = e + 1
        return "POSITIVE: " + Any.toString(e)
    else:
        return "NEGATIVE: " + Any.toString(e)

void ::main(System.Console sys):
    sys.out.println(f(-1))
    sys.out.println(f(1))
    sys.out.println(f(1234))
 
