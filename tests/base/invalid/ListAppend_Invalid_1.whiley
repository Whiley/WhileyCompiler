import * from whiley.lang.*

int f([string] r):
    return |r|
 
void ::main(System sys,[string] args):
    r = args + [1]
    f(r)
    sys.out.println(toString(r))
