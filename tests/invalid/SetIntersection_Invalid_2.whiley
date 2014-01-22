
function f({int} xs, {int} ys, {int} zs) => void
requires zs == (xs & {1, 2, 3}):
    debug Any.toString(xs)

function h({int} ys, {int} zs) => void:
    f(ys, zs, ys & zs)

method main(System.Console sys) => void:
    h({}, {})
    h({1}, {1, 2})
    h({1, 2, 3}, {3, 4, 5})
    h({1, 2}, {3, 4, 5})
