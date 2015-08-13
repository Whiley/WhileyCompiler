

function f(int[] x) -> int:
    return |x|

public export method test() -> void:
    int[][] arr = [[1, 2, 3]]
    assume f(arr[0]) == 3
