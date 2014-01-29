
function f([int] x, int i) => void
requires |x| > 0:
    if (i < 0) || (i >= |x|):
        i = 1
    int y = x[i]
    int z = x[i]
    assert y == z
    debug Any.toString(y)
    debug Any.toString(z)

method main(System.Console sys) => void:
    [int] arr = [1, 2, 3]
    f(arr, 1)
    debug Any.toString(arr)
    f(arr, 2)
    debug Any.toString(arr)
    arr = [123]
    f(arr, 3)
    debug Any.toString(arr)
    arr = [123, 22, 2]
    f(arr, -1)
    debug Any.toString(arr)
    f(arr, 4)
