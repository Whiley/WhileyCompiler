import * from whiley.lang.*

int f(int x) throws string|int:
    if x >= 0:
        return 1
    else if x == -1:
        throw "error"
    else:
        throw x

void ::missed(System sys,int x) throws string:
    try:
        f(x)
    catch(int e):
        sys.out.println("CAUGHT EXCEPTION (int): " + String.toString(e))

void ::main(System sys,[string] args):
    try:
        missed(sys,1)
        missed(sys,-2)
        missed(sys,-1)
    catch(string e):
        sys.out.println("CAUGHT EXCEPTION (string): " + e)
