import println from whiley.lang.System


int f(real x) throws string:
    if x >= 0:
        return 1
    else:
        throw "error"

void ::main(System.Console sys):
    try:
        sys.out.println(Any.toString(f(1)))
        sys.out.println(Any.toString(f(0)))
        sys.out.println(Any.toString(f(-1)))
    catch(string e):
        sys.out.println("CAUGHT EXCEPTION: " + e)
    sys.out.println("DONE")        
