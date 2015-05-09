
type num is (int x) where x in {1, 2, 3, 4}

function f(num x) -> int:
    num y = x
    return y

function g(int x, int z) -> int
requires ((x == 0) || (x == 1)) && (z in {1, 2, 3, x}):
    return f(z)
