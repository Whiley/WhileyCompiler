

type num is (int x) where 1 <= x && x <= 4

function f(num x) -> int:
    int y = x
    return y

function g(int x, int z) -> int
requires (x == 1 || x == 2) && (z == 1 || z == 2 || z == 3 || z == x):
    return f(z)

public export method test() :
    assume g(1, 2) == 2
