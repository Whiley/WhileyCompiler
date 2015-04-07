import whiley.lang.*

type tac1tup is {int f1, int f2} where f1 < f2

function f() -> tac1tup:
    return {f1: 1, f2: 3}

method main(System.Console sys) -> void:
    tac1tup x = f()
    x.f1 = x.f2 - 2
    assert x.f1 != x.f2
    sys.out.println(x)
