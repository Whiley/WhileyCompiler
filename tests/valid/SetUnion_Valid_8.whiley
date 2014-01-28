import whiley.lang.System

function f({int} xs) => string:
    if |xs| > 0:
        return Any.toString(xs)
    else:
        return "FAILED"

function g({int} ys) => string:
    return f(ys + {1})

method main(System.Console sys) => void:
    sys.out.println(g({}))
    sys.out.println(g({2}))
