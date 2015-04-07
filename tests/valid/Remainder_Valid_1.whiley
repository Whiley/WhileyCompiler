import whiley.lang.*

function f(int x, int y) -> int:
    return x % y

method main(System.Console sys) -> void:
    sys.out.println(f(10, 5))
    sys.out.println(f(10, 4))
    sys.out.println(f(1, 4))
    sys.out.println(f(103, 2))
    sys.out.println(f(-10, 5))
    sys.out.println(f(-10, 4))
    sys.out.println(f(-1, 4))
    sys.out.println(f(-103, 2))
    sys.out.println(f(-10, -5))
    sys.out.println(f(-10, -4))
    sys.out.println(f(-1, -4))
    sys.out.println(f(-103, -2))
    sys.out.println(f(10, -5))
    sys.out.println(f(10, -4))
    sys.out.println(f(1, -4))
    sys.out.println(f(103, -2))
