

function f(int[][] x) -> int
// Input list cannot be empty
requires |x| > 0:
    //
    if |x[0]| > 2:
        return x[0][1]
    else:
        return 0

public export method test() :
    int[][] arr = [[1, 2, 3], [1]]
    assume f(arr) == 2
