import whiley.lang.System

type etup is (int, int) | (real, real)

function f(int x) => etup:
    if x < 0:
        return (1, 2)
    else:
        return (1.2, 2.3)

public method main(System.Console sys) => void:
    (int|real x,int|real y) = f(-1)
    sys.out.println("X=" ++ x ++ ",Y=" ++ y)
    (x, y) = f(2)
    sys.out.println("X=" ++ x ++ ",Y=" ++ y)
