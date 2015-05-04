

function f(int x) -> (int, int):
    return (x, x + 2)

public export method test() -> void:
    int x, int y = f(1)
    assume x == 1
    assume y == 3
