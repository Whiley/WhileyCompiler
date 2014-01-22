import println from whiley.lang.System

function f(int x, int y) => int
requires (x >= 0) && (y >= 0)
ensures $ > 0:
    a = true
    if x < y:
        a = false
    if !a:
        return x + y
    else:
        return 123

method main(System.Console sys) => void:
    sys.out.println(Any.toString(1))
