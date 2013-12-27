import println from whiley.lang.System

define listset as [int]|{int}

int f(listset ls):
    r = 0
    for l in ls:
        r = r + l
    return r

void ::main(System.Console sys):
    ls = [1,2,3,4,5]
    sys.out.println(f(ls))
    ls = {10,20,30,40,50}
    sys.out.println(f(ls))
