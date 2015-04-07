import whiley.lang.*

function f({int} xs) -> bool:
    if |xs| == 1:
        return true
    else:
        return false

function g({int} ys) -> bool:
    return f(ys + {1})

method main(System.Console sys) -> void:
    sys.out.println(g({}))
    sys.out.println(g({2}))
