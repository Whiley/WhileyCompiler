import whiley.lang.*

function f([[int]] x) -> int
// Input list cannot be empty
requires |x| > 0:
    //
    if |x[0]| > 2:
        return x[0][1]
    else:
        return 0

method main(System.Console sys) -> void:
    [[int]] arr = [[1, 2, 3], [1]]
    sys.out.println(f(arr))
