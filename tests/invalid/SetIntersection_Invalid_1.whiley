
function f({int} xs) -> void
requires xs ⊆ {1, 2, 3}:
    debug Any.toString(xs)

function g({int} ys) -> void:
    f(ys & {1, 2, 3, 4})

method main(System.Console sys) -> void:
    g({1, 2, 3, 4})
    g({2})
    g({})
