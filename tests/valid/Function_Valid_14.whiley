import whiley.lang.System

function f(int x) => string:
    return "F(INT)"

function f(real y) => string:
    return "F(REAL)"

function f([int] xs) => string:
    return "F([int])"

function f({int} xs) => string:
    return "F({int})"

method main(System.Console sys) => void:
    sys.out.println(f(1.234))
    sys.out.println(f(1))
    sys.out.println(f([1, 2, 3]))
    sys.out.println(f({1, 2, 3}))
