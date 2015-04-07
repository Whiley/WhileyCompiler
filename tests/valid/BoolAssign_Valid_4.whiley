import whiley.lang.*

function f(int x, int y) -> int:
    bool a = x == y
    if a:
        return 1
    else:
        return x + y

function g(int x, int y) -> int:
    bool a = x >= y
    if !a:
        return x + y
    else:
        return 1

method main(System.Console sys) -> void:
    sys.out.println(f(1, 1))
    sys.out.println(f(0, 0))
    sys.out.println(f(4, 345))
    sys.out.println(g(1, 1))
    sys.out.println(g(0, 0))
    sys.out.println(g(4, 345))
