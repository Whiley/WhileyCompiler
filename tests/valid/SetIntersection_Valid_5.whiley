

function f({int} xs) -> {int}
requires |xs| < 3:
    return xs

function g({int} ys) -> {int}:
    return f(ys & {1, 2})

public export method test() -> void:
    assume g({}) == {}
    assume g({2, 3, 4, 5, 6}) == {2}
    assume g({2, 6}) == {2}
