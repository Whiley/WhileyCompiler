function f({real} x, {int} ys) -> bool
requires x ⊆ ys:
    return true

method main() -> void:
    f({1.0, 2.0}, {1, 2, 3})
    f({1.0, 4.0}, {1, 2, 3})
