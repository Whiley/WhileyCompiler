import whiley.lang.*

function f({int} xs, {int} ys) -> bool
requires xs ⊆ ys:
    return true

method main(System.Console sys) -> void:
    assume f({1, 2, 3}, {1, 2, 3})
    assume f({1, 2}, {1, 2, 3})
    assume f({1}, {1, 2, 3})
