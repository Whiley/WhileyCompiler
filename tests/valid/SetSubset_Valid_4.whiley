

function f({int} xs, {int} ys) -> bool
requires xs ⊂ ys:
    return true

function g({int} xs, {int} ys) -> bool
requires xs ⊂ ys:
    return f(xs, ys)

public export method test() -> void:
    assume g({1, 2}, {1, 2, 3}) == true
    assume g({1}, {1, 2, 3}) == true
