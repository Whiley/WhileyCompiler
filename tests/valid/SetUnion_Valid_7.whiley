import whiley.lang.*

function f({int} xs) -> {int}
requires |xs| > 0:
    return xs

function g({int} ys) -> {int}:
    return f(ys + {1})

method main(System.Console sys) -> void:
    sys.out.println(g({}))
    sys.out.println(g({2}))
