

type fr2nat is int

function f(fr2nat x) -> int:
    return x

public export method test() -> void:
    int y = 1
    assume f(y) == 1
