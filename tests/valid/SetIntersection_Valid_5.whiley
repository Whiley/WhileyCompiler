import whiley.lang.System

function f({int} xs) -> ASCII.string
requires |xs| < 3:
    return Any.toString(xs)

function g({int} ys) -> ASCII.string:
    return f(ys & {1, 2})

method main(System.Console sys) -> void:
    sys.out.println(g({}))
    sys.out.println(g({2, 3, 4, 5, 6}))
    sys.out.println(g({2, 6}))
