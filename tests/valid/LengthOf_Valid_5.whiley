import println from whiley.lang.System

define liststr as [int]|string

int len(liststr l):
    return |l|

void ::main(System.Console sys):
    l = [1,2]
    sys.out.println(len(l))
    l = "Hello World"
    sys.out.println(len(l))
