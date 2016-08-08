type num is (int x) where x == 1 || x == 2 || x == 3 || x == 4

function f(num x) -> int:
    num y = x
    return y

function g(int x, int z) -> int
requires ((x == 0) || (x == 1)) && (z == 1 || z == 2 || z == 3 || z == x):
    return f(z)
