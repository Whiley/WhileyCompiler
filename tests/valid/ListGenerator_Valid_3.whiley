import whiley.lang.System

function f([[int]] x) => string
// Input list cannot be empty
requires |x| > 0:
    //
    if |x[0]| > 2:
        return Any.toString(x[0][1])
    else:
        return ""

method main(System.Console sys) => void:
    [[int]] arr = [[1, 2, 3], [1]]
    sys.out.println(f(arr))
