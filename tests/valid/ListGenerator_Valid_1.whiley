import whiley.lang.*

function f([int] x) -> int
// Input list cannot be empty
requires |x| > 0:
    //
    int z = |x|
    debug Any.toString(z) ++ "\n"
    debug Any.toString(x[z - 1]) ++ "\n"
    return z

method main(System.Console sys) -> void:
    [int] arr = [1, 2, 3]
    sys.out.println(f(arr))
