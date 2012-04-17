import println from whiley.lang.System

int f(int x) throws string|int:
    if x > 0:
        return 1
    else if x == -1:
        throw "error"
    else:
        throw x

void ::g(System.Console sys, int x):
    try:
        f(x)
    catch(string e):
        sys.out.println("CAUGHT EXCEPTION (string): " + e)
    catch(int e):
        sys.out.println("CAUGHT EXCEPTION (int): " + Any.toString(e))
    

void ::main(System.Console sys):
    g(sys,1)
    g(sys,0)
    g(sys,-1)
