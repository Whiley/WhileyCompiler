import * from whiley.lang.*

int f(int x) requires x+1 > 0, ensures $ < 0:
    debug Any.toString(x)
    return -1

void ::main(System sys,[string] args):
    f(|args|-1)
