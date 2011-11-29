import * from whiley.lang.*

[[int]] update([[int]] ls):
    ls[0][0] = 10
    return ls

([[int]],[[int]]) f([[int]] ls):
    nls = update(ls)
    return ls,nls

void ::main(System sys,[string] args):
    ls = [[1,2,3,4]]
    ls,nls = f(ls)
    sys.out.println(toString(ls))
    sys.out.println(toString(nls))
