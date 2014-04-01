import whiley.lang.System

type fr6nat is (int x) where x >= 0

function g({fr6nat} xs) => {fr6nat}:
    return { y | y in xs, y > 1 }

function f({int} x) => string:
    return Any.toString(x)

method main(System.Console sys) => void:
    {int} ys = {1, 2, 3}
    sys.out.println(f(g(ys)))
