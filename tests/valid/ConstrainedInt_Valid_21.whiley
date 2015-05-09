

function f(int x) -> (int r)
requires x != 0
ensures r != 1:
    //
    return x + 1

public export method test() -> void:
    assume f(9) == 10
