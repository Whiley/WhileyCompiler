import whiley.lang.*

method main(System.Console sys) -> void:
    {int f1, int f2} x = {f1: 2, f2: 3}
    {int f1, int f2} y = {f1: 1, f2: 3}
    sys.out.println(x)
    sys.out.println(y)
    assert x != y
    x.f1 = 1
    sys.out.println(x)
    sys.out.println(y)
    assert x == y
