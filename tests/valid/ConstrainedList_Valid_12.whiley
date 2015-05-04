

function f(int x) -> [int]:
    return [x]

public export method test() -> void:
    assume f(0) == [0]
