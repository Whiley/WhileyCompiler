import * from whiley.lang.*

void f([int] ls) requires no { i in {0,1,2,3,4} | i >= 0 && i < |ls| && ls[i] < 0}:
    debug Any.toString(ls)

void ::main(System.Console sys,[string] args):
    f([-1,0,1,2,3])
