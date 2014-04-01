import whiley.lang.System

type fr6nat is int

function g({fr6nat} xs) => {fr6nat}:
    return { y | y in xs, y > 1 }

function f({int} x) => string:
    return Any.toString(x)

method main(System.Console sys) => void:
    {int} ys = {-12309812, 1, 2, 2987, 2349872, 234987234987, 234987234987234}
    sys.out.println(f(g(ys)))
