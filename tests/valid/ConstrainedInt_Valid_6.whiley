import whiley.lang.System

type num is (int x) where x in {1, 2, 3, 4}

function f(num x) -> ASCII.string:
    int y = x
    return Any.toString(y)

function g(int x, int z) -> ASCII.string
requires ((x == 1) || (x == 2)) && (z in {1, 2, 3, x}):
    return f(z)

method main(System.Console sys) -> void:
    sys.out.println(g(1, 2))
