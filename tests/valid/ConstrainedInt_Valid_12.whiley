

type cr1nat is int

function f(cr1nat x) -> int:
    int y = x
    return y

public export method test() -> void:
    assume f(9) == 9
