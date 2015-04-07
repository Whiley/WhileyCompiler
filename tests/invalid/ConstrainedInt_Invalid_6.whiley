type c3num is (int x) where 1 <= x && x <= 4

function f(c3num x) -> int:
    int y = x
    return y

function g(int z) -> int:
    return f(z)
