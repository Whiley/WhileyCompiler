import whiley.lang.System

function f({int} xs) => string:
    return Any.toString(xs)

method main(System.Console sys) => void:
    sys.out.println(f({}))
