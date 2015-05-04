

function f(int i) -> real:
    return (real) i

public export method test() -> void:
    assume f(1) == 1.0
