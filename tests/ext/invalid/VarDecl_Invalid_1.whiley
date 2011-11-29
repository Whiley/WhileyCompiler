import * from whiley.lang.*

void f(int x) requires x >= 0:
    y = 10 / x
    debug toString(x)
    debug toString(y)

void ::main(System sys,[string] args):
    f(10)
    f(0)
