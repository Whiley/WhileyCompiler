import println from whiley.lang.System

void ::f(System.Console sys, [int] args):
    i = 0
    do:
        i = i + 1
        sys.out.println(args[i])
    while (i+1) < |args|

void ::main(System.Console sys):
    f(sys,[1,2,3])
    f(sys,[1,2])
    f(sys,[1,2,3,4,5,6])

