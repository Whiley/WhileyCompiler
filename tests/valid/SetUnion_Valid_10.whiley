

function f({int} xs, {int} ys, {int} zs) -> bool:
    if zs == (xs + ys):
        return true
    else:
        return false

function g({int} ys) -> bool:
    return f(ys, ys, ys)

function h({int} ys, {int} zs) -> bool:
    return f(ys, zs, ys + zs)

public export method test() -> void:
    assume g({}) == true
    assume g({2}) == true
    assume g({1, 2, 3}) == true
    assume h({}, {}) == true
    assume h({1}, {2}) == true
    assume h({1, 2, 3}, {3, 4, 5}) == true
