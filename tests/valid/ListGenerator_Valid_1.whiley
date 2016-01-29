

function f(int[] x) -> int
// Input list cannot be empty
requires |x| > 0:
    //
    int z = |x|
    return x[z-1]

public export method test() :
    int[] arr = [1, 2, 3]
    assume f(arr) == 3
