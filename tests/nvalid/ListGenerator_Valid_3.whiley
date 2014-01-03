import println from whiley.lang.System

function f([[int]] x) => string
requires |x| > 0:
    if |x[0]| > 2:
        return Any.toString(x[0][1])
    else:
        return ""

method main(System.Console sys) => void:
    arr = [[1, 2, 3], [1]]
    sys.out.println(f(arr))
