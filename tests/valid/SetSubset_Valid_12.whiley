import whiley.lang.System

function f({int} xs, {int} ys) => string:
    if xs ⊆ ys:
        return "XS IS A SUBSET"
    else:
        return "FAILED"

function g({int} xs, {int} ys) => string:
    return f(xs, ys)

method main(System.Console sys) => void:
    sys.out.println(g({1, 2, 3}, {1, 2, 3}))
    sys.out.println(g({1, 2}, {1, 2, 3}))
    sys.out.println(g({1}, {1, 2, 3}))
