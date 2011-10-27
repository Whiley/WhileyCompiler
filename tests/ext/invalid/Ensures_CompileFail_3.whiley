import * from whiley.lang.*

int ::g(int x):
    return x + 1

void ::f(int x) requires x > g(x):
    debug toString(x)

void ::main(System sys,[string] args):
    f(1)
