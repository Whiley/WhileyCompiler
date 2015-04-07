import whiley.lang.*

type num is (int x) where x in {1, 2, 3, 4}

function f(num x) -> int:
    int y = x
    return y

function g(int x, int z) -> int
requires ((x == 1) || (x == 2)) && (z in {1, 2, 3, x}):
    return f(z)

method main(System.Console sys) -> void:
    sys.out.println(g(1, 2))
