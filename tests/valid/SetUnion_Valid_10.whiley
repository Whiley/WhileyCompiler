import whiley.lang.System

function f({int} xs, {int} ys, {int} zs) -> ASCII.string:
    if zs == (xs + ys):
        return Any.toString(xs)
    else:
        return "FAILED"

function g({int} ys) -> ASCII.string:
    return f(ys, ys, ys)

function h({int} ys, {int} zs) -> ASCII.string:
    return f(ys, zs, ys + zs)

method main(System.Console sys) -> void:
    sys.out.println_s(g({}))
    sys.out.println_s(g({2}))
    sys.out.println_s(g({1, 2, 3}))
    sys.out.println_s(h({}, {}))
    sys.out.println_s(h({1}, {2}))
    sys.out.println_s(h({1, 2, 3}, {3, 4, 5}))
