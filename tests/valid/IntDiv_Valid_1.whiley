import whiley.lang.*

function f(int x, int y) -> int
requires y != 0:
    return x / y

method main(System.Console sys) -> void:
    int x = f(10, 2)
    sys.out.println(x)
