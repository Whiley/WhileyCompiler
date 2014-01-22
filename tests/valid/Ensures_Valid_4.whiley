import println from whiley.lang.System

function f(int x) => int
requires x >= 0
ensures ($ >= 0) && (x >= 0):
    return x

method main(System.Console sys) => void:
    sys.out.println(Any.toString(f(10)))
