import whiley.lang.*

function f(real x) -> int:
    return 1

function f(int x) -> int:
    return 2

method main(System.Console sys) -> void:
    sys.out.println(f(1))
    sys.out.println(f(1.23))
