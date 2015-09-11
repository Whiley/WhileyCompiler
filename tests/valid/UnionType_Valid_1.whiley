

public export method test() -> void:
    int|int[] x
    //
    if 0 == 1:
        x = 1
    else:
        x = [1, 2, 3]
    //
    assume x == [1,2,3]
