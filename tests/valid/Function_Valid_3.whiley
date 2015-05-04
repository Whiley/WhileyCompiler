

type fr3nat is int

function f(int x) -> int:
    return x

public export method test() -> void:
    int y = 234987234987234982304980130982398723
    assume f(y) == y
