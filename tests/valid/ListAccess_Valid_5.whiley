import whiley.lang.System

function f([int] x) => string:
    return Any.toString(|x|)

method main(System.Console sys) => void:
    [[int]] arr = [[1, 2, 3]]
    sys.out.println(f(arr[0]))
