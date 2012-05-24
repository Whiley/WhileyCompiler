import println from whiley.lang.System

define listdict as [int]|{int=>int}

int f(listdict ls):
    r = 0
    for l in ls:
        r = r + 1
    return r

void ::main(System.Console sys):
    ls = [1,2,3,4,5]
    sys.out.println(f(ls))
    ls = {10=>20,30=>40}
    sys.out.println(f(ls))
