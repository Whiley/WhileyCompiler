import whiley.lang.*

function f({int} xs) -> {int}:
    return xs

method main(System.Console sys) -> void:
    {int} ys = {1, 2, 3}
    {int} zs = ys
    assume f(zs) == {1,2,3}
