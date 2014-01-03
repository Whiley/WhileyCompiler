import println from whiley.lang.System

function f([int] a) => string:
    a[0] = 5
    return Any.toString(a)

method main(System.Console sys) => void:
    b = [1, 2, 3]
    sys.out.println(Any.toString(b))
    sys.out.println(f(b))
    sys.out.println(Any.toString(b))
