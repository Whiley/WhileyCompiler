import * from whiley.lang.*

void f(int x) requires x >= 0:
    y = 10 / x
    debug Any.toString(x)
    debug Any.toString(y)

void ::main(System.Console sys,[string] args):
    f(10)
    f(0)
