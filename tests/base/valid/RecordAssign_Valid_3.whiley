import whiley.lang.*:*

void ::main(System sys,[string] args):
    x = {f1:2,f2:3}
    y = {f1:1,f2:3}
    sys.out.println(str(x))
    sys.out.println(str(y)   )
    assert x != y
    x.f1 = 1
    sys.out.println(str(x))
    sys.out.println(str(y)  )
    assert x == y
