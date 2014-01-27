import println from whiley.lang.System

type etup is (int, int) | (real, real)

function f(int x) => etup:
    if x < 0:
        return (1, 2)
    else:
        return (1.2, 2.3)

function g(int x) => (int | real, int | real):
    return f(x)

public method main(System.Console sys) => void:
    (x, y) = g(-1)
    sys.out.println("X=" ++ x ++ ",Y=" ++ y)
    (x, y) = g(2)
    sys.out.println("X=" ++ x ++ ",Y=" ++ y)
