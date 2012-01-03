import * from whiley.lang.*

define scf1nat as int where $ >= 0

int f(scf1nat x):
    return x

void ::main(System sys,[string] args):
    x = -1
    f(x)
