
function f({int} xs, {int} ys) => void
requires xs ⊆ ys:
    debug "XS IS A SUBSET"

function g({int} xs, {int} ys) => void
requires xs ⊆ ys:
    f(ys, xs)

method main(System.Console sys) => void:
    g({1, 3}, {1, 2, 3})
    g({1}, {1, 2, 3})
