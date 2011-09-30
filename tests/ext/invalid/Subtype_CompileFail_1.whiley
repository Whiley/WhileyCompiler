import * from whiley.lang.*

define scf1nat as int where $ >= 0

int f(sc1nat x):
    return x

void ::main(System sys,[string] args):
    x = -1
    f(x)
