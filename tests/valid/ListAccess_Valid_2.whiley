import println from whiley.lang.System

function f([int] x) => void:
    y = x[0]
    z = x[0]
    assert y == z

method main(System.Console sys) => void:
    arr = [1, 2, 3]
    f(arr)
    sys.out.println(Any.toString(arr[0]))
