import whiley.lang.*

type C1 is null | any

type C2 is null | any

type C3 is C1 | C2

method f(C1 x) -> C3:
    return x

method g(C2 x) -> C3:
    return x

method h(C3 x) -> C1:
    return x

method i(C3 x) -> C2:
    return x

method main(System.Console sys) -> void:
    C3 x = f(null)
    sys.out.println(x)
