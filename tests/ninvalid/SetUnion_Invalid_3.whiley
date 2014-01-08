
function f({int} xs) => void
requires |xs| > 0:
    debug Any.toString(xs)

function g({int} ys, {int} zs) => void:
    f(ys + zs)

method main(System.Console sys) => void:
    g({}, {1})
    g({2}, {2})
    g({}, {})
