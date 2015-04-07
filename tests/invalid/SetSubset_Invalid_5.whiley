function f({int} xs, {int} ys) -> bool
requires xs ⊆ ys:
    return true

function g({int} xs, {int} ys)
requires xs ⊆ ys:
    f(ys, xs)

method main() -> void:
    g({1, 3}, {1, 2, 3})
    g({1}, {1, 2, 3})
