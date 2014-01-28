import whiley.lang.System

method main(System.Console sys) => void:
    {int f1, int f2} x = {f1: 2, f2: 3}
    {int f1, int f2} y = {f1: 1, f2: 3}
    sys.out.println(Any.toString(x))
    sys.out.println(Any.toString(y))
    assert x != y
    x.f1 = 1
    sys.out.println(Any.toString(x))
    sys.out.println(Any.toString(y))
    assert x == y
