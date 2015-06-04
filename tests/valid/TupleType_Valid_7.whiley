

type etup is (int, int) | (real, real)

function f(int x) -> etup:
    if x < 0:
        return (1, 2)
    else:
        return (1.2, 2.3)

function g(int x) -> (int | real, int | real):
    return f(x)

public export method test() -> void:
    (int|real x, int|real y) = g(-1)
    assume x == 1 && y == 2
    (x, y) = g(2)
    assume x == 1.2 && y == 2.3
