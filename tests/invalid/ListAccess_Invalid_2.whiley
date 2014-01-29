
function f([int] x) => void
requires |x| > 0:
    int y = x[0]
    int z = x[-1]
    assert y == z

method main(System.Console sys) => void:
    [int] arr = [1, 2, 3]
    f(arr)
    debug Any.toString(arr[0])
