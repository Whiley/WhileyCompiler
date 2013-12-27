import println from whiley.lang.System

define setdict as {int}|{int=>int}

int len(setdict l):
    return |l|

void ::main(System.Console sys):
    l = {1,2,3}
    sys.out.println(len(l))
    l = {1=>2,3=>4,5=>6,7=>8}
    sys.out.println(len(l))
