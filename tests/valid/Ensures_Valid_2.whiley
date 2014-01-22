import println from whiley.lang.System

function f(int x) => int
ensures $ > x:
    x = x + 1
    return x

method main(System.Console sys) => void:
    y = f(1)
    sys.out.println(Any.toString(y))
