import whiley.lang.*

function f({int} xs) -> {int}
requires xs ⊆ {1, 2, 3}:
    return xs

function g({int} ys) -> {int}:
    return f(ys & {1, 2, 3})

method main(System.Console sys) -> void:
    sys.out.println(g({1, 2, 3, 4}))
    sys.out.println(g({2}))
    sys.out.println(g({}))
