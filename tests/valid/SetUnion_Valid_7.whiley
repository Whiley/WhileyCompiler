

function f({int} xs) -> {int}
requires |xs| > 0:
    return xs

function g({int} ys) -> {int}:
    return f(ys + {1})

public export method test() -> void:
    assume g({}) == {1}
    assume g({2}) == {1,2}
