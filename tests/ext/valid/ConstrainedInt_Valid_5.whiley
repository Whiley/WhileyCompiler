import * from whiley.lang.*

// this is a comment!
define num as {1,2,3,4}

string f(num x):
    y = x
    return toString(y)

string g(int x, int z) requires (x == 1 || x == 2) && z in {1,2,3,x}:
    return f(z)

void ::main(System sys,[string] args):
    sys.out.println(g(1,2))
