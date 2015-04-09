import whiley.lang.*

function f({int} xs) -> {int}:
    return xs

method main(System.Console sys) -> void:
    assume f({}) == {}
