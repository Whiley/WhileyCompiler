import * from whiley.lang.*

real g(int x):
     return x / 3.123

string f(int x, int y):
    return toString(g(x))

void ::main(System sys,[string] args):
     sys.out.println(f(1,2))
