import * from whiley.lang.*

string g(int z):
    return toString(z)

string f(int x):
    y = x + 1
    return g(y)

void ::main(System sys,[string] args):
    sys.out.println(f(1))
