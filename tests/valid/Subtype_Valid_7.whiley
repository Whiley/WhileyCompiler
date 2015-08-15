public export method test() -> void:
    int[] x = [1,2]
    x[0] = 2
    assert x == [2,2]
