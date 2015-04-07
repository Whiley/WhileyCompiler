
function f({int} xs, {int} ys) -> bool
requires xs ⊆ ys:
    return true

method main() -> void:
    f({1, 2}, {1, 2, 3})
    f({1, 4}, {1, 2, 3})
