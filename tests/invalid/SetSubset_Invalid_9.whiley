
function f({int} xs, {int} ys) -> void
requires xs ⊂ ys:
    debug "XS IS A SUBSET"

function g({int} xs, {int} ys) -> void:
    f(xs, ys)

method main() -> void:
    g({1}, {1, 2, 3})
    g({1, 4}, {1, 2, 3})
