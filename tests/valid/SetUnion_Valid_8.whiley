import whiley.lang.System

function f({int} xs) -> ASCII.string:
    if |xs| > 0:
        return Any.toString(xs)
    else:
        return "FAILED"

function g({int} ys) -> ASCII.string:
    return f(ys + {1})

method main(System.Console sys) -> void:
    sys.out.println_s(g({}))
    sys.out.println_s(g({2}))
