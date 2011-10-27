import * from whiley.lang.*

void f([int] ls) requires some { i in {0,1,2,3,4} | i >= 0 && i < |ls| && ls[i] < 0}:
    debug toString(ls)

void ::main(System sys,[string] args):
    f([1,2,3])
