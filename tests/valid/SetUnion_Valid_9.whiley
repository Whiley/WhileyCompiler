import whiley.lang.System

function f({int} xs, {int} ys, {int} zs) => string
requires zs == (xs + ys):
    return Any.toString(xs)

function g({int} ys) => string:
    return f(ys, ys, ys)

function h({int} ys, {int} zs) => string:
    return f(ys, zs, ys + zs)

method main(System.Console sys) => void:
    sys.out.println(g({}))
    sys.out.println(g({2}))
    sys.out.println(g({1, 2, 3}))
    sys.out.println(h({}, {}))
    sys.out.println(h({1}, {2}))
    sys.out.println(h({1, 2, 3}, {3, 4, 5}))
