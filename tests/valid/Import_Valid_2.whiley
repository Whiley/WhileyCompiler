

function f([int] x) -> int
requires |x| > 0:
    return x[0]

public export method test() -> void:
    assume f("1") == '1'
