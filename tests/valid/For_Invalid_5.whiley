import println from whiley.lang.System

define liststr as [int]|string

int f(liststr ls):
    r = 0
    for l in ls:
        r = r + l
    return r

void ::main(System.Console sys):
    ls = [1,2,3,4,5,6,7,8]
    sys.out.println(f(ls))
    ls = "Hello World"
    sys.out.println(f(ls))
