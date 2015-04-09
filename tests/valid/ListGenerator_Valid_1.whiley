import whiley.lang.*

function f([int] x) -> int
// Input list cannot be empty
requires |x| > 0:
    //
    int z = |x|
    return x[z-1]

method main(System.Console sys) -> void:
    [int] arr = [1, 2, 3]
    assume f(arr) == 3
