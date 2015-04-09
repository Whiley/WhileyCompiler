import whiley.lang.*

function f({int} xs) -> {int}:
    return xs

method main(System.Console sys) -> void:
    assume f({1, 4}) == {1,4}
    assume f({}) == {}
    assume f({}) == {}
