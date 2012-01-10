import * from whiley.lang.System

void ::f(System.Console sys, [int] sys.args):
    i = 0
    do:
        i = i + 1
        sys.out.println(sys.args[i])
    while (i+1) < |sys.args|

void ::main(System.Console sys):
    f(sys,[1,2,3])
    f(sys,[1,2])
    f(sys,[1,2,3,4,5,6])

