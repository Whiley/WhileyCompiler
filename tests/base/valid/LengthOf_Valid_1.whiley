import println from whiley.lang.System

define listset as [int]|{int}

int len(listset l):
    return |l|

void ::main(System.Console sys):
    l = [1,2,3]
    sys.out.println(len(l))
    l = {1,2,3,4,5,6}
    sys.out.println(len(l))
