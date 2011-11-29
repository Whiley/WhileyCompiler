import * from whiley.lang.*

int f(int x):
    return x

void ::main(System sys,[string] args):
    x = 1
    x = f(x + 2.3)
