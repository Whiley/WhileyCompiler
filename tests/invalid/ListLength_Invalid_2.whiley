
method main(System.Console sys) -> void:
    [int] arr
    //
    if |sys.args| > 0:
        arr = [1, 2, 4]
    else:
        arr = [1, 2, 3]
    assert arr[0] < |arr|
    assert arr[1] < |arr|
    assert arr[2] != |arr|
    debug Any.toString(arr[0])
