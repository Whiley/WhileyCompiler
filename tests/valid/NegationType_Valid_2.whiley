

function f(int x) -> !null:
    return x

public export method test() -> void:
    assume f(1) == 1
