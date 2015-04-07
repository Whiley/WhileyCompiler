import whiley.lang.*

function f({int} xs, {int} ys, {int} zs) -> {int}
requires zs == (xs & ys):
    return xs

function g({int} ys) -> {int}:
    return f(ys, ys, ys)

function h({int} ys, {int} zs) -> {int}:
    return f(ys, zs, ys & zs)

method main(System.Console sys) -> void:
    sys.out.println(g({}))
    sys.out.println(g({2}))
    sys.out.println(g({1, 2, 3}))
    sys.out.println(h({}, {}))
    sys.out.println(h({1}, {1, 2}))
    sys.out.println(h({1, 2, 3}, {3, 4, 5}))
    sys.out.println(h({1, 2}, {3, 4, 5}))
