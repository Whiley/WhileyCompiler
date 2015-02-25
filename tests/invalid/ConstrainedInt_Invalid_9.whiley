
type num is (int x) where x in {1, 2, 3, 4}

function f(num x) -> void:
    num y = x
    debug Any.toString(y)

function g(int x, int z) -> void
requires ((x == 0) || (x == 1)) && (z in {1, 2, 3, x}):
    f(z)

method main(System.Console sys) -> void:
    g(0, 0)
