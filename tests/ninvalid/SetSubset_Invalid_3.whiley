
function f({int} xs, {int} ys) => void
requires xs ⊂ ys:
    debug "X IS A SUBSET"

method main(System.Console sys) => void:
    f({1, 2}, {1, 2, 3})
    f({1, 4}, {1, 2, 3})
