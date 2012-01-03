import * from whiley.lang.*

void ::main(System sys,[string] args):
    x = {f1:2,f2:3}
    y = {f1:1,f2:3}
    sys.out.println(Any.toString(x))
    sys.out.println(Any.toString(y)   )
    assert x != y
    x.f1 = 1
    sys.out.println(Any.toString(x))
    sys.out.println(Any.toString(y)  )
    assert x == y
