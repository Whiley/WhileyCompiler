import whiley.lang.System

type fr5nat is int

function g({fr5nat} xs) => {fr5nat}:
    {fr5nat} ys = {}
    for y in xs:
        if y > 1:
            ys = ys + {y}
    return ys

function f({fr5nat} x) => string:
    return Any.toString(x)

method main(System.Console sys) => void:
    {int} ys = {1, 2, 3}
    sys.out.println(f(g(ys)))
