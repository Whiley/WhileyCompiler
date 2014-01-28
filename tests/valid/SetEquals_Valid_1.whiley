import whiley.lang.System

function f({int} xs, {real} ys) => string:
    if xs == ys:
        return "EQUAL"
    else:
        return "NOT EQUAL"

method g(System.Console sys, {int} xs, {real} ys) => void:
    sys.out.println(Any.toString(xs))
    sys.out.println(Any.toString(ys))
    sys.out.println(f(xs, ys))

method main(System.Console sys) => void:
    g(sys, {1, 4}, {1.0, 4.0})
    g(sys, {1, 4, 4}, {1.0, 4.0})
    g(sys, {1, 4}, {1.0, 4.2})
    g(sys, {1, 4}, {1.0, 4.2})
    g(sys, {}, {})
