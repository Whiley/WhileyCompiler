import * from whiley.lang.*


int f(int x) throws string:
    if x >= 0:
        return 1
    else:
        throw "error"

int g(int x):
    return x

void ::main(System.Console sys,[string] args):
    x = 1
    try:
        sys.out.println(Any.toString(f(1)))
        x = 1.02
        sys.out.println(Any.toString(f(0)))
        sys.out.println(Any.toString(f(-1)))
    catch(int e):
        sys.out.println("CAUGHT EXCEPTION: " + g(x))
    sys.out.println("DONE")        