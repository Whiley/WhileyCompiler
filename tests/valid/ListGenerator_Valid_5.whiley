

function f(int[] x) -> int:
    return |x|

public export method test() -> void:
    int[] arr = [0;0]
    assume f(arr) == 0
