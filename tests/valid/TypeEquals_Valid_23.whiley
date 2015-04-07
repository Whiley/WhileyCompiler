import whiley.lang.*

type bop is {int y, int x} where x > 0

type expr is int | bop

function f(expr e) -> int:
    if e is bop:
        return e.x + e.y
    else:
        return e + 1

method main(System.Console sys) -> void:
    x = f(1)
    sys.out.println(x)
    x = f({y: 10, x: 4})
    sys.out.println(x)
