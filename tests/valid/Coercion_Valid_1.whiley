

function f(int x) -> real:
    return (real) x

public export method test() -> void:
    assume f(123) == 123.0
