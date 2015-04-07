import whiley.lang.*

type fr5nat is (int x) where x >= 0

function g({fr5nat} xs) -> {fr5nat}:
    {fr5nat} ys = {}
    for y in xs:
        if y > 1:
            ys = ys + {y}
    return ys

function f({fr5nat} x) -> {int}:
    return x

method main(System.Console sys) -> void:
    {int} ys = {1, 2, 3}
    sys.out.println(f(g(ys)))
