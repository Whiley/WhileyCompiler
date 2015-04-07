import whiley.lang.*

type nat is (int n) where n >= 0

type T is int | [int]

function f(T x) -> int:
    if x is [int] | nat:
        return 0
    else:
        return x

method main(System.Console sys) -> void:
    sys.out.println(f(1))
    sys.out.println(f(-1))
    sys.out.println(f([1, 2, 3]))
