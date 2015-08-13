

type T is int[] | int

function f(T x) -> int:
    if x is int[]:
        return |x|
    else:
        return x

public export method test() -> void:
    assume f([1, 2, 3, 4]) == 4
    assume f(123) == 123
