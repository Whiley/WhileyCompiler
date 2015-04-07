import whiley.lang.*

function f({int} xs, {int} ys) -> bool:
    if xs ⊆ ys:
        return true
    else:
        return false

function g({int} xs, {int} ys) -> bool:
    return f(xs, ys)

method main(System.Console sys) -> void:
    sys.out.println(g({1, 2, 3}, {1, 2, 3}))
    sys.out.println(g({1, 2}, {1, 2, 3}))
    sys.out.println(g({1}, {1, 2, 3}))
