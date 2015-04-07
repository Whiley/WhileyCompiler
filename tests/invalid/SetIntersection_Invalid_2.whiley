
function f({int} xs, {int} ys, {int} zs) -> bool
requires zs == (xs & {1, 2, 3}):
    return true

function h({int} ys, {int} zs) -> void:
    f(ys, zs, ys & zs)

method main() -> void:
    h({}, {})
    h({1}, {1, 2})
    h({1, 2, 3}, {3, 4, 5})
    h({1, 2}, {3, 4, 5})
