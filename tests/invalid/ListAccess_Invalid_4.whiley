
function f([int] x) => void:
    y = x[0]
    z = x[1]
    assert y == z

method main(System.Console sys) => void:
    arr = [1, 2, 3]
    f(arr)
