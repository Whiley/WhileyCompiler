import * from whiley.lang.*

define fcode as {1,2,3,4}
define tcode as {1,2}

string g(fcode f):
    return Any.toString(f)

void ::main(System.Console sys,[string] args):
    x = 1
    sys.out.println(g(x))
