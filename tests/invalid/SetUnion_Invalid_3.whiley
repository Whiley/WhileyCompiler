
function f({int} xs) -> bool
requires |xs| > 0:
    return true

function g({int} ys, {int} zs) -> void:
    f(ys + zs)

method main() -> void:
    g({}, {1})
    g({2}, {2})
    g({}, {})
