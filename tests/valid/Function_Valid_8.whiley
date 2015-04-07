import whiley.lang.*

type fr6nat is (int x) where x >= 0

function g({fr6nat} xs) -> {fr6nat}:
    {fr6nat} ys = {}
    for y in xs:
        if y > 1:
            ys = ys + {y}
    return ys

function f({int} x) -> {int}:
    return x

method main(System.Console sys) -> void:
    {int} ys = {1, 2, 3}
    sys.out.println(f(g(ys)))
