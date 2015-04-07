import whiley.lang.*

function f({int} xs, {int} ys, {int} zs) -> bool:
    if zs == (xs + ys):
        return true
    else:
        return false

function g({int} ys) -> bool:
    return f(ys, ys, ys)

function h({int} ys, {int} zs) -> bool:
    return f(ys, zs, ys + zs)

method main(System.Console sys) -> void:
    sys.out.println(g({}))
    sys.out.println(g({2}))
    sys.out.println(g({1, 2, 3}))
    sys.out.println(h({}, {}))
    sys.out.println(h({1}, {2}))
    sys.out.println(h({1, 2, 3}, {3, 4, 5}))
