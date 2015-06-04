

function f(int x) -> (int r)
requires x >= 0
ensures r >= 0 && x >= 0:
    return x

public export method test() -> void:
    assume f(10) == 10
