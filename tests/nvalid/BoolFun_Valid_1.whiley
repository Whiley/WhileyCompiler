import println from whiley.lang.System

function f(bool b) => string:
    return Any.toString(b)

method main(System.Console sys) => void:
    x = true
    sys.out.println(f(x))
    x = false
    sys.out.println(f(x))
