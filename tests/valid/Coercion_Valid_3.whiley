

function f(int x) -> int:
    return (int) x

public export method test() -> void:
    assume f('H') == 72
