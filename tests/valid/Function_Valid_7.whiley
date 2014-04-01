import whiley.lang.System

type fr5nat is int

function g({fr5nat} xs) => {fr5nat}:
    return { y | y in xs, y > 1 }

function f({fr5nat} x) => string:
    return Any.toString(x)

method main(System.Console sys) => void:
    {int} ys = {1, 2, 3}
    sys.out.println(f(g(ys)))
