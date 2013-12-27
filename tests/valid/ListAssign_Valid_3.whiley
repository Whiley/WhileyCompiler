import println from whiley.lang.System

[[int]] update([[int]] ls):
    ls[0][0] = 10
    return ls

([[int]],[[int]]) f([[int]] ls):
    nls = update(ls)
    return ls,nls

void ::main(System.Console sys):
    ls = [[1,2,3,4]]
    ls,nls = f(ls)
    sys.out.println(Any.toString(ls))
    sys.out.println(Any.toString(nls))
