import println from whiley.lang.System

define pos as real
define neg as int
define expr as pos|neg|[int]

string f(expr e):
    if e is pos && e > 0:
        return "POSITIVE: " + Any.toString(e)
    else if e is neg:
        return "NEGATIVE: " + Any.toString(e)
    else:
        return "OTHER"

void ::main(System.Console sys):
    sys.out.println(f(-1))
    sys.out.println(f(1.0))
    sys.out.println(f(1.234))
    sys.out.println(f([1,2,3]))
 
