

function f({int} xs, {int} ys) -> bool:
    if xs ⊆ ys:
        return true
    else:
        return false

function g({int} xs, {int} ys) -> bool:
    return f(xs, ys)

public export method test() -> void:
    assume g({1, 2, 3}, {1, 2, 3}) == true
    assume g({1, 2}, {1, 2, 3}) == true
    assume g({1}, {1, 2, 3}) == true
