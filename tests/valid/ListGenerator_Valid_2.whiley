

method f(int[] x) -> void:
    int z = |x|
    assume x[z - 1] == 3

public export method test() -> void:
    int[] arr = [1, 2, 3]
    f(arr)
