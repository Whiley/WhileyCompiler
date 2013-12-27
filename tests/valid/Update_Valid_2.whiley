import println from whiley.lang.System

define liststr as [char]|string

liststr update(liststr l, int index, char value):
    l[index] = value
    return l

void ::main(System.Console sys):
    l = ['1','2','3']
    sys.out.println(update(l,1,0))
    sys.out.println(update(l,2,0))
    l = "Hello World"
    sys.out.println(update(l,1,0))
    sys.out.println(update(l,2,0))

