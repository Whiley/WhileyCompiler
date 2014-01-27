import println from whiley.lang.System

function f([int] x) => int
requires |x| > 0:
    z = |x|
    debug Any.toString(z) ++ "\n"
    debug Any.toString(x[z - 1]) ++ "\n"
    return z

method main(System.Console sys) => void:
    arr = [1, 2, 3]
    sys.out.println(f(arr))
