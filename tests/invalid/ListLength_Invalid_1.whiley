
method main(System.Console sys) => void:
    [int] arr
    if |sys.args| > 0:
        arr = [1, 2]
    else:
        arr = [1, 2, 3]
    assert |arr| == 4
    debug Any.toString(arr[0])
