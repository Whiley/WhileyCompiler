

function f(int x, int y) -> (int, int)
requires x == (2 * y):
    x = x + 2
    y = y + 1
    assert (2 * y) == x
    return (x, y)

public export method test() -> void:
    assume f(2, 1) == (4,2)
