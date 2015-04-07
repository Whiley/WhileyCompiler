
function f({int} xs, {int} ys, {int} zs) -> bool
requires zs == (xs + ys):
    return true

function g({int} ys) -> void:
    f(ys, ys, ys + {6})

function h({int} ys, {int} zs) -> void:
    f(ys, zs, ys + zs)

method main() -> void:
    g({})
    g({2})
    g({1, 2, 3})
    h({}, {})
    h({1}, {2})
    h({1, 2, 3}, {3, 4, 5})
