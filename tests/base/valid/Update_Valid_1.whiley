import println from whiley.lang.System

define listdict as [int]|{int=>int}

listdict update(listdict l, int index, int value):
    l[index] = value
    return l

void ::main(System.Console sys):
    l = [1,2,3]
    sys.out.println(update(l,1,0))
    sys.out.println(update(l,2,0))
    l = {1=>1,2=>2,3=>3}
    sys.out.println(update(l,1,0))
    sys.out.println(update(l,2,0))

