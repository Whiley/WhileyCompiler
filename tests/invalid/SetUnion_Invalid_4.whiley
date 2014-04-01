
function f({int} xs, {int} ys, {int} zs) => void
requires zs == (xs + ys):
    debug Any.toString(xs)

function g({int} ys) => void:
    f(ys, ys, ys + {6})

function h({int} ys, {int} zs) => void:
    f(ys, zs, ys + zs)

method main(System.Console sys) => void:
    g({})
    g({2})
    g({1, 2, 3})
    h({}, {})
    h({1}, {2})
    h({1, 2, 3}, {3, 4, 5})
