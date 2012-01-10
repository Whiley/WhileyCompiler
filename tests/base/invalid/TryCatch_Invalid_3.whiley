import * from whiley.lang.*


int f(real x) throws string:
    if x >= 0:
        return 1
    else:
        throw "error"

void ::main(System.Console sys,[string] args):
    if |args| > 0:
        x = 1
    try:
        sys.out.println(Any.toString(f(1)))
        sys.out.println(Any.toString(f(0)))
        sys.out.println(Any.toString(f(-1)))
    catch(int e):
        sys.out.println("CAUGHT EXCEPTION: " + x)
    sys.out.println("DONE")        