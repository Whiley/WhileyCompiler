

type bop is {int y, int x} where x > 0

type expr is int | bop

function f(expr e) -> int:
    if e is bop:
        return e.x + e.y
    else:
        return e + 1

public export method test() :
    x = f(1)
    assume x == 2
    x = f({y: 10, x: 4})
    assume x == 14
