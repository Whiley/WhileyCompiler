import println from whiley.lang.System

int f(int x) throws string|int:
    if x >= 0:
        return 1
    else if x == -1:
        throw "error"
    else:
        throw x

void ::missed(System.Console sys,int x) throws string:
    try:
        f(x)
    catch(int e):
        sys.out.println("CAUGHT EXCEPTION (int): " + Any.toString(e))

void ::main(System.Console sys):
    try:
        missed(sys,1)
        missed(sys,-2)
        missed(sys,-1)
    catch(string e):
        sys.out.println("CAUGHT EXCEPTION (string): " + e)
