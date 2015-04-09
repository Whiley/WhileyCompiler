import whiley.lang.*

function f({int} xs) -> {int}:
    return xs

function g({int} ys) -> {int}:
    return f(ys & {1, 2, 3})

method main(System.Console sys) -> void:
    assume g({1, 2, 3, 4}) == {1,2,3}
    assume g({2}) == {2}
    assume g({}) == {}
