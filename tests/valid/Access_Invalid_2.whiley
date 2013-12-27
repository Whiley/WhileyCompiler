import println from whiley.lang.System

define liststr as [int]|string

any index(liststr l, int index):
    return l[index]

void ::main(System.Console sys):
    l = [1,2,3]
    sys.out.println(index(l,1))
    sys.out.println(index(l,2))
    l = "Hello World"
    sys.out.println(index(l,0))
    sys.out.println(index(l,2))

