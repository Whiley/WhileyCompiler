import * from whiley.lang.*

int ::g(int x):
    return x + 1

void ::f(int x) requires x > g(x):
    debug Any.toString(x)

void ::main(System.Console sys,[string] args):
    f(1)
