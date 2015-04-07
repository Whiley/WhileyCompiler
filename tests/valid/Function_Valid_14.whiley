import whiley.lang.*

function f(int x) -> int:
    return 1

function f(real y) -> int:
    return 2

function f([int] xs) -> int:
    return 3

function f({int} xs) -> int:
    return 4

method main(System.Console sys) -> void:
    sys.out.println(f(1))
    sys.out.println(f(1.234))
    sys.out.println(f([1, 2, 3]))
    sys.out.println(f({1, 2, 3}))
