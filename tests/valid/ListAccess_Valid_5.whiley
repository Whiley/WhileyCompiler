

function f(int[] x) -> int:
    return |x|

public export method test() :
    int[][] arr = [[1, 2, 3]]
    assume f(arr[0]) == 3
