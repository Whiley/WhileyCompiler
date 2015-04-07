import whiley.lang.*

function f([int] a) -> [int]
// Input list cannot be empty
requires |a| > 0:
    //
    a[0] = 5
    return a

method main(System.Console sys) -> void:
    [int] b = [1, 2, 3]
    sys.out.println(f(b))
