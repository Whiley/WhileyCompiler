

type fr6nat is int

function g({fr6nat} xs) -> {fr6nat}:
    {fr6nat} ys = {}
    for y in xs:
        if y > 1:
            ys = ys + {y}
    return ys

function f({int} x) -> {int}:
    return x

public export method test() -> void:
    {int} ys = {-12309812, 1, 2, 2987, 2349872, 234987234987, 234987234987234}
    assume f(g(ys)) == {2, 2987, 2349872, 234987234987, 234987234987234}

