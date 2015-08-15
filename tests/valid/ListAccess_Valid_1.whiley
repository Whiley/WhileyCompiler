

function f(int[] x) -> void
requires |x| > 0:
    int y = x[0]
    int z = x[0]
    assert y == z

public export method test() -> void:
    int[] arr = [1, 2, 3]
    f(arr)
    assert arr[0] == 1
