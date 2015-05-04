

function f([int] x) -> int:
    return x[0]

public export method test() -> void:
    assume f("1") == '1'
