import println from whiley.lang.System

define fcode as {1,2,3,4}
define tcode as {1,2}

string g(fcode f):
    return Any.toString(f)

void ::main(System.Console sys):
    x = 1
    sys.out.println(g(x))
