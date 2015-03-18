import whiley.lang.System

function f(int x) -> ASCII.string:
    return "F(INT)"

function f(real y) -> ASCII.string:
    return "F(REAL)"

function f([int] xs) -> ASCII.string:
    return "F([int])"

function f({int} xs) -> ASCII.string:
    return "F({int})"

method main(System.Console sys) -> void:
    sys.out.println_s(f(1.234))
    sys.out.println_s(f(1))
    sys.out.println_s(f([1, 2, 3]))
    sys.out.println_s(f({1, 2, 3}))
